package com.kas.q.server.internal;

import com.kas.q.server.ClientHandler;

public interface IHandlerCallback
{
  public void onHandlerStart(ClientHandler handler);
  public void onHandlerStop(ClientHandler handler);
}
