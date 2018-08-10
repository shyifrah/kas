package com.kas.mq.server.internal;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.UniqueId;
import com.kas.infra.base.threads.ThreadPool;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.MqConfiguration;

/**
 * A {@link ClientController} is the object that supervises and manage all {@link ClientHandler}.
 * 
 * @author Pippo
 */
public class ClientController extends AKasObject implements IController
{
  /**
   * Logger
   */
  private ILogger mLogger;
  
  /**
   * A map of client id to client handler
   */
  private Map<UniqueId, ClientHandler> mHandlers;
  
  /**
   * KAS/MQ configuration
   */
  private MqConfiguration mConfig;
  
  /**
   * Constructs a ClientController which is basically the object that supervises active clients
   */
  public ClientController(MqConfiguration config)
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mHandlers = new HashMap<UniqueId, ClientHandler>();
    mConfig = config;
  }
  
  /**
   * Create a new client handler.<br>
   * <br>
   * This method actually creates a new {@link ClientHandler} object that will handle incoming traffic
   * from the {@code socket} and responds, if needed, over it.<br> 
   * Once created, the {@link ClientHandler} is then sent for execution on a different thread.
   * 
   * @param socket the client socket
   * @throws IOException if creation of new {@link ClientHandler} throws
   */
  public void newClient(Socket socket) throws IOException
  {
    mLogger.trace("About to spawn a new ClientHandler for socket: " + socket.getRemoteSocketAddress().toString());
    
    ClientHandler handler = new ClientHandler(socket, this);
    UniqueId id = handler.getClientId();
    mHandlers.put(id, handler);
    
    mLogger.trace("Client at " + socket.getRemoteSocketAddress().toString() + " (ID=" + id + ") was sent for execution");
    ThreadPool.execute(handler);
  }
  
  /**
   * Get the controller's MQ configuration
   * 
   * @return the controller's MQ configuration
   */
  public MqConfiguration getConfig()
  {
    return mConfig;
  }
  
  /**
   * Returns a replica of this {@link ClientController}.<br>
   * <br>
   * The replica will have an empty map of handlers.
   * 
   * @return a replica of this {@link ClientController}
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public ClientController replicate()
  {
    return new ClientController(mConfig);
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
