package com.kas.mq.impl.internal;

import com.kas.mq.impl.IMqMessage;

/**
 * A {@link MqRemoteQueue} object is a remotely-managed destination
 * 
 * @author Pippo
 */
public class MqRemoteQueue extends MqQueue
{
  /**
   * The client that will execute the actual commands for getting/putting messages
   */
  private MqConnection mClient;
  
  /**
   * Construct a {@link MqRemoteQueue} object
   * 
   * @param mgr The owning {@link MqManager} object
   * @param name The name of this destination object
   */
  public MqRemoteQueue(MqManager mgr, String name)
  {
    super(mgr, name);
    mClient = new MqConnection();
  }
  
  /**
   * The actual implementation of the Put method.<br>
   * <br>
   * Connecting to the KAS/MQ server, putting the message and disconnecting from it.
   * 
   * @param message The message to put
   * @return {@code true} if message was put, {@code false} otherwise
   */
  protected boolean internalPut(IMqMessage<?> message)
  {
    mLogger.debug("MqRemoteQueue::put() - IN");
    
    boolean success = false;
    if (message != null)
    {
      mClient.connect(mManager.getHost(), mManager.getPort());
      if (mClient.isConnected())
      {
        mClient.login(IMqConstants.cSystemUserName, IMqConstants.cSystemPassWord);
        mClient.put(mName, message);
        success = true;
      }
      mClient.disconnect();
    }
    
    mLogger.debug("MqRemoteQueue::put() - OUT, Returns=" + success);
    return success;
  }

  /**
   * The actual implementation of the Get method.<br>
   * <br>
   * Connecting to the KAS/MQ server, getting the message and disconnecting from it.
   * 
   * @param message The message to put
   * @return {@code true} if message was put, {@code false} otherwise
   */
  protected IMqMessage<?> internalGet(long timeout, long interval)
  {
    mLogger.debug("MqRemoteQueue::get() - IN, Timeout=" + timeout + ", Interval=" + interval);
    
    IMqMessage<?> result = null;
    
    mClient.connect(mManager.getHost(), mManager.getPort());
    if (mClient.isConnected())
    {
      mClient.login(IMqConstants.cSystemUserName, IMqConstants.cSystemPassWord);
      result = mClient.get(mName, timeout, interval);
    }
    mClient.disconnect();
    
    mLogger.debug("MqRemoteQueue::get() - OUT");
    return result;
  }
}
