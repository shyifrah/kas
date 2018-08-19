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
import com.kas.mq.impl.MqQueue;
import com.kas.mq.server.ServerRepository;

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
  private ILogger mConsole;
  
  /**
   * A map of client id to client handler
   */
  private Map<UniqueId, ClientHandler> mHandlers;
  
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
  public ClientController(MqConfiguration config, ServerRepository repository)
  {
    mLogger  = LoggerFactory.getLogger(this.getClass());
    mConsole = LoggerFactory.getStdout(this.getClass());
    mHandlers = new HashMap<UniqueId, ClientHandler>();
    mConfig = config;
    mRepository = repository;
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
   * Get indication if a user's password matches the one defined in the {@link MqConfiguration}
   * 
   * @param user The user's name
   * @param pass The user's password
   * @return {@code true} if the user's password matches the one defined in the {@link MqConfiguration}, {@code false} otherwise
   * 
   * @see com.kas.mq.server.internal.IController#isPasswordMatch(String, String)
   */
  public boolean isPasswordMatch(String user, String pass)
  {
    String confPass = mConfig.getUserPassword(user);
    if (confPass == null)
    {
      if (user == null)
        return true;
      else
        return false;
    }
    
    return confPass.equals(pass);
  }
  
  /**
   * Get handlers map
   * 
   * @return the handlers map
   * 
   * @see com.kas.mq.server.internal.IController#getHandlers()
   */
  public Map<UniqueId, ClientHandler> getHandlers()
  {
    return mHandlers;
  }

  /**
   * Get queue by name
   * 
   * @param queue The queue name
   * @return the {@link MqQueue} object associated with the specified queue name
   * 
   * @see com.kas.mq.server.internal.IController#getQueue(String)
   */
  public MqQueue getQueue(String name)
  {
    return mRepository.getQueue(name);
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
