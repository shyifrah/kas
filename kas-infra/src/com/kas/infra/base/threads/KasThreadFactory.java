package com.kas.infra.base.threads;

import java.util.concurrent.ThreadFactory;

public class KasThreadFactory implements ThreadFactory
{
  /***************************************************************************************************************
   * Create a general-purpose {@code Thread} for the specified command with the specified name
   * 
   * @param cmd the {@code Runnable} object which will be executed by this thread
   * @param name the name of the newly created thread
   * 
   * @return a newly created thread with the specified name 
   */
  public static Thread createThread(Runnable cmd, String name)
  {
    return new KasRunnableThread(name, cmd);
  }
  
  /***************************************************************************************************************
   *  
   */
  private int    mSequence;
  private String mPrefix;
  private Object mLock;
  
  /***************************************************************************************************************
   * Construct a thread factory which will create threads with a name starting with the specified prefix
   * and a sequence number.
   * 
   * @param pref the name prefix 
   */
  public KasThreadFactory(String pref)
  {
    mPrefix = pref;
    mSequence = 0;
    mLock = new Object();
  }
  
  /***************************************************************************************************************
   * Factory method
   * and a sequence number.
   * 
   * @param pref the name prefix 
   */
  public Thread newThread(Runnable r)
  {
    String tn;
    synchronized(mLock)
    {
      ++mSequence;
      tn = mPrefix + '-' + mSequence;
    }
    return KasThreadFactory.createThread(r, tn);
  }
}
