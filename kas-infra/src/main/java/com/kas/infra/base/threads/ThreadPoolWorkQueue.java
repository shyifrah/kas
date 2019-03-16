package com.kas.infra.base.threads;

import java.util.concurrent.LinkedBlockingQueue;
import com.kas.infra.base.IObject;
import com.kas.infra.utils.StringUtils;

/**
 * KAS implementation for a thread pool work queue.<br>
 * The major difference is described in {@link #offer(Runnable)} and {@link #force(Runnable)}.
 * 
 * @author Pippo
 */
public class ThreadPoolWorkQueue extends LinkedBlockingQueue<Runnable> implements IObject
{
  private static final long serialVersionUID = 1L;
  
  /**
   * Maximum size of this work queue
   */
  private int mMaxSize;
  
  /**
   * Construct a work queue which will hold all Runnable objects that were submitted for execution
   * in the ThreadPool.<br>
   * <br>
   * This work queue is capped at the specified {@code maxSize} value.
   * 
   * @param maxSize
   *   the work queue cap 
   */
  ThreadPoolWorkQueue(int maxSize)
  {
    mMaxSize = maxSize;
  }
  
  /**
   * Override the {@link LinkedBlockingQueue#offer(Object)} method.<br>
   * <br>
   * Since {@code LinkedBlockingQueue} is unbound, all offer() calls will succeed, which means that
   * a {@code ThreadPoolExecutor} that works with this type of queue will never spawn new threads.<br>
   * By "capping" the work queue, we gain the ability to spawn new threads.
   * 
   * @param cmd
   *   The {@code Runnable} to be executed
   * @return
   *   {@code true} if the {@code Runnable} was queued, {@code false} otherwise
   */
  public boolean offer(Runnable cmd)
  {
    if (size() == mMaxSize)
      return false;
    
    return super.offer(cmd);
  }
  
  /**
   * This method is used to queue a {@code Runnable} regardless of queue cap limitations
   * 
   * @param cmd
   *   The {@code Runnable} to be executed
   * @return
   *   {@code true} if the {@code Runnable} was queued, {@code false} otherwise
   */
  public boolean force(Runnable cmd)
  {
    return super.offer(cmd);
  }
  
  /**
   * Returns the {@link IObject} simple class name enclosed with chevrons.
   * 
   * @return
   *   class name enclosed with chevrons.
   */
  public String name()
  {
    return StringUtils.getClassName(getClass());
  }
  
  /**
   * Returns the {@link IObject} string representation.
   * 
   * @param level
   *   The required padding level
   * @return
   *   the string representation with the specified level of padding
   */
  public String toPrintableString(int level)
  {
    String pad = StringUtils.getPadding(level);
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n")
      .append(pad).append("  MaxSize=").append(mMaxSize).append("\n")
      .append(pad).append("  ActualSize=").append(size()).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
