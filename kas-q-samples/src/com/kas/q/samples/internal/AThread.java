package com.kas.q.samples.internal;

import com.kas.infra.base.KasException;
import com.kas.infra.base.Properties;
import com.kas.infra.utils.RunTimeUtils;
import com.kas.q.ext.KasqClient;

public abstract class AThread extends Thread
{
  public static final String cProperty_ThreadName      = "thread_name";
  public static final String cProperty_PreAndPostDelay = "pre_post_delay";
  public static final String cProperty_NumOfMessages   = "num_of_messages";
  public static final String cProperty_KasqClient      = "kasq_client";
  public static final String cProperty_QueueName       = "queue_name";
  public static final String cProperty_UserName        = "connection_username";
  public static final String cProperty_Password        = "connection_password";
  
  protected int        mNumOfMessages;
  protected int        mDelay;
  protected KasqClient mClient;
  protected Properties mProperties;
  
  AThread(Properties threadParams) throws KasException
  {
    if (threadParams == null)
      throw new NullPointerException("AThread::AThread() - Null thread params");
    
    mProperties = threadParams;
    
    mNumOfMessages = mProperties.getIntProperty(cProperty_NumOfMessages, 5);
    mDelay         = mProperties.getIntProperty(cProperty_PreAndPostDelay, 5);
    mClient        = (KasqClient)mProperties.getObjectProperty(cProperty_KasqClient, null);
    if (mClient == null)
      throw new NullPointerException("AThread::AThread() - Null client object");
    
    String name = mProperties.getStringProperty(cProperty_ThreadName, "");
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
