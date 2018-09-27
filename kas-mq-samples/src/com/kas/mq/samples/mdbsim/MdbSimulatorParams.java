package com.kas.mq.samples.mdbsim;

import java.util.Map;
import com.kas.mq.samples.ParamsContainer;

public class MdbSimulatorParams extends ParamsContainer
{
  public String mRequestsQueue;
  public String mRepliesQueue;
  
  MdbSimulatorParams(Map<String,String> map)
  {
    super(map, "mdb.sim.");
    mUserName  = getStrArg("username", null);                            // username to identify to KAS/MQ
    mPassword  = getStrArg("password", null);                            // password for username
    mHost      = getStrArg("host", "localhost");                         // KAS/MQ server host name / ip address 
    mPort      = getIntArg("port", 14560);                               // KAS/MQ server listenning port
    mCreateResources = getBoolArg("create.res", false);                  // should the app create the queues?
    mRequestsQueue = getStrArg("req.queuename", "mdb.req.queue");        // requests queue
    mRepliesQueue  = getStrArg("rep.queuename", "mdb.rep.queue");        // replies queue
  }
}
