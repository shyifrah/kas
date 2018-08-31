package com.kas.mq.samples.clientapp;

import com.kas.infra.base.KasException;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.MqContext;

class ConsumerThread extends Thread
{
  static final long cConsumerPollingInterval = 1000L;
  static final long cConsumerGetTimeout      = 5000L;
  
  private ILogger mLogger;
  private MqContext mContext;
  private int mThreadIndex;
  private ClientAppParams mParams;
  
  private String mQueueName;
  
  ConsumerThread(int tix, ClientAppParams params)
  {
    super(ConsumerThread.class.getSimpleName() + tix);
    mContext = new MqContext();
    mThreadIndex = tix;
    mParams = params;
    mQueueName = mParams.mQueueName;
    mLogger = LoggerFactory.getLogger(this.getClass());
  }
  
  public void run()
  {
    mLogger.debug("ConsumerThread::run() - IN, TIX=" + mThreadIndex + ", Queue=" + mQueueName);
    
    try
    {
      mLogger.debug("ConsumerThread::run() - Connecting to " + mParams.mHost + ':' + mParams.mPort);
      mContext.connect(mParams.mHost, mParams.mPort, mParams.mUserName, mParams.mPassword);
    }
    catch (KasException e) {}
    
    mLogger.debug("ConsumerThread::run() - Starting actual work...");
    int total = 0;
    IMqMessage<?> getMessage = mContext.get(mQueueName, cConsumerGetTimeout, cConsumerPollingInterval);
    while (getMessage != null)
    {
      if ((total%100==0) && (total != 0))
        System.out.println(String.format("[C%d] ... %d", mThreadIndex, total));
      ++total;
      getMessage = mContext.get(mQueueName, cConsumerGetTimeout, cConsumerPollingInterval);
    }
    
    try
    {
      mLogger.debug("ConsumerThread::run() - Disconnecting from remote host");
      mContext.disconnect();
    }
    catch (KasException e) {}
    mLogger.debug("ConsumerThread::run() - OUT");
  }
}