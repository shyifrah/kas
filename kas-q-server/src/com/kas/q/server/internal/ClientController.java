package com.kas.q.server.internal;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.threads.ThreadPool;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.server.IClientController;

public class ClientController extends AKasObject implements IClientController
{
  private ILogger mLogger;
  private ILogger mConsole;
  private List<ClientHandler> mHandlers;
  private ServerSocket mServerSocket;
  
  /***************************************************************************************************************
   * Constructs a ClientController which is basically the object that supervises active clients
   */
  public ClientController(ServerSocket socket)
  {
    mLogger  = LoggerFactory.getLogger(this.getClass());
    mConsole = LoggerFactory.getStdout(this.getClass());
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
      ClientHandler handler = new ClientHandler(socket);
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
    logInfo("New Handler started: " + handler.toString());
    
    mLogger.debug("ClientController::onHandlerStart() - OUT");
  }
  
  /***************************************************************************************************************
   * 
   */
  public synchronized void onHandlerStop(ClientHandler handler)
  {
    mLogger.debug("ClientController::onHandlerStop() - IN");
    
    mHandlers.remove(handler);
    logInfo("Handler stopped: " + handler.toString());
    
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
      logInfo("KAS/Q server shutdown in progress...");
      mServerSocket.close();
    }
    catch (Throwable e) {}
    
    mLogger.debug("ClientController::onShutdownRequest() - OUT");
  }
  
  /***************************************************************************************************************
   * Terminating all active ClientHandlers
   */
  public void closeAll()
  {
    mLogger.debug("ClientController::closeAll() - IN");
    
    logInfo("Signaling all client handlers to terminate...");
    for (ClientHandler handler : mHandlers)
    {
      handler.term();
    }
    
    mLogger.debug("ClientController::closeAll() - OUT");
  }
  
  /***************************************************************************************************************
   * Logs a message to both Console and Logger
   * 
   * @param message the message to be logged
   */
  private void logInfo(String message)
  {
    mLogger.info(message);
    mConsole.info(message);
  }

  /***************************************************************************************************************
   * 
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    sb.append(name()).append("(\n")
      .append(pad).append("  HandlerList=(\n")
      .append(StringUtils.asPrintableString(mHandlers, level+2)).append("\n")
      .append(pad).append("  )");
    return sb.toString();
  }
}
