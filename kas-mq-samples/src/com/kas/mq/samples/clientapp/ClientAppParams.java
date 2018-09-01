package com.kas.mq.samples.clientapp;

import java.util.Map;
import com.kas.mq.samples.ParamsContainer;

public class ClientAppParams extends ParamsContainer
{
  public String mProdQueueName;
  public String mConsQueueName;
  public int    mTotalProducers;
  public int    mTotalConsumers;
  public int    mTotalMessages;
  
  ClientAppParams(Map<String,String> map)
  {
    super(map, "client.app.");
    mUserName  = getStrArg("username", null);
    mPassword  = getStrArg("password", null);
    mHost      = getStrArg("host", "localhost");
    mPort      = getIntArg("host", 14560);
    mCreateResources = getBoolArg("create.res", false);
    mProdQueueName = getStrArg("put.queuename", "client.app.putqueue");
    mConsQueueName = getStrArg("get.queuename", "client.app.getqueue");
    mTotalProducers = getIntArg("total.producers", 2);
    mTotalConsumers = getIntArg("total.consumers", 2);
    mTotalMessages  = getIntArg("total.messages", 10000);
  }
}
