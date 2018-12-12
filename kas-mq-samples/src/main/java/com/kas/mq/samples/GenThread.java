package com.kas.mq.samples;

import com.kas.infra.base.KasException;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.impl.MqContext;

public abstract class GenThread extends Thread
{
  protected ILogger mLogger;
  protected MqContext mContext;
  protected ParamsContainer mParams;
  
  public GenThread(String name, ParamsContainer params)
  {
    super(name);
    mContext = new MqContext(name);
    mParams = params;
    mLogger = LoggerFactory.getLogger(this.getClass());
  }
  
  public void run()
  {
    mLogger.debug("GenThread::run() - IN");
    
    boolean success = false;
    
    try
    {
      mLogger.debug("ConsumerThread::run() - Connecting to " + mParams.mHost + ':' + mParams.mPort);
      mContext.connect(mParams.mHost, mParams.mPort, mParams.mUserName, mParams.mPassword);
      success = true;
    }
    catch (KasException e) {}
    
    if (!success)
    {
      mLogger.debug("GenThread::run() - Failed to connect to remote server. Response: " + mContext.getResponse());
    }
    else
    {
      mLogger.debug("GenThread::run() - Starting actual work...");
      work();
    }
    
    try
    {
      mLogger.debug("GenThread::run() - Disconnecting from remote host");
      mContext.disconnect();
    }
    catch (KasException e) {}
    mLogger.debug("GenThread::run() - OUT");
  }
  
  public abstract void work();
}