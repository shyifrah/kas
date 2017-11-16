package com.kas.infra.base.threads;

import java.util.concurrent.LinkedBlockingQueue;
import com.kas.infra.base.IObject;
import com.kas.infra.utils.StringUtils;

public class ThreadPoolWorkQueue extends LinkedBlockingQueue<Runnable> implements IObject
{
  /***************************************************************************************************************
   *  
   */
  private static final long serialVersionUID = 1L;
  
  /***************************************************************************************************************
   *  
   */
  private int mMaxSize;
  
  /***************************************************************************************************************
   * Construct a work queue which will hold all Runnable objects that were submitted for execution
   * in the ThreadPool.
   * This work queue is capped at the specified {@code maxSize} value.
   * 
   * @param maxSize the work queue cap 
   */
  ThreadPoolWorkQueue(int maxSize)
  {
    mMaxSize = maxSize;
  }
  
  /***************************************************************************************************************
   * Override the {@link LinkedBlockingQueue#offer(Object)} method.
   * Since {@code LinkedBlockingQueue} is unbound, all offer() calls will succeed, which means that
   * a {@code ThreadPoolExecutor} that works with this type of queue will never spawn new threads.
   * By "capping" the work queue, we gain the ability to spawn new threads.
   * 
   * @param cmd the {@code Runnable} to be executed
   * 
   * @return true if the {@code Runnable} was queued, false otherwise
   */
  public boolean offer(Runnable cmd)
  {
    if (size() == mMaxSize)
      return false;
    
    return super.offer(cmd);
  }
  
  /***************************************************************************************************************
   * This method is used to queue a {@code Runnable} regardless of queue cap limitations
   * 
   * @param cmd the {@code Runnable} to be executed
   * 
   * @return true if the {@code Runnable} was queued, false otherwise
   */
  public boolean force(Runnable cmd)
  {
    return super.offer(cmd);
  }
  
  /***************************************************************************************************************
   *  
   */
  public String name()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("<")
      .append(this.getClass().getSimpleName())
      .append(">");
    return sb.toString();
  }

  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    String pad = StringUtils.getPadding(level);
    StringBuffer sb = new StringBuffer();
    sb.append(name()).append("(\n")
      .append(pad).append("  MaxSize=").append(mMaxSize).append("\n")
      .append(pad).append("  ActualSize=").append(size()).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
