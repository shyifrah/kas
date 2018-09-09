package com.kas.mq.impl.internal;

import com.kas.infra.base.KasException;
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
  private IClient mClient;
  
  /**
   * Construct a {@link MqRemoteQueue} object
   * 
   * @param mgr The owning {@link MqManager} object
   * @param name The name of this destination object
   */
  public MqRemoteQueue(MqManager mgr, String name)
  {
    super(mgr, name);
    mClient = new MqClientImpl();
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
      try
      {
        mClient.connect(mManager.getHost(), mManager.getPort());
        mClient.put(mName, message);
        success = true;
      }
      catch (KasException e)
      {
        mClient.setResponse("Failed to connect to remote host at " + mManager.getHost() + ':' + mManager.getPort());
      }
      finally
      {
        try
        {
          mClient.disconnect();
        }
        catch (KasException e) {}
      }
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
    
    try
    {
      mClient.connect(mManager.getHost(), mManager.getPort());
      result = mClient.get(mName, timeout, interval);
    }
    catch (KasException e)
    {
      mClient.setResponse("Failed to connect to remote host at " + mManager.getHost() + ':' + mManager.getPort());
    }
    finally
    {
      try
      {
        mClient.disconnect();
      }
      catch (KasException e) {}
    }
    
    mLogger.debug("MqRemoteQueue::get() - OUT");
    return result;
  }
}
