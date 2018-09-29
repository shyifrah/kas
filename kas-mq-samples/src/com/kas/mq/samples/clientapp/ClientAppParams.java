package com.kas.mq.samples.clientapp;

import java.util.Map;
import com.kas.mq.samples.ParamsContainer;

public class ClientAppParams extends ParamsContainer
{
  private static final long serialVersionUID = 1L;
  
  public String mProdQueueName;
  public String mConsQueueName;
  public int    mTotalProducers;
  public int    mTotalConsumers;
  public int    mTotalMessages;
  public int    mMessageType;
  
  ClientAppParams(Map<String,String> map)
  {
    super(map, "client.app.");
    mUserName  = getStrArg("username", null);                            // username to identify to KAS/MQ
    mPassword  = getStrArg("password", null);                            // password for username
    mHost      = getStrArg("host", "localhost");                         // KAS/MQ server host name / ip address 
    mPort      = getIntArg("port", 14560);                               // KAS/MQ server listenning port
    mCreateResources = getBoolArg("create.res", false);                  // should the app create the queues?
    mProdQueueName = getStrArg("put.queuename", "client.app.putqueue");  // the name of the queue to put messages into
    mConsQueueName = getStrArg("get.queuename", "client.app.getqueue");  // the name of the queue to get messages which
    mTotalProducers = getIntArg("total.producers", 2);                   // number of threads that put messages
    mTotalConsumers = getIntArg("total.consumers", 2);                   // number of threads that get messages
    mTotalMessages  = getIntArg("total.messages", 10000);                // total number of messages to put
    mMessageType    = getIntArg("message.type", 1);                      // the type of messages to send (0-No-body, 1-String, 2-Object, 3-Bytes)
  }
  
  public String toString()
  {
    return "ClientAppParams";
  }
}
