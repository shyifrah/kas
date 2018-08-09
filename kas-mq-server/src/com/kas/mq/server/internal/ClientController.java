package com.kas.mq.server.internal;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.UniqueId;
import com.kas.infra.base.threads.ThreadPool;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;

public class ClientController extends AKasObject
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
   * Constructs a ClientController which is basically the object that supervises active clients
   */
  public ClientController()
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mHandlers = new HashMap<UniqueId, ClientHandler>();
  }
  
  /**
   * Create a new client handler.<br>
   * <br>
   * This method actually creates a new {@link ClientHandler} object that will handle incoming traffic
   * from the {@code socket} and responds, if needed, over it.<br> 
   * Once created, the {@link ClientHandler} is then sent for execution on a different thread.
   * 
   * @param socket the client socket
   */
  public void newClient(Socket socket)
  {
    mLogger.trace("About to spawn a new ClientHandler for socket: " + socket.getRemoteSocketAddress().toString());
    
    ClientHandler handler = new ClientHandler(socket);
    UniqueId id = handler.getClientId();
    mHandlers.put(id, handler);
    
    mLogger.trace("Client at " + socket.getRemoteSocketAddress().toString() + " (ID=" + id + ") was sent for execution");
    ThreadPool.execute(handler);
  }
  
  public AKasObject replicate()
  {
    return null;
  }

  public String toPrintableString(int level)
  {
    return null;
  }
}
