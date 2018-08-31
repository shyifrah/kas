package com.kas.mq.samples.clientapp;

import com.kas.infra.base.KasException;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.impl.MqContext;
import com.kas.mq.impl.MqMessageFactory;
import com.kas.mq.impl.MqTextMessage;

class ProducerThread extends Thread
{
  private ILogger mLogger;
  private MqContext mContext;
  private int mThreadIndex;
  private ClientAppParams mParams;
  
  private int mTotalMessages;
  private String mQueueName;
  
  ProducerThread(int tix, ClientAppParams params)
  {
    super(ProducerThread.class.getSimpleName() + tix);
    mContext = new MqContext();
    mThreadIndex = tix;
    mParams = params;
    mTotalMessages = mParams.mTotalMessages / mParams.mTotalProducers;
    mQueueName = mParams.mQueueName;
    mLogger = LoggerFactory.getLogger(this.getClass());
  }
  
  public void run()
  {
    mLogger.debug("ProducerThread::run() - IN, TIX=" + mThreadIndex + ", Queue=" + mQueueName + ", TotalMessages=" + mTotalMessages);
    
    try
    {
      mLogger.debug("ProducerThread::run() - Connecting to " + mParams.mHost + ':' + mParams.mPort);
      mContext.connect(mParams.mHost, mParams.mPort, mParams.mUserName, mParams.mPassword);
    }
    catch (KasException e) {}
    
    mLogger.debug("ProducerThread::run() - Starting actual work...");
    for (int i = 0; i < mTotalMessages; ++i)
    {
      String messageBody = "message number: " + (i + 1);
      MqTextMessage putMessage = MqMessageFactory.createTextMessage(messageBody);
      putMessage.setPriority(i%10);
      mContext.put(mQueueName, putMessage);
      
      if (i%100==0)
        System.out.println(String.format("[P%d] ... %d", mThreadIndex, i));
    }
    
    try
    {
      mLogger.debug("ProducerThread::run() - Disconnecting from remote host");
      mContext.disconnect();
    }
    catch (KasException e) {}
    
    mLogger.debug("ProducerThread::run() - OUT");
  }
}