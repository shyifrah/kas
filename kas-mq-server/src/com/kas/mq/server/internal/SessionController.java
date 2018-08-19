package com.kas.mq.server.internal;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import com.kas.comm.impl.NetworkAddress;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.UniqueId;
import com.kas.infra.base.threads.ThreadPool;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.MqConfiguration;
import com.kas.mq.server.ServerRepository;

/**
 * A {@link SessionController} is the object that supervises and manage all {@link SessionHandler}.
 * 
 * @author Pippo
 */
public class SessionController extends AKasObject implements IController
{
  /**
   * Loggers
   */
  private ILogger mLogger;
  private ILogger mConsole;
  
  /**
   * A map of client id to client handler
   */
  private Map<UniqueId, SessionHandler> mHandlers;
  
  /**
   * KAS/MQ configuration
   */
  private MqConfiguration mConfig;
  
  /**
   * KAS/MQ configuration
   */
  private ServerRepository mRepository;
  
  /**
   * Constructs a ClientController which is basically the object that supervises active clients
   */
  public SessionController(MqConfiguration config, ServerRepository repository)
  {
    mLogger  = LoggerFactory.getLogger(this.getClass());
    mConsole = LoggerFactory.getStdout(this.getClass());
    mHandlers = new HashMap<UniqueId, SessionHandler>();
    mConfig = config;
    mRepository = repository;
  }
  
  /**
   * Create a new client handler.<br>
   * <br>
   * This method actually creates a new {@link SessionHandler} object that will handle incoming traffic
   * from the {@code socket} and responds, if needed, over it.<br> 
   * Once created, the {@link SessionHandler} is then sent for execution on a different thread.
   * 
   * @param socket the client socket
   * @throws IOException if creation of new {@link SessionHandler} throws
   */
  public void newSession(Socket socket) throws IOException
  {
    mLogger.trace("About to spawn a new ClientHandler for socket: " + socket.getRemoteSocketAddress().toString());
    
    SessionHandler handler = new SessionHandler(socket, this);
    UniqueId id = handler.getClientId();
    mHandlers.put(id, handler);
    
    String remoteAddress = new NetworkAddress(socket).toString();
    mConsole.info("New connection accepted from " + remoteAddress);
    mLogger.trace("Client at " + remoteAddress + " (ID=" + id + ") was sent for execution");
    ThreadPool.execute(handler);
  }
  
  /**
   * Get the controller's MQ configuration
   * 
   * @return the controller's MQ configuration
   * 
   * @see com.kas.mq.server.internal.IController#getConfig()
   */
  public MqConfiguration getConfig()
  {
    return mConfig;
  }
  
  /**
   * Get the server's repository
   * 
   * @return the server's repository
   * 
   * @see com.kas.mq.server.internal.IController#getRepository()
   */
  public ServerRepository getRepository()
  {
    return mRepository;
  }
  
  /**
   * Get handlers map
   * 
   * @return the handlers map
   * 
   * @see com.kas.mq.server.internal.IController#getHandlers()
   */
  public Map<UniqueId, SessionHandler> getHandlers()
  {
    return mHandlers;
  }

  /**
   * Get the object's detailed string representation
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
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
