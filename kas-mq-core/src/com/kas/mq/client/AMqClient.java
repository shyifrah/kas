package com.kas.mq.client;

import com.kas.infra.base.AKasObject;

/**
 * Abstract client implementation that actually carries out the requests made by the facade client.
 * 
 * @author Pippo
 */
public abstract class AMqClient extends AKasObject implements IClient
{
  /**
   * The response from last {@link IClient} call
   */
  protected String mResponse;
  
  /**
   * Get last response from last {@link IClient} call.
   * 
   * @return the last message the {@link IClient} issued for a call.
   * 
   * @see com.kas.mq.client.IClient#getResponse()
   */
  public String getResponse()
  {
    return mResponse;
  }
  
  /**
   * Set response from last {@link IClient} call.
   * 
   * @param response The response from last {@link IClient} call
   * 
   * @see com.kas.mq.client.IClient#getResponse()
   */
  public void setResponse(String response)
  {
    mResponse = response;
  }
}
