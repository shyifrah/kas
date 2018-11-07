package com.kas.mq.samples.clientapp;

import com.kas.logging.LoggerFactory;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.samples.GenThread;

class ConsumerThread extends GenThread
{
  static final long cConsumerPollingInterval = 1000L;
  static final long cConsumerGetTimeout      = 5000L;
  
  private int mThreadIndex;
  private ClientAppParams mParams;
  
  private String mQueueName;
  
  ConsumerThread(int tix, ClientAppParams params)
  {
    super(ConsumerThread.class.getSimpleName() + tix, params);
    mLogger = LoggerFactory.getLogger(this.getClass());
    mThreadIndex = tix;
    mParams = params;
    mQueueName = mParams.mConsQueueName;
  }
  
  public void work()
  {
    mLogger.debug("ConsumerThread::work() - IN");
    
    mLogger.debug("ConsumerThread::work() - Starting actual work...");
    int total = 0;
    IMqMessage getMessage = mContext.get(mQueueName, cConsumerGetTimeout, cConsumerPollingInterval);
    while (getMessage != null)
    {
      if ((total != 0) && (total % 100 == 0)) System.out.println(String.format("[C%d] ... %d", mThreadIndex, total));
      ++total;
      getMessage = mContext.get(mQueueName, cConsumerGetTimeout, cConsumerPollingInterval);
    }
    
    mLogger.debug("ConsumerThread::work() - OUT");
  }
}