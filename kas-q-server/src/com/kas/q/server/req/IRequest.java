package com.kas.q.server.req;

import com.kas.infra.base.IObject;

public interface IRequest extends IObject
{
  /***************************************************************************************************************
   * Get request type
   * 
   * @return the request type which identifies the specific request
   */
  public abstract ERequestType getRequestType();
  
  /***************************************************************************************************************
   * A short version of the {@link com.kas.infra.base.IObject#toPrintableString(int) IObject#toPrintableString(int)}
   * 
   * @return a string representation of the request
   */
  public abstract String toString();
}
