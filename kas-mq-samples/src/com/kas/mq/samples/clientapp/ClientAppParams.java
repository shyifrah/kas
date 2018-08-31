package com.kas.mq.samples.clientapp;

import java.util.Map;

public class ClientAppParams
{
  private Map<String,String> mMap;
  
  public String mUserName;
  public String mPassword;
  public String mHost;
  public int    mPort;
  public String mQueueName;
  public int    mTotalProducers;
  public int    mTotalConsumers;
  public int    mTotalMessages;
  
  ClientAppParams(Map<String,String> map)
  {
    mMap = map;
    mUserName  = getStrArg("client.app.username", null);
    mPassword  = getStrArg("client.app.password", null);
    mHost      = getStrArg("client.app.host", "localhost");
    mPort      = getIntArg("client.app.host", 14560);
    mQueueName = getStrArg("client.app.queuename", "client.app.queue");
    mTotalProducers = getIntArg("client.app.total.producers", 2);
    mTotalConsumers = getIntArg("client.app.total.consumers", 2);
    mTotalMessages  = getIntArg("client.app.total.messages", 10000);
  }
  
  /**
   * Get int argument value
   * @param key
   * @param defval
   * @return the value
   */
  private int getIntArg(String key, int defval)
  {
    String strval = mMap.get(key);
    int result = defval;
    if (strval != null)
    {
      try
      {
        int temp = Integer.valueOf(strval);
        result = temp;
      }
      catch (NumberFormatException e) {}
    }
    return result;
  }
  
  /**
   * Get string argument value
   * @param key
   * @param defval
   * @return the value
   */
  private String getStrArg(String key, String defval)
  {
    String strval = mMap.get(key);
    String result = defval;
    if (strval != null)
    {
      result = strval;
    }
    return result;
  }
}
