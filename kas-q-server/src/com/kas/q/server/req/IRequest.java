package com.kas.q.server.req;

public interface IRequest
{
  /***************************************************************************************************************
   * Get request type
   * 
   * @return the request type which identifies the specific request
   */
  public abstract ERequestType getRequestType();
}
