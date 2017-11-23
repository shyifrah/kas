package com.kas.q.samples.internal;

import javax.jms.Connection;
import com.kas.infra.base.KasException;
import com.kas.infra.base.Properties;
import com.kas.infra.utils.RunTimeUtils;

public abstract class AThread extends Thread
{
  public static final String cProperty_ThreadName = "thread_name";
  public static final String cProperty_PreAndPostDelay = "pre_post_delay";
  public static final String cProperty_NumOfMessages = "num_of_messages";
  public static final String cProperty_KasqConnection = "kasq_connection";
  public static final String cProperty_QueueName = "queue_name";
  
  protected int        mNumOfMessages;
  protected int        mDelay;
  protected Connection mConnection;
  protected Properties mProperties;
  
  AThread(Properties threadParams) throws KasException
  {
    if (threadParams == null)
      throw new NullPointerException("AThread::AThread() - Null thread params");
    mProperties = threadParams;
    mNumOfMessages = mProperties.getIntProperty("num_of_messages", 5);
    mDelay         = mProperties.getIntProperty("pre_post_delay", 5);
    mConnection    = (Connection)mProperties.getObjectProperty("kasq_connection");
    if (mConnection == null)
      throw new NullPointerException("AThread::AThread() - Null connection object");
    String name    = mProperties.getStringProperty("thread_name", "");
    super.setName(name);
  }
  
  public void run()
  {
    RunTimeUtils.sleep(mDelay);

    work();
    
    RunTimeUtils.sleep(mDelay);
  }
  
  public abstract void work();
}
