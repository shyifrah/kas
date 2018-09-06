package com.kas.mq.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.kas.comm.IPacket;
import com.kas.comm.impl.PacketHeader;
import com.kas.infra.base.TimeStamp;
import com.kas.infra.utils.FileUtils;
import com.kas.infra.utils.RunTimeUtils;
import com.kas.mq.types.MessageStore;

/**
 * A {@link MqQueue} object is a locally-managed destination
 * 
 * @author Pippo
 */
public class MqQueue extends MqDestination
{
  /**
   * Maximum number of messages this queue can hold before
   * starting to fail {@link #put(IMqMessage) put} operations.
   */
  private int mThreshold;
  
  /**
   * Last access
   */
  private String mLastAccessUser = "SYSTEM";
  private TimeStamp mLastAccessTimeStamp = TimeStamp.now();
  private String mLastAccessMethod = "<init>";
  
  /**
   * The actual message container. An array of {@link MessageStore} objects, one for each priority.<br>
   * <br>
   * When a message with priority of 0 is received by this {@link MqQueue} object, it is stored in the 
   * {@link MessageStore} at index 0 of the array. A message with priority of 1 is stored at index 1 etc.
   */
  protected transient MessageStore [] mStoresArray;
  
  /**
   * The file backing up this {@link MqQueue} object.
   */
  protected transient File mBackupFile = null;
  
  /**
   * Constructing a {@link MqQueue} object with the specified name.
   * 
   * @param mgr The name of the manager that owns this {@link MqQueue}
   * @param name The name of this {@link MqQueue} object.
   * @param threshold The maximum message capacity this {@link MqQueue} can hold
   */
  public MqQueue(String mgr, String name, int threshold)
  {
    super(mgr, name);
    mStoresArray = new MessageStore[ IMqConstants.cMaximumPriority + 1 ];
    for (int i = 0; i <= IMqConstants.cMaximumPriority; ++i)
      mStoresArray[i] = new MessageStore();
  }
  
  /**
   * Get the {@link MqQueue} threshold
   * 
   * @return the {@link MqQueue} threshold
   */
  public int getThreshold()
  {
    return mThreshold;
  }
  
  /**
   * Get queue size
   * 
   * @return the number of messages in all priority queues
   */
  public int size()
  {
    int sum = 0;
    for (int i = 0; i < mStoresArray.length; ++i)
    {
      sum += mStoresArray[i].size();
    }
    return sum;
  }
  
  /**
   * Restore the {@link MqQueue} contents from the file system
   * 
   * @return {@code true} if queue contents restored successfully, {@code false} otherwise
   */
  public synchronized boolean restore()
  {
    mLogger.debug("MqQueue::restore() - IN");
    boolean success = true;
    
    String fullFileName = RunTimeUtils.getProductHomeDir() + File.separator + "repo" + File.separator + mName + ".qbk";
    mBackupFile = new File(fullFileName);
    mLogger.debug("MqQueue::restore() - Backup file: [" + mBackupFile.getAbsolutePath() + "]");
    
    if (!mBackupFile.exists())
    {
      success = FileUtils.createFile(fullFileName);
      mLogger.debug("MqQueue::restore() - Backup file doesn't exist. Creating it... " + success);
    }
    else if (!mBackupFile.canRead())
    {
      mLogger.warn("Cannot read contents of backup file " + fullFileName);
      success = false;
    }
    else if (!mBackupFile.isFile())
    {
      mLogger.warn("Backup file " + fullFileName + " doesn't designate a regular file");
      success = false;
    }
    else
    {
      FileInputStream fis = null;
      ObjectInputStream istream = null;
      try
      {
        boolean eof = false;
        boolean err = false;
        
        fis = new FileInputStream(mBackupFile);
        istream = new ObjectInputStream(fis);
        
        while ((!eof) && (!err))
        {
          try
          {
            PacketHeader header = new PacketHeader(istream);
            IPacket packet = header.read(istream);
            IMqMessage<?> message = (IMqMessage<?>)packet;
            mLogger.diag("MqQueue::restore() - Header="  + header.toPrintableString());
            mLogger.diag("MqQueue::restore() - Message=" + message.toPrintableString(0));
            
            put(message);
          }
          catch (IOException e)
          {
            eof = true;
          }
          catch (Throwable e)
          {
            mLogger.warn("Exception caught while trying to restore queue " + mName + " contents. Exception: ", e);
            err = true;
            success = false;
          }
        }
      }
      catch (IOException e)
      {
        mLogger.warn("Exception caught while trying to open queue " + mName + " backup file. Exception: ", e);
        success = false;
      }
      finally
      {
        try
        {
          if (fis != null) fis.close();
          if (istream != null) istream.close();
        }
        catch (Throwable e) {}
      }
      
      if (success)
      {
        FileUtils.deleteFile(mBackupFile.getAbsolutePath());
        mLogger.info("Queue " + mName + " contents successfully restored; Total read messages [" + size() + "]");
      }
    }
    
    mLogger.debug("MqQueue::restore() - OUT, Returns=" + Boolean.toString(success));
    return success;
  }

  /**
   * Backup the {@link MqQueue} contents to file system
   * 
   * @return {@code true} if completed writing all queue contents successfully, {@code false} otherwise
   */
  public synchronized boolean backup()
  {
    mLogger.debug("MqQueue::backup() - IN, name=[" + mName + "]");
    boolean success = true;
    
    String fullFileName = RunTimeUtils.getProductHomeDir() + File.separator + "repo" + File.separator + mName + ".qbk";
    mBackupFile = new File(fullFileName);
    mLogger.debug("MqQueue::backup() - Backup file: [" + mBackupFile.getAbsolutePath() + "]");
    
    if (mBackupFile.exists())
    {
      success = FileUtils.deleteFile(fullFileName);
      mLogger.debug("MqQueue::restore() - Backup file already exists. Deleting it... " + Boolean.toString(success));
    }
    
    if (success && (!mBackupFile.exists()))
    {
      success = FileUtils.createFile(fullFileName);
      mLogger.debug("MqQueue::restore() - Backup file doesn't exist. Creating it... " + Boolean.toString(success));
    }
    
    if (success && (!mBackupFile.canWrite()))
    {
      mLogger.warn("Cannot write contents to backup file [" + fullFileName + "], file is not writable...");
      success = false;
    }
    
    if (success && (!mBackupFile.isFile()))
    {
      mLogger.warn("Backup file " + fullFileName + " doesn't designate a regular file");
      success = false;
    }

    if (success)
    {
      // save all messages to file
      int msgs = 0;
      FileOutputStream fos = null;
      ObjectOutputStream ostream = null;
      
      try
      {
        fos = new FileOutputStream(mBackupFile);
        ostream = new ObjectOutputStream(fos);
        
        try
        {
          for (int i = 0; i < mStoresArray.length; ++i)
          {
            MessageStore store = mStoresArray[i];
            while (!store.isEmpty())
            {
              IMqMessage<?> message = store.poll();
              
              PacketHeader header = message.createHeader();
              header.serialize(ostream);
              message.serialize(ostream);
              mLogger.diag("MqQueue::backup() - Header="  + header.toPrintableString());
              mLogger.diag("MqQueue::backup() - Message=" + message.toPrintableString(0));
              
              ++msgs;
            }
          }
        }
        catch (Throwable e)
        {
          mLogger.warn("Exception caught while trying to write to queue " + mName + " backup file. Exception: ", e);
          success = false;
        }
      }
      catch (IOException e)
      {
        mLogger.warn("Exception caught while trying to open queue " + mName + " backup file. Exception: ", e);
        success = false;
      }
      
      // cleanup
      try
      {
        ostream.flush();
        ostream.close();
        fos.flush();
        fos.close();
      }
      catch (Throwable e) {}
      
      if (success) mLogger.info("Total messages saved to queue " + mName + " backup file: " + msgs);
    }
    
    mLogger.debug("MqQueue::backup() - OUT, Returns=" + Boolean.toString(success));
    return success;
  }
  
  /**
   * Put a message into this {@link MqQueue} object.
   * 
   * @param message The message that should be stored at this {@link MqQueue} object.
   * @return {@code true} if message was added, {@code false} otherwise.
   */
  public boolean put(IMqMessage<?> message)
  {
    if (message == null)
      return false;
    
    if (size() >= mThreshold)
      return false;
    
    int prio = message.getPriority();
    
    boolean success = false;
    success = mStoresArray[prio].offer(message);
    
    return success;
  }
  
  /**
   * Get a message and wait indefinitely for one to be available.<br>
   * 
   * @return The {@link AMqMessage}
   */
  public IMqMessage<?> get()
  {
    return internalGet(0, IMqConstants.cDefaultPollingInterval);
  }
  
  /**
   * Get a message and wait {@code timeout} milliseconds for one to be available.<br>
   * <br>
   * If {@code timeout} is 0, this method is equivalent to {@link #get()}.
   * 
   * @return The {@link AMqMessage} or {@code null} if one is unavailable
   * 
   * @throws IllegalArgumentException if {@code timeout} is lower than 0
   */
  public IMqMessage<?> get(long timeout)
  {
    if (timeout < 0)
      throw new IllegalArgumentException("Invalid timeout: " + timeout);
    
    return internalGet(timeout, IMqConstants.cDefaultPollingInterval);
  }
  
  /**
   * Get a message and wait {@code timeout} milliseconds for one to be available.<br>
   * <br>
   * Execution is suspended for {@code interval} milliseconds between each polling operation.
   * 
   * @return The {@link AMqMessage} or {@code null} if one is unavailable
   * 
   * @throws IllegalArgumentException if {@code timeout} or {@code interval} are lower than 0
   */
  public IMqMessage<?> get(long timeout, long interval)
  {
    if (timeout < 0)
      throw new IllegalArgumentException("Invalid timeout: " + timeout);
    
    if (interval <= 0)
      throw new IllegalArgumentException("Invalid polling interval: " + interval);
    
    return internalGet(timeout, interval);
  }
  
  /**
   * Get the {@link AMqMessage message} with the highest priority from this {@link MqQueue} object.<br>
   * <br>
   * Since the actual message store is implemented by {@link MessageStore}, the actual "get" operations
   * are translated to {@link MessageStore#poll()}.<br>
   * Polling is done by calling the {@link MessageStore#poll()} method and then suspend the thread
   * execution for {@code interval} milliseconds.<br>
   * <br>
   * If a message is not available, the method will poll for one until {@code timeout} expires.
   * If {@code timeout} is 0, the method will poll indefinitely.
   * 
   * @param timeout The timeout until which the method will give up
   * @param interval The gap length between polling operations
   * @return The {@link AMqMessage} or {@code null} if one is unavailable
   */
  private IMqMessage<?> internalGet(long timeout, long interval)
  {
    IMqMessage<?> result = null;
    
    long millisPassed = 0;
    boolean timeoutExpired = false;
    while ((result == null) && (!timeoutExpired))
    {
      int priority = internalGetPriorityIndex();
      if (priority > -1)
      {
        result = mStoresArray[priority].poll();
      }
      else
      {
        RunTimeUtils.sleepForMilliSeconds(interval);
        millisPassed += interval;
        
        if (timeout != 0)
          timeoutExpired = millisPassed >= timeout;
      }
    }
    
    return result;
  }
  
  /**
   * Find the first non-empty {@link MessageStore} object in the queue array.
   * 
   * @return the index of the first non-empty queue, or -1 if all are empty.
   */
  private int internalGetPriorityIndex()
  {
    for (int prio = IMqConstants.cMaximumPriority; prio >= IMqConstants.cMinimumPriority; --prio)
    {
      int size;
      size = mStoresArray[prio].size();
      if (size > 0) return prio;
    }
    return -1;
  }
  
  /**
   * Set the last access to the queue
   * 
   * @param user Last user to access this {@link MqQueue}
   * @param method The last method that was used to access this {@link MqQueue}
   */
  public synchronized void setLastAccess(String user, String method)
  {
    mLastAccessUser = user;
    mLastAccessTimeStamp = TimeStamp.now();
    mLastAccessMethod = method;
  }
  
  /**
   * Get the last access to the queue
   * 
   * @return A string describing the last access info
   */
  public synchronized String getLastAccess()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("By ").append(mLastAccessUser)
      .append(" at ").append(mLastAccessTimeStamp.toString())
      .append(" for method ").append(mLastAccessMethod).append("()");
    return sb.toString();
  }
  
  /**
   * Expire old messages
   * 
   * @return the total number of messages expired
   */
  public int expire()
  {
    mLogger.debug("MqQueue::expire() - IN");
    
    int total = 0;
    for (int prio = IMqConstants.cMaximumPriority; prio >= IMqConstants.cMinimumPriority; --prio)
    {
      MessageStore mdq = mStoresArray[prio];
      for (IMqMessage<?> msg : mdq)
      {
        if (msg.isExpired())
        {
          mdq.remove(msg);
        }
      }
    }
    
    setLastAccess("SYSTEM", "expire");
    
    mLogger.debug("MqQueue::expire() - OUT, Returns=" + total);
    return total;
  }
  
  /**
   * Get the object's detailed string representation
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n")
      .append(pad).append("  Manager=").append(mManager).append("\n")
      .append(pad).append("  Name=").append(mName).append("\n")
      .append(pad).append("  Threshold=").append(mThreshold).append("\n")
      .append(pad).append("  LastAccess=(\n")
      .append(pad).append("    By=").append(mLastAccessUser).append("\n")
      .append(pad).append("    At=").append(mLastAccessTimeStamp.toString()).append("\n")
      .append(pad).append("    For=").append(mLastAccessMethod).append("\n")
      .append(pad).append("  )\n")
      .append(pad).append("  PriorityStores=(\n");
    
    for (int i = 0; i < mStoresArray.length; ++i)
      sb.append(pad).append("    P").append(String.format("%02d=(", i)).append(mStoresArray[i].toPrintableString(0)).append(")\n");
    
    sb.append(pad).append("  )\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
