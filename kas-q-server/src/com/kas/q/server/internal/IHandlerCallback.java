package com.kas.q.server.internal;

import com.kas.infra.base.IObject;
import com.kas.q.server.ClientHandler;

public interface IHandlerCallback extends IObject
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
}
