package com.kas.q.server;

import java.io.IOException;
import com.kas.infra.base.IObject;
import com.kas.q.ext.IKasqMessage;

public interface IClientHandler extends IObject, Runnable
{
  /***************************************************************************************************************
   * Send a message to client 
   * 
   * @param message the {@code IKasqMessage} to be processed.
   * 
   * @throws IOException if the Messenger throws an exception 
   */
  public abstract void send(IKasqMessage message) throws IOException;
  
  /***************************************************************************************************************
   * Returns the authentication state of this handler 
   * 
   * @returns true if this handler was successfully authenticated, false otherwise 
   */
  public abstract boolean isAuthenticated();
}
