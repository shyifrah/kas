package com.kas.mq.samples;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kas.infra.base.KasException;
import com.kas.mq.impl.MqContext;

public abstract class GenThread extends Thread
{
  protected Logger mLogger;
  protected MqContext mContext;
  protected ParamsContainer mParams;
  
  public GenThread(String name, ParamsContainer params)
  {
    super(name);
    mContext = new MqContext(name);
    mParams = params;
    mLogger = LogManager.getLogger(getClass());
  }
  
  public void run()
  {
    mLogger.trace("GenThread::run() - IN");
    
    boolean success = false;
    
    try
    {
      mLogger.trace("ConsumerThread::run() - Connecting to " + mParams.mHost + ':' + mParams.mPort);
      mContext.connect(mParams.mHost, mParams.mPort, mParams.mUserName, mParams.mPassword);
      success = true;
    }
    catch (KasException e) {}
    
    if (!success)
    {
      mLogger.trace("GenThread::run() - Failed to connect to remote server. Response: " + mContext.getResponse());
    }
    else
    {
      mLogger.trace("GenThread::run() - Starting actual work...");
      work();
    }
    
    try
    {
      mLogger.trace("GenThread::run() - Disconnecting from remote host");
      mContext.disconnect();
    }
    catch (KasException e) {}
    mLogger.trace("GenThread::run() - OUT");
  }
  
  public abstract void work();
}