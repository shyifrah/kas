package com.kas.mq.samples.clientapp;

import org.apache.logging.log4j.LogManager;
import com.kas.infra.base.KasException;
import com.kas.infra.base.Properties;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.impl.messages.MqMessageFactory;
import com.kas.mq.impl.messages.MqStreamMessage;
import com.kas.mq.samples.GenThread;

class ProducerThread extends GenThread
{
  private int mThreadIndex;
  private ClientAppParams mParams;
  
  private int mTotalMessages;
  private String mQueueName;
  
  ProducerThread(int tix, ClientAppParams params)
  {
    super("SAMPLE-" + ProducerThread.class.getSimpleName() + tix, params);
    mLogger = LogManager.getLogger(getClass());
    mThreadIndex = tix;
    mParams = params;
    mTotalMessages = mParams.mTotalMessages / mParams.mTotalProducers;
    mQueueName = mParams.mProdQueueName;
  }
  
  public void work()
  {
    mLogger.trace("ProducerThread::work() - IN");
  
    mLogger.trace("ProducerThread::work() - Starting actual work...");
    for (int i = 0; i < mTotalMessages; ++i)
    {
      IMqMessage putMessage = createMessage(i);
      putMessage.setPriority(i%10);
      mContext.put(mQueueName, putMessage);
      
      if (i % 100 == 0) System.out.println(String.format("[P%d] ... %d", mThreadIndex, i));
    }
    
    mLogger.trace("ProducerThread::work() - OUT");
  }
  
  private IMqMessage createMessage(int idx)
  {
    IMqMessage msg;
    switch (mParams.mMessageType)
    {
      case 1:
        msg = MqMessageFactory.createStringMessage("Message number: " + (idx+1));
        break;
      case 2:
        msg = MqMessageFactory.createObjectMessage(mParams);
        break;
      case 3:
        msg = MqMessageFactory.createBytesMessage(("Message number: " + (idx+1)).getBytes());
        break;
      case 4:
        Properties map = new Properties();
        map.setStringProperty("client.app.author", "shy ifrah");
        msg = MqMessageFactory.createMapMessage(map);
        break;
      case 5:
        MqStreamMessage m = MqMessageFactory.createStreamMessage();
        try
        {
          m.writeInt(3);
          m.writeString("Shy Ifrah");
          m.writeInt(10);
        }
        catch (KasException e)
        {
          mLogger.trace("an error occurred while trying to write some data into message body");
        }
        msg = m;
        break;
      case 0:
      default:
        msg = MqMessageFactory.createMessage();
        msg.setStringProperty("client.app.author", "shy ifrah");
        break;
    }
    return msg;
  }
}