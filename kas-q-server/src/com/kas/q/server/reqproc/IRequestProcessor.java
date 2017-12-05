package com.kas.q.server.reqproc;

import java.io.IOException;
import javax.jms.JMSException;
import com.kas.infra.base.IObject;
import com.kas.q.requests.ERequestType;
import com.kas.q.server.IClientHandler;

public interface IRequestProcessor extends IObject
{
  /***************************************************************************************************************
   * Get request type
   * 
   * @return the request type which identifies the specific request
   */
  public abstract ERequestType getRequestType();
  
  /***************************************************************************************************************
   * Process the specific request.
   * 
   * @param handler the {@code ClientHandler} that received the request
   * 
   * @return true if request was successfully processed, false otherwise.
   *   Note that the value returned is from the server perspective, not the client. For example, a client
   *   requesting for a Shutdown request might get a "false" value on his request, because of lack of permissions
   *   but from the server point of view the request was successfully processed.
   */
  public abstract boolean process(IClientHandler handler) throws IOException, JMSException;
  
  /***************************************************************************************************************
   * A short version of the {@link com.kas.infra.base.IObject#toPrintableString(int) IObject#toPrintableString(int)}
   * 
   * @return a string representation of the request
   */
  public abstract String toString();
}
