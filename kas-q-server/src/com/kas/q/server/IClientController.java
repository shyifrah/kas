package com.kas.q.server;

import com.kas.infra.base.IObject;
import com.kas.q.server.internal.ClientHandler;

public interface IClientController extends IObject
{
  /***************************************************************************************************************
   * This callback is called whenever a {@code ClientHandler} is started.
   * 
   * @param handler the {@code ClientHandler} that was started.
   */
  public void onHandlerStart(ClientHandler handler);
  
  /***************************************************************************************************************
   * This callback is called whenever a {@code ClientHandler} is stopped.
   * 
   * @param handler the {@code ClientHandler} that was stopped.
   */
  public void onHandlerStop(ClientHandler handler);
  
  /***************************************************************************************************************
   * This callback is called when a request to shutdown was received by a ClientHandler. 
   */
  public void onShutdownRequest();
}
