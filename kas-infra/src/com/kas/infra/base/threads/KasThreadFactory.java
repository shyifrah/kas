package com.kas.infra.base.threads;

import java.util.concurrent.ThreadFactory;
import com.kas.infra.base.IRunnable;

/**
 * A general-purpose thread factory
 * 
 * @author Pippo
 *  
 */
public class KasThreadFactory implements ThreadFactory
{
  /**
   * Create a general-purpose {@code Thread} for the specified command with the specified name
   * 
   * @param cmd the {@code Runnable} object which will be executed by this thread
   * @param name the name of the newly created thread
   * 
   * @return a newly created thread with the specified name 
   */
  public static Thread createThread(IRunnable cmd, String name)
  {
    return new KasRunnableThread(name, cmd);
  }
  
  /**
   * A sequence number that will be used in generating thread names
   */
  private int mSequence;
  
  /**
   * A prefix that will be used in generating thread names
   */
  private String mPrefix;
  
  /**
   * A lock object to synchronize thread creation
   */
  private Object mLock;
  
  /**
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
  
  /**
   * Factory method.
   * 
   * @param cmd The {@link Runnable} object to be executed by the newly created {@code Thread}
   * @return a newly created {@code Thread}
   * 
   * @throws IllegalArgumentException if {@code cmd} is not a {@link IRunnable} object.
   * 
   * @see java.util.concurrent.ThreadFactory#newThread(Runnable)
   */
  public Thread newThread(Runnable cmd)
  {
    if (!(cmd instanceof IRunnable))
      throw new IllegalArgumentException("Cannot create a new thread. Command object not a IRunnable");
    
    IRunnable iCmd = (IRunnable)cmd;
    String tn;
    synchronized(mLock)
    {
      ++mSequence;
      tn = mPrefix + '-' + mSequence;
    }
    return KasThreadFactory.createThread(iCmd, tn);
  }
}
