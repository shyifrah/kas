package com.kas.mq.internal;

import com.kas.mq.impl.messages.IMqMessage;

/**
 * A {@link MqRemoteQueue} object is a remotely-managed destination
 * 
 * @author Pippo
 */
public class MqRemoteQueue extends MqQueue
{
  /**
   * The pool that provides connections
   */
  private IMqConnectionPool mPool;
  
  /**
   * Construct a {@link MqRemoteQueue} object
   * 
   * @param mgr
   *   The owning {@link MqManager} object
   * @param name
   *   The name of this destination object
   */
  public MqRemoteQueue(MqManager mgr, String name, IMqConnectionPool pool)
  {
    super(mgr, name);
    mPool = pool;
  }
  
  /**
   * The actual implementation of the Put method.<br>
   * Connecting to the KAS/MQ server, putting the message and disconnecting from it.
   * 
   * @param message
   *   The message to put
   * @return
   *   {@code true} if message was put, {@code false} otherwise
   */
  protected boolean internalPut(IMqMessage message)
  {
    mLogger.trace("MqRemoteQueue::put() - IN");
    
    boolean success = false;
    if (message != null)
    {
      MqConnection conn = mPool.allocate();
      
      conn.connect(mManager.getHost(), mManager.getPort());
      if (conn.isConnected())
      {
        conn.login(IMqConstants.cSystemUserName, IMqConstants.cSystemPassWord);
        conn.put(mName, message);
        success = true;
      }
      conn.disconnect();
      
      mPool.release(conn);
    }
    
    mLogger.trace("MqRemoteQueue::put() - OUT, Returns=" + success);
    return success;
  }

  /**
   * The actual implementation of the Get method.<br>
   * Connecting to the KAS/MQ server, getting the message and disconnecting from it.
   * 
   * @param message
   *   The message to put
   * @return
   *   {@code true} if message was put, {@code false} otherwise
   */
  protected IMqMessage internalGet(long timeout, long interval)
  {
    mLogger.trace("MqRemoteQueue::get() - IN, Timeout=" + timeout + ", Interval=" + interval);
    
    IMqMessage result = null;
    
    MqConnection conn = mPool.allocate();
    
    conn.connect(mManager.getHost(), mManager.getPort());
    if (conn.isConnected())
    {
      conn.login(IMqConstants.cSystemUserName, IMqConstants.cSystemPassWord);
      result = conn.get(mName, timeout, interval);
    }
    conn.disconnect();
    
    mPool.release(conn);
    
    mLogger.trace("MqRemoteQueue::get() - OUT");
    return result;
  }
}
