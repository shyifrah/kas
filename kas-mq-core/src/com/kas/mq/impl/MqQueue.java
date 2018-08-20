package com.kas.mq.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.kas.comm.IPacket;
import com.kas.comm.impl.PacketHeader;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.UniqueId;
import com.kas.infra.utils.FileUtils;
import com.kas.infra.utils.RunTimeUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.internal.MessageDeque;

/**
 * A {@link MqQueue} object is the simplest destination that is managed by the KAS/MQ system.
 * 
 * @author Pippo
 */
public class MqQueue extends AKasObject
{
  /**
   * Logger
   */
  private ILogger mLogger;
  
  /**
   * Name of this message queue
   */
  private String mName;
  
  /**
   * A UniqueId representing this message queue
   */
  private UniqueId mQueueId;
  
  /**
   * The actual message container. An array of {@link MessageDeque} objects, one for each priority.<br>
   * <br>
   * When a message with priority of 0 is received by this {@link MqQueue} object, it is stored in the 
   * {@link MessageDeque} at index 0 of the array. A message with priority of 1 is stored at index 1 etc.
   */
  protected transient MessageDeque [] mQueueArray;
  
  /**
   * The file backing up this {@link MqQueue} object.
   */
  protected transient File mBackupFile = null;
  
  /**
   * Constructing a {@link MqQueue} object with the specified name.
   * 
   * @param name The name of this {@link MqQueue} object.
   */
  public MqQueue(String name)
  {
    mLogger     = LoggerFactory.getLogger(this.getClass());
    mName       = name;
    mQueueId    = UniqueId.generate();
    mQueueArray = new MessageDeque[ IMqConstants.cMaximumPriority + 1 ];
    for (int i = 0; i <= IMqConstants.cMaximumPriority; ++i)
      mQueueArray[i] = new MessageDeque();
  }
  
  /**
   * Get queue name
   * 
   * @return queue name
   */
  public String getName()
  {
    return mName;
  }
  
  /**
   * Get queue ID
   * 
   * @return queue ID
   */
  public UniqueId getId()
  {
    return mQueueId;
  }
  
  /**
   * Get queue size
   * 
   * @return the number of messages in all priority queues
   */
  public int size()
  {
    int sum = 0;
    for (int i = 0; i < mQueueArray.length; ++i)
      sum += mQueueArray[i].size();
    return sum;
  }
  
  /**
   * Restore the {@link MqQueue} contents from the file system
   * 
   * @return {@code true} if queue contents restored successfully, {@code false} otherwise
   */
  public boolean restore()
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
            MqMessage message = (MqMessage)packet;
            mLogger.diag("MqQueue::restore() - Header="  + header.toPrintableString());
            mLogger.diag("MqQueue::restore() - Message=" + message.toPrintableString());
            
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
  public boolean backup()
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
          for (int i = 0; i < mQueueArray.length; ++i)
          {
            MessageDeque msgDeq = mQueueArray[i];
            while (!msgDeq.isEmpty())
            {
              MqMessage message = msgDeq.poll();
              
              PacketHeader header = message.createHeader();
              header.serialize(ostream);
              message.serialize(ostream);
              mLogger.diag("MqQueue::backup() - Header="  + header.toPrintableString());
              mLogger.diag("MqQueue::backup() - Message=" + message.toPrintableString());
              
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
  public boolean put(MqMessage message)
  {
    if (message == null)
      return false;
    
    int prio = message.getPriority();
    return mQueueArray[prio].offer(message);
  }
  
  /**
   * Get a message with max priority from this {@link MqQueue} object
   * 
   * @return the returned message or {@code null} if there is no message
   */
  public MqMessage get()
  {
    return get(IMqConstants.cMaximumPriority);
  }
  
  /**
   * Get a message with a specific priority from this {@link MqQueue} object
   * 
   * @param priority The priority of the message to be retrieved
   * @return the returned message or {@code null} if there is no message
   */
  public MqMessage get(int priority)
  {
    if ((priority < IMqConstants.cMinimumPriority) || (priority > IMqConstants.cMaximumPriority))
      throw new IllegalArgumentException("Invalid message priority: " + priority);
    
    return mQueueArray[priority].poll();
  }
  
  /**
   * Get a message with max priority from this {@link MqQueue} object, and wait indefinitely if one is not available.
   * 
   * @return the returned message
   */
  public MqMessage getAndWait()
  {
    return getAndWait(IMqConstants.cMaximumPriority);
  }
  
  /**
   * Get a message with a specific priority from this {@link MqQueue} object, and wait indefinitely if one is not available.
   * 
   * @param priority The priority of the message to be retrieved 
   * @return the returned message
   */
  public MqMessage getAndWait(int priority)
  {
    if ((priority < IMqConstants.cMinimumPriority) || (priority > IMqConstants.cMaximumPriority))
      throw new IllegalArgumentException("Invalid message priority: " + priority);
    
    MqMessage result = null;
    try
    {
      result = mQueueArray[priority].take();
    }
    catch (InterruptedException e) {}
    return result;
  }
  
  /**
   * Get a message with max priority from this {@link MqQueue} object. If a message is not available immediately
   * wait for {@code timeout} milliseconds at most.<br>
   * <br>
   * Because this operation is done in a manner of polling, the thread is delayed for {@code 1000} milliseconds after each
   * unsuccessful poll. Then it polls for a message again, and delayed again, etc. This continues until the total delayed time has
   * exceeded the timeout value or a message was retrieved.
   * 
   * @param timeout The number of milliseconds to wait until the get operation is aborted.
   * @return the returned message or {@code null} if timeout occurred. 
   */
  public MqMessage getAndWaitWithTimeout(long timeout)
  {
    return getAndWaitWithTimeout(IMqConstants.cMinimumPriority, timeout, 1000L);
  }
  
  /**
   * Get a message with a specific priority from this {@link MqQueue} object. If a message is not available immediately
   * wait for {@code timeout} milliseconds at most.<br>
   * <br>
   * Because this operation is done in a manner of polling, the thread is delayed for {@code 1000} milliseconds after each
   * unsuccessful poll. Then it polls for a message again, and delayed again, etc. This continues until the total delayed time has
   * exceeded the timeout value or a message was retrieved.
   * 
   * @param priority The priority of the message to be retrieved
   * @param timeout The number of milliseconds to wait until the get operation is aborted.
   * @return the returned message or {@code null} if timeout occurred. 
   */
  public MqMessage getAndWaitWithTimeout(int priority, long timeout)
  {
    return getAndWaitWithTimeout(priority, timeout, 1000L);
  }
  
  /**
   * Get a message with a specific priority from this {@link MqQueue} object. If a message is not available immediately
   * wait for {@code timeout} milliseconds at most.<br>
   * <br>
   * Because this operation is done in a manner of polling, the thread is delayed for {@code interval} milliseconds after each
   * unsuccessful poll. Then it polls for a message again, and delayed again, etc. This continues until the total delayed time has
   * exceeded the timeout value or a message was retrieved.
   * 
   * @param priority The priority of the message to be retrieved
   * @param timeout The number of milliseconds to wait until the get operation is aborted
   * @param interval The number of milliseconds to delay between each polling operation
   * @return the returned message or {@code null} if timeout occurred. 
   */
  public MqMessage getAndWaitWithTimeout(int priority, long timeout, long interval)
  {
    if ((priority < IMqConstants.cMinimumPriority) || (priority > IMqConstants.cMaximumPriority))
      throw new IllegalArgumentException("Invalid message priority: " + priority);
    
    if (timeout <= 0)
      return null;
    
    MqMessage result = get(priority);
    long millisPassed = 0;
    while ((result == null) && (millisPassed < timeout))
    {
      RunTimeUtils.sleepForMilliSeconds(interval);
      millisPassed += interval;
      result = get(priority);
    }
    
    return result;
  }
  
  /**
   * Get the object's string representation
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("Name=").append(mName).append(",Id=").append(mQueueId.toString());
    return sb.toString();
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
      .append(pad).append("  Name=").append(mName).append("\n")
      .append(pad).append("  UniqueId=").append(mQueueId.toString()).append("\n")
      .append(pad).append("  Queues=(\n");
    
    for (int i = 0; i < mQueueArray.length; ++i)
      sb.append(pad).append("    P").append(String.format("%02d=(", i)).append(mQueueArray[i].toPrintableString(0)).append(")\n");
    
    sb.append(pad).append("  )\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
