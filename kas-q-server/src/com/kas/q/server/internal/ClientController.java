package com.kas.q.server.internal;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.ThreadPool;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;

public class ClientController extends AKasObject implements IController
{
  private ILogger mLogger;
  private List<ClientHandler> mHandlers;
  private ServerSocket mServerSocket;
  
  /***************************************************************************************************************
   * Constructs a ClientController which is basically the object that supervises active clients
   */
  public ClientController(ServerSocket socket)
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mHandlers = new ArrayList<ClientHandler>();
    mServerSocket = socket;
  }
  
  /***************************************************************************************************************
   * Handle the processing of a new client.
   * This method actually creates a new {@code ClientHandler} object that will handle all incoming traffic from
   * the specified socket.
   * The {@code ClientHandler} is then sent for execution on a different thread.
   * 
   * @param socket the client socket
   */
  public void startClient(Socket socket)
  {
    try
    {
      ClientHandler handler = new ClientHandler(socket, this);
      ThreadPool.execute(handler);
    }
    catch (IOException e)
    {
      mLogger.trace("Failed to create ClientHandler for " + socket.toString() + ". Exception caught: ", e);
    }
  }
  
  /***************************************************************************************************************
   * 
   */
  public synchronized void onHandlerStart(ClientHandler handler)
  {
    mLogger.debug("ClientController::onHandlerStart() - IN");
    
    mHandlers.add(handler);
    
    mLogger.debug("ClientController::onHandlerStart() - OUT");
  }
  
  /***************************************************************************************************************
   * 
   */
  public synchronized void onHandlerStop(ClientHandler handler)
  {
    mLogger.debug("ClientController::onHandlerStop() - IN");
    
    mHandlers.remove(handler);
    
    mLogger.debug("ClientController::onHandlerStop() - OUT");
  }
  
  /***************************************************************************************************************
   * 
   */
  public void onShutdownRequest()
  {
    mLogger.debug("ClientController::onShutdownRequest() - IN");
    
    try
    {
      mServerSocket.close();
    }
    catch (Throwable e) {}
    
    mLogger.debug("ClientController::onShutdownRequest() - OUT");
  }
  
  /***************************************************************************************************************
   * 
   */
  public void closeAll()
  {
    mLogger.debug("ClientController::closeAll() - IN");
    
    for (ClientHandler handler : mHandlers)
    {
      handler.term();
    }
    
    mLogger.debug("ClientController::closeAll() - OUT");
  }

  /***************************************************************************************************************
   * 
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    sb.append(name()).append("(\n")
      .append(pad).append("  HandlerList=(").append(StringUtils.asPrintableString(mHandlers, level+1)).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}