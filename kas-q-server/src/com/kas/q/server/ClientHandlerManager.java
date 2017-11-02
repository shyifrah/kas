package com.kas.q.server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import com.kas.infra.base.ThreadPool;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.server.internal.IHandlerCallback;

public class ClientHandlerManager implements IHandlerCallback
{
  private ILogger mLogger;
  private List<ClientHandler> mHandlers;
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public ClientHandlerManager()
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mHandlers = new ArrayList<ClientHandler>();
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
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
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public synchronized void onHandlerStart(ClientHandler handler)
  {
    mLogger.debug("ClientHandlerManager::onHandlerStart() - IN");
    
    mHandlers.add(handler);
    
    mLogger.debug("ClientHandlerManager::onHandlerStart() - OUT");
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public synchronized void onHandlerStop(ClientHandler handler)
  {
    mLogger.debug("ClientHandlerManager::onHandlerStop() - IN");
    
    mHandlers.remove(handler);
    
    mLogger.debug("ClientHandlerManager::onHandlerStop() - OUT");
  }
}
