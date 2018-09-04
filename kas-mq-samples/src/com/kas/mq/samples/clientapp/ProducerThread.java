package com.kas.mq.samples.clientapp;

import com.kas.logging.LoggerFactory;
import com.kas.mq.impl.MqMessageFactory;
import com.kas.mq.impl.MqTextMessage;
import com.kas.mq.samples.GenThread;

class ProducerThread extends GenThread
{
  private int mThreadIndex;
  private ClientAppParams mParams;
  
  private int mTotalMessages;
  private String mQueueName;
  
  ProducerThread(int tix, ClientAppParams params)
  {
    super(ProducerThread.class.getSimpleName() + tix, params);
    mLogger = LoggerFactory.getLogger(this.getClass());
    mThreadIndex = tix;
    mParams = params;
    mTotalMessages = mParams.mTotalMessages / mParams.mTotalProducers;
    mQueueName = mParams.mProdQueueName;
  }
  
  public void work()
  {
    mLogger.debug("ProducerThread::work() - IN");
  
    mLogger.debug("ProducerThread::work() - Starting actual work...");
    for (int i = 0; i < mTotalMessages; ++i)
    {
      String messageBody = "message number: " + (i + 1);
      MqTextMessage putMessage = MqMessageFactory.createTextMessage(messageBody);
      putMessage.setPriority(i%10);
      mContext.put(mQueueName, putMessage);
      
      if (i%100==0)
        System.out.println(String.format("[P%d] ... %d", mThreadIndex, i));
    }
    
    mLogger.debug("ProducerThread::work() - OUT");
  }
}