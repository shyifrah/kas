package com.kas.q.server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import com.kas.infra.base.KasObject;
import com.kas.infra.base.ThreadPool;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.server.internal.IHandlerCallback;

public class ClientHandlerManager extends KasObject implements IHandlerCallback
{
  private ILogger mLogger;
  private List<ClientHandler> mHandlers;
  
  /***************************************************************************************************************
   * Constructs a ClientHandlerManager which is basically a list of all active ClientHandler objects.
   */
  public ClientHandlerManager()
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mHandlers = new ArrayList<ClientHandler>();
  }
  
  /***************************************************************************************************************
   * Handle the processing of a new client.
   * This method actually creates a new ClientHandler object that will handle all incoming traffic from
   * the specified socket.
   * The ClientHandler is then sent for execution on a different thread.
   * 
   * @param socket the client socket
   */
  public void newClient(Socket socket)
  {
    try
    {
      ClientHandler handler = new ClientHandler(socket, this);
      ThreadPool.submit(handler);
    }
    catch (IOException e)
    {
      mLogger.trace("Failed to create ClientHandler for " + socket.toString() + ". Exception caught: ", e);
    }
  }
  
  public synchronized void onHandlerStart(ClientHandler handler)
  {
    mLogger.debug("ClientHandlerManager::onHandlerStart() - IN");
    
    mHandlers.add(handler);
    
    mLogger.debug("ClientHandlerManager::onHandlerStart() - OUT");
  }
  
  public synchronized void onHandlerStop(ClientHandler handler)
  {
    mLogger.debug("ClientHandlerManager::onHandlerStop() - IN");
    
    mHandlers.remove(handler);
    
    mLogger.debug("ClientHandlerManager::onHandlerStop() - OUT");
  }

  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    
    sb.append(name()).append("(\n")
      .append(pad).append("  HandlerList=(").append(StringUtils.asPrintableString(mHandlers, level+2)).append(")\n")
      .append(pad).append(")");
    
    return sb.toString();
  }
}
