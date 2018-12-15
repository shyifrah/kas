package com.kas.mq.internal;

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
import com.kas.infra.utils.StringUtils;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.typedef.MessageQueue;

/**
 * A {@link MqLocalQueue} object is a locally-managed destination
 * 
 * @author Pippo
 */
public class MqLocalQueue extends MqQueue
{
  /**
   * Maximum number of messages this queue can hold before put operations
   * will start to fail.
   */
  private int mThreshold;
  
  /**
   * Should this queue be saved to file-system
   */
  private boolean mBackup;
  
  /**
   * Last access
   */
  private String mLastAccessUser = IMqConstants.cSystemUserName;
  private TimeStamp mLastAccessTimeStamp = TimeStamp.now();
  private String mLastAccessMethod = "<init>";
  
  /**
   * The actual message container. An array of {@link MessageQueue} objects, one for each priority.<br>
   * <br>
   * When a message with priority of 0 is received by this {@link MqLocalQueue} object, it is stored in the 
   * {@link MessageQueue} at index 0 of the array. A message with priority of 1 is stored at index 1 etc.
   */
  protected transient MessageQueue [] mQueueArray;
  
  /**
   * The file backing up this {@link MqLocalQueue} object.
   */
  protected transient File mBackupFile = null;
  
  /**
   * Constructing a {@link MqLocalQueue} object with the specified name.
   * 
   * @param mgr The name of the manager that owns this {@link MqLocalQueue}
   * @param name The name of this {@link MqLocalQueue} object.
   * @param threshold The maximum message capacity this {@link MqLocalQueue} can hold
   * @param backup Should this queue be saved to file-system
   */
  public MqLocalQueue(MqManager mgr, String name, int threshold, boolean backup)
  {
    super(mgr, name);
    mThreshold = threshold;
    mBackup = backup;
    mQueueArray = new MessageQueue[ IMqConstants.cMaximumPriority + 1 ];
    for (int i = 0; i <= IMqConstants.cMaximumPriority; ++i)
      mQueueArray[i] = new MessageQueue();
  }
  
  /**
   * Get the {@link MqLocalQueue} threshold
   * 
   * @return the {@link MqLocalQueue} threshold
   */
  public int getThreshold()
  {
    return mThreshold;
  }
  
  /**
   * Get the {@link MqLocalQueue} permanent value
   * 
   * @return the {@link MqLocalQueue}  permanent value
   */
  public boolean getPermanentValue() {
	  return mBackup;
  }
 
  /**
   * Set the {@link MqLocalQueue} Threshold
   *  	
   * @param threshold The threshold value to be set
   */  
  public void setThreshold(int threshold) {
	  this.mThreshold=threshold;
  }
  
  /**
   * Set the {@link MqLocalQueue} Permanent value
   *  	
   * @param backup The permanent value to be set
   */  
  public void setPermanentValue(boolean backup) {
	  this.mBackup = backup;
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
    {
      sum += mQueueArray[i].size();
    }
    return sum;
  }
  
  /**
   * Restore the {@link MqLocalQueue} contents from the file system
   * 
   * @return {@code true} if queue contents restored successfully, {@code false} otherwise
   */
  public synchronized boolean restore()
  {
    mLogger.debug("MqLocalQueue::restore() - IN");
    boolean success = true;
    
    String fullFileName = RunTimeUtils.getProductHomeDir() + File.separator + "repo" + File.separator + mName + ".qbk";
    mBackupFile = new File(fullFileName);
    mLogger.debug("MqLocalQueue::restore() - Backup file: [" + mBackupFile.getAbsolutePath() + "]");
    
    if (!mBackupFile.exists())
    {
      success = FileUtils.createFile(fullFileName);
      mLogger.debug("MqLocalQueue::restore() - Backup file doesn't exist. Creating it... " + success);
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
        
        // read queue details
        try
        {
          mThreshold = istream.readInt();
          mLastAccessUser = (String)istream.readObject();
          mLastAccessTimeStamp = new TimeStamp(istream.readLong());
          mLastAccessMethod = (String)istream.readObject();
          mLogger.debug("MqLocalQueue::restore() - Threshold=" + mThreshold + "; LastAccess=(" + getLastAccess() + ")");
        }
        catch (Throwable e) {}
        
        while ((!eof) && (!err))
        {
          try
          {
            PacketHeader header = new PacketHeader(istream);
            IPacket packet = header.read(istream);
            IMqMessage message = (IMqMessage)packet;
            mLogger.diag("MqLocalQueue::restore() - Header="  + StringUtils.asPrintableString(header));
            mLogger.diag("MqLocalQueue::restore() - Message=" + StringUtils.asPrintableString(message));
            
            internalPut(message, false);
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
    
    mLogger.debug("MqLocalQueue::restore() - OUT, Returns=" + Boolean.toString(success));
    return success;
  }

  /**
   * Backup the {@link MqLocalQueue} contents to file system
   * 
   * @return {@code true} if completed writing all queue contents successfully, {@code false} otherwise
   */
  public synchronized boolean backup()
  {
    mLogger.debug("MqLocalQueue::backup() - IN, name=[" + mName + "]");
    boolean success = true;
    
    if (mBackup)
    {
      String fullFileName = RunTimeUtils.getProductHomeDir() + File.separator + "repo" + File.separator + mName + ".qbk";
      mBackupFile = new File(fullFileName);
      mLogger.debug("MqLocalQueue::backup() - Backup file: [" + mBackupFile.getAbsolutePath() + "]");
      
      if (mBackupFile.exists())
      {
        success = FileUtils.deleteFile(fullFileName);
        mLogger.debug("MqLocalQueue::backup() - Backup file already exists. Deleting it... " + Boolean.toString(success));
      }
      
      if (success && (!mBackupFile.exists()))
      {
        success = FileUtils.createFile(fullFileName);
        mLogger.debug("MqLocalQueue::backup() - Backup file doesn't exist. Creating it... " + Boolean.toString(success));
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
            // write queue details
            mLogger.debug("MqLocalQueue::backup() - Threshold=" + mThreshold + "; LastAccess=(" + getLastAccess() + ")");
            ostream.writeInt(mThreshold);
            ostream.writeObject(mLastAccessUser);
            ostream.writeLong(mLastAccessTimeStamp.getAsLong());
            ostream.writeObject(mLastAccessMethod);
            
            for (int i = 0; i < mQueueArray.length; ++i)
            {
              MessageQueue mq = mQueueArray[i];
              while (!mq.isEmpty())
              {
                IMqMessage message = mq.poll();
                
                PacketHeader header = message.createHeader();
                header.serialize(ostream);
                message.serialize(ostream);
                mLogger.diag("MqLocalQueue::backup() - Header="  + StringUtils.asPrintableString(header));
                mLogger.diag("MqLocalQueue::backup() - Message=" + StringUtils.asPrintableString(message));
                
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
    }
    
    mLogger.debug("MqLocalQueue::backup() - OUT, Returns=" + Boolean.toString(success));
    return success;
  }
  
  /**
   * Expire old messages
   * 
   * @return the total number of messages expired
   */
  public int expire()
  {
    mLogger.debug("MqLocalQueue::expire() - IN");
    
    int total = 0;
    for (int prio = IMqConstants.cMaximumPriority; prio >= IMqConstants.cMinimumPriority; --prio)
    {
      MessageQueue mdq = mQueueArray[prio];
      for (IMqMessage msg : mdq)
      {
        if (msg.isExpired())
        {
          mdq.remove(msg);
        }
      }
    }
    
    setLastAccess(IMqConstants.cSystemUserName, "expire");
    
    mLogger.debug("MqLocalQueue::expire() - OUT, Returns=" + total);
    return total;
  }
  
  /**
   * Put a message into this {@link MqLocalQueue} object.
   * 
   * @param message The message that should be stored at this {@link MqLocalQueue} object.
   * @param updateLastAccess whether to update the last access fields for this operation
   * @return {@code true} if message was added, {@code false} otherwise.
   */
  public boolean internalPut(IMqMessage message, boolean updateLastAccess)
  {
    mLogger.debug("MqLocalQueue::internalPut() - IN");
    
    boolean success = false;
    if ((mThreshold == 0) || (size() < mThreshold))
    {
      int prio = message.getPriority();
      success = mQueueArray[prio].offer(message);
      
      String user = message.getStringProperty(IMqConstants.cKasPropertyPutUserName, IMqConstants.cSystemUserName);
      if (updateLastAccess) setLastAccess(user, "put");
    }
    
    mLogger.debug("MqLocalQueue::internalPut() - OUT, Returns=" + success);
    return success;
  }
  
  /**
   * Put a message into this {@link MqLocalQueue} object.
   * 
   * @param message The message that should be stored at this {@link MqLocalQueue} object.
   * @return {@code true} if message was added, {@code false} otherwise.
   */
  public boolean internalPut(IMqMessage message)
  {
    return internalPut(message, true);
  }
  
  /**
   * Get the {@link IMqMessage message} with the highest priority from this {@link MqLocalQueue} object.<br>
   * <br>
   * Since the actual message container is implemented by {@link MessageQueue}, the actual "get" operations
   * are translated to {@link MessageQueue#poll()}.<br>
   * Polling is done by calling the {@link MessageQueue#poll()} method and then suspend the thread
   * execution for {@code interval} milliseconds.<br>
   * <br>
   * If a message is not available, the method will poll for one until {@code timeout} expires.
   * If {@code timeout} is 0, the method will poll indefinitely.
   * 
   * @param timeout The timeout until which the method will give up
   * @param interval The gap length between polling operations
   * @return The {@link IMqMessage} or {@code null} if one is unavailable
   */
  protected IMqMessage internalGet(long timeout, long interval)
  {
    mLogger.debug("MqLocalQueue::get() - IN, Timeout=" + timeout + ", Interval=" + interval);
    
    IMqMessage result = null;
    
    long millisPassed = 0;
    boolean timeoutExpired = false;
    while ((result == null) && (!timeoutExpired))
    {
      int priority = internalGetPriorityIndex();
      if (priority > -1)
      {
        result = mQueueArray[priority].poll();
      }
      else
      {
        RunTimeUtils.sleepForMilliSeconds(interval);
        millisPassed += interval;
        
        if (timeout != 0)
          timeoutExpired = millisPassed >= timeout;
      }
    }
    
    mLogger.debug("MqLocalQueue::get() - OUT");
    return result;
  }
  
  /**
   * Find the first non-empty {@link MessageQueue} object in the queue array.
   * 
   * @return the index of the first non-empty queue, or -1 if all are empty.
   */
  private int internalGetPriorityIndex()
  {
    for (int prio = IMqConstants.cMaximumPriority; prio >= IMqConstants.cMinimumPriority; --prio)
    {
      int size;
      size = mQueueArray[prio].size();
      if (size > 0) return prio;
    }
    return -1;
  }
  
  /**
   * Set the last access to the queue
   * 
   * @param user Last user to access this {@link MqLocalQueue}
   * @param method The last method that was used to access this {@link MqLocalQueue}
   */
  public synchronized void setLastAccess(String user, String method)
  {
    mLastAccessUser = user;
    mLastAccessTimeStamp = TimeStamp.now();
    mLastAccessMethod = method;
  }
  
  /**
   * Response to Query request
   * 
   * @param all Whether to include all data in the response
   * @return a string that describes the queue 
   */
  public String queryResponse(boolean all)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("Queue.............: ").append(mName);
    if (all)
    {
      sb.append('\n');
      sb.append("  Owned by...: ").append(mManager.getName()).append('\n');
      sb.append("  Accessed...: ").append(getLastAccess()).append('\n');
      sb.append("  Threshold..: ").append(mThreshold).append('\n');
      sb.append("  Backup.....: ").append(mBackup).append('\n');
      sb.append("  Messages...: ").append(size()).append('\n');
    }
    return sb.toString();
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
      .append(pad).append("  Backup=").append(mBackup).append("\n")
      .append(pad).append("  Threshold=").append(mThreshold).append("\n")
      .append(pad).append("  LastAccess=(\n")
      .append(pad).append("    By=").append(mLastAccessUser).append("\n")
      .append(pad).append("    At=").append(mLastAccessTimeStamp.toString()).append("\n")
      .append(pad).append("    For=").append(mLastAccessMethod).append("\n")
      .append(pad).append("  )\n")
      .append(pad).append("  QueueArray=(\n");
    
    for (int i = 0; i < mQueueArray.length; ++i)
      sb.append(pad).append("    P").append(String.format("%02d=(", i)).append(mQueueArray[i].toPrintableString(level+2)).append(")\n");
    
    sb.append(pad).append("  )\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
