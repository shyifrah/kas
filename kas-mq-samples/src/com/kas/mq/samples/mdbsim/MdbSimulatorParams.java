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
    mUserName  = getStrArg("username", null);
    mPassword  = getStrArg("password", null);
    mHost      = getStrArg("host", "localhost");
    mPort      = getIntArg("port", 14560);
    mCreateResources = getBoolArg("create.res", false);
    mRequestsQueue = getStrArg("req.queuename", "mdb.req.queue");
    mRepliesQueue  = getStrArg("rep.queuename", "mdb.rep.queue");
  }
}
