package com.kas.q.samples.internal;

import javax.jms.Queue;
import javax.jms.Session;
import com.kas.infra.utils.RunTimeUtils;

public abstract class AThread extends Thread
{
  protected int     mNumOfMessages;
  protected int     mDelay;
  protected Session mSession;
  protected Queue   mQueue;
  
  AThread(String name, int numOfMessages, int delay, Session session, Queue queue)
  {
    super(name);
    mNumOfMessages = numOfMessages;
    mDelay   = delay;
    mSession = session;
    mQueue   = queue;
  }
  
  public void run()
  {
    RunTimeUtils.sleep(mDelay);

    work();
    
    RunTimeUtils.sleep(mDelay);
  }
  
  public abstract void work();
}
