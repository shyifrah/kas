package com.kas.q.requests;

import com.kas.infra.base.IObject;
import com.kas.q.ext.IKasqMessage;

public interface IRequest extends IObject
{
  /***************************************************************************************************************
   * Get request type
   * 
   * @return the request type which identifies the specific request
   */
  public abstract ERequestType getRequestType();
  
  /***************************************************************************************************************
   * Create a request message
   * 
   * @return the request message
   */
  public abstract IKasqMessage createRequestMessage();
  
  /***************************************************************************************************************
   * A short version of the {@link com.kas.infra.base.IObject#toPrintableString(int) IObject#toPrintableString(int)}
   * 
   * @return a string representation of the request
   */
  public abstract String toString();
}
