package com.kas.mq.server.internal;

import java.io.IOException;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kas.comm.impl.NetworkAddress;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.IObject;
import com.kas.infra.base.UniqueId;
import com.kas.infra.base.threads.ThreadPool;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.server.IController;
import com.kas.mq.server.IMqServer;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.MqConfiguration;

/**
 * A {@link SessionController} is the object that supervises and manage all {@link SessionHandler}.
 * 
 * @author Pippo
 */
public class SessionController extends AKasObject implements IController
{
  /**
   * Logger
   */
  private Logger mLogger;
  private Logger mStdout;
  
  /**
   * A map of session id to session handler
   */
  private ConcurrentHashMap<UniqueId, SessionHandler> mHandlers;
  
  /**
   * KAS/MQ configuration
   */
  private MqConfiguration mConfig;
  
  /**
   * KAS/MQ repository
   */
  private IRepository mRepository;
  
  /**
   * KAS/MQ server
   */
  private IMqServer mServer;
  
  /**
   * Constructs a {@link SessionController} which is basically the object that supervises active sessions
   * 
   * @param config
   *   The {@link IMqServer} object
   */
  public SessionController(IMqServer server)
  {
    mLogger = LogManager.getLogger(getClass());
    mStdout = LogManager.getLogger("stdout");
    mHandlers = new ConcurrentHashMap<UniqueId, SessionHandler>();
    mServer = server;
    mConfig = mServer.getConfig();
    mRepository = mServer.getRepository();
  }
  
  /**
   * Create a new session handler.<br>
   * This method actually creates a new {@link SessionHandler} object that will handle incoming traffic
   * from the {@code socket} and responds, if needed, over it.<br> 
   * Once created, the {@link SessionHandler} is then sent for execution on a different thread.
   * 
   * @param socket
   *   the session's socket
   * @throws IOException
   *   if creation of new {@link SessionHandler} throws
   */
  public void newSession(Socket socket) throws IOException
  {
    mLogger.debug("About to spawn a new SessionHandler for socket: {}", socket.getRemoteSocketAddress());
    
    SessionHandler handler = new SessionHandler(socket, this, mRepository);
    
    String remoteAddress = new NetworkAddress(socket).toString();
    
    logBoth("New connection accepted from {}", remoteAddress);
    ThreadPool.execute(handler);
  }
  
  /**
   * A callback that is invoked under the handler's thread right before
   * the handler starts its run() method.
   * 
   * @param handler
   *   The handler that invoked the callback
   */
  public void onHandlerStart(SessionHandler handler)
  {
    mLogger.trace("SessionController::onHandlerStart() - IN");
    UniqueId id = handler.getSessionId();
    mLogger.debug("Started handler for session ID: {}", id);
    mHandlers.put(id, handler);
    mLogger.trace("SessionController::onHandlerStart() - OUT");
  }
  
  /**
   * A callback that is invoked under the handler's thread right before
   * the handler ends its run() method.
   * 
   * @param handler
   *   The handler that invoked the callback
   */
  public void onHandlerEnd(SessionHandler handler)
  {
    mLogger.trace("SessionController::onHandlerEnd() - IN");
    UniqueId id = handler.getSessionId();
    mLogger.debug("Ending handler for session ID: ", id);
    SessionHandler removed = mHandlers.remove(id);
    mLogger.trace("SessionController::onHandlerEnd() - Removed handler: {}", (removed == null ? "null" : removed.toPrintableString()));
    mLogger.trace("SessionController::onHandlerEnd() - OUT");
  }
  
  /**
   * Get the handler serving session ID with {@code id}
   * 
   * @param id
   *   The {@link UniqueId} of the session
   * @return
   *   the {@link SessionHandler handler} associated with the specified session ID
   */
  public SessionHandler getHandler(UniqueId id)
  {
    mLogger.trace("SessionController::getHandler() - IN, HandlerId={}", id);
    
    SessionHandler handler = null;
    if (id != null)
      handler = mHandlers.get(id);
    
    mLogger.trace("SessionController::getHandler() - OUT");
    return handler;
  }
  
  /**
   * Get all handlers
   * 
   * @return
   *   a collection of all handlers
   */
  public Collection<SessionHandler> getHandlers()
  {
    return mHandlers.values();
  }
  
  /**
   * Get the controller's MQ configuration
   * 
   * @return
   *   the controller's MQ configuration
   */
  public MqConfiguration getConfig()
  {
    return mConfig;
  }
  
  /**
   * Get the server's repository
   * 
   * @return
   *   the server's repository
   */
  public IRepository getRepository()
  {
    return mRepository;
  }
  
  /**
   * Configuration has been refreshed
   */
  public void refresh()
  {
  }
  
  /**
   * Graceful shutdown.<br>
   * First, we signal all handlers to shutdown, so the next time a handler
   * tests if it should shutdown the result will be that it needs to end.<br>
   * Than, we're closing all opened connections, so if a handler depends on one
   * that won't prevent it from from shutting down...<br>
   * Finally, we signal the main server's thread it should stop.
   */
  public void shutdown()
  {
    mLogger.trace("SessionController::shutdown() - IN");
    
    logBoth("KAS/MQ server received a Shutdown request, signaling all handlers to terminate...");
    
    for (Map.Entry<UniqueId, SessionHandler> entry : mHandlers.entrySet())
    {
      UniqueId uid = entry.getKey();
      SessionHandler handler = entry.getValue();
      
      mLogger.debug("Handler {} was signaled to terminate processing...", uid);
      handler.stop();
    }
    
    logBoth("Closing all opened connections...");
    MqServerConnectionPool.getInstance().shutdown();
    
    logBoth("Signaling main thread to terminate...");
    mServer.stop();
    
    mLogger.trace("SessionController::shutdown() - OUT");
  }
  
  /**
   * Issue a message to both the logger and stdout
   * 
   * @param message
   *   The message to issue
   * @param args
   *   Arguments to pass the logger and stdout
   */
  private void logBoth(String message, Object ... args)
  {
    mLogger.info(message, args);
    mStdout.info(message, args);
  }

  /**
   * Returns the {@link IObject} string representation.
   * 
   * @param level
   *   The required padding level
   * @return
   *   the string representation with the specified level of padding
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n")
      .append(pad).append("  Handlers=(").append(StringUtils.asPrintableString(mHandlers, level+2)).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
