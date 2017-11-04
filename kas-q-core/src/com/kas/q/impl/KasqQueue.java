package com.kas.q.impl;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayDeque;
import javax.jms.JMSException;
import javax.jms.TemporaryQueue;
import com.kas.infra.base.KasObject;
import com.kas.infra.utils.RunTimeUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.ext.IDestination;
import com.kas.q.ext.IMessage;
import com.kas.q.ext.impl.MessageSerializer;

public class KasqQueue extends KasObject implements IDestination, TemporaryQueue 
{
  private static final long serialVersionUID = 1L;
  private static ILogger    sLogger = LoggerFactory.getLogger(KasqQueue.class);
  
  /***************************************************************************************************************
   * 
   */
  private String  mName;
  private String  mManagerName;
  private File    mBackupFile;
  private ArrayDeque<IMessage> mQueue;
  
  /***************************************************************************************************************
   * Constructs a {@code KasqQueue} object, specifying its name
   * 
   * @param name the name associated with this queue
   * @param managerName the name of the manager of this queue
   */
  public KasqQueue(String name, String managerName)
  {
    mName   = name;
    mManagerName = managerName;
    mQueue  = new ArrayDeque<IMessage>();
    sLogger = LoggerFactory.getLogger(this.getClass());
    mBackupFile = null;
  }
  
  /***************************************************************************************************************
   * 
   */
  public boolean init()
  {
    sLogger.debug("KasqQueue::init() - IN, name=[" + mName + "]");
    boolean success = true;
    
    sLogger.info("Starting initialization for queue " + mName);
    try
    {
      String fileName = RunTimeUtils.getProductHomeDir() + "/repo/" + mName;
      sLogger.debug("KasqQueue::init() - Check if backup file exists. Backup file=[" + fileName + "]...");
      mBackupFile = new File(fileName);
      if (mBackupFile.exists())
      {
        sLogger.trace("Backup file [" + fileName + "] exists; Reading contents...");
        
        boolean error = false;
        boolean eof   = false;
        FileInputStream fis = new FileInputStream(mBackupFile);
        ObjectInputStream istream = new ObjectInputStream(fis);
        
        while ((!eof) && (!error))
        {
          try
          {
            IMessage message = MessageSerializer.deserialize(istream);
            put(message);
          }
          catch (EOFException e)
          {
            eof = true;
          }
          catch (Throwable e)
          {
            sLogger.debug("KasqQueue::init() - Exception caught while trying to restore queue contents ", e);
            error = true;
          }
        }
        
        // delete the file so we won't read the same messages upon next startup
        mBackupFile.delete();
        sLogger.trace("Queue contents successfully restored; Total read messages [" + mQueue.size() + "]");
      }
    }
    catch (Throwable e)
    {
      sLogger.debug("KasqQueue::init() - Exception caught during queue initialization. QueueName=" + mName + "; Exception: ", e);
      success = false;
    }
    
    sLogger.debug("KasqQueue::init() - OUT, Returns=" + success);
    return success;
  }
  
  /***************************************************************************************************************
   * 
   */
  public boolean term()
  {
    sLogger.debug("KasqQueue::term() - IN, name=[" + mName + "]");
    boolean success = true;
    
    sLogger.info("Starting termination for queue " + mName);
    try
    {
      if (!mQueue.isEmpty())
      {
        String fileName = RunTimeUtils.getProductHomeDir() + "/repo/" + mName;
        sLogger.trace("Queue is not empty. Saving messages to backup file=[" + fileName + "]");
        mBackupFile = new File(fileName);
        if (!mBackupFile.exists())
        {
          success = mBackupFile.createNewFile();
          if (!success)
          {
            sLogger.warn("Failed to create file: " + fileName);
          }
        }

        if (success)
        {
          // save all messages to file
          int msgCounter = 0;
          FileOutputStream fos = new FileOutputStream(mBackupFile);
          ObjectOutputStream ostream = new ObjectOutputStream(fos);
          while (!mQueue.isEmpty())
          {
            IMessage msg = mQueue.poll();
            MessageSerializer.serialize(ostream, msg);
            ++msgCounter;
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
          sLogger.trace("Total messages saved to file: " + msgCounter);
        }
      }
    }
    catch (Throwable e)
    {
      sLogger.debug("KasqQueue::term() - Exception caught during queue termination. QueueName=" + mName + "; Exception: ", e);
      success = false;
    }
    
    sLogger.debug("KasqQueue::term() - OUT, Returns=" + success);
    return success;
  }
  
  /***************************************************************************************************************
   * 
   */
  public String getQueueName() throws JMSException
  {
    return mName;
  }
  
  /***************************************************************************************************************
   * 
   */
  public void delete() throws JMSException
  {
    throw new JMSException("Unsupported method: TemporaryQueue.delete()");
  }
  
  /***************************************************************************************************************
   * 
   */
  public synchronized void put(IMessage message)
  {
    mQueue.offer(message);
  }
  
  /***************************************************************************************************************
   * 
   */
  public synchronized IMessage get()
  {
    return mQueue.peek();
  }
  
  /***************************************************************************************************************
   * 
   */
  public synchronized IMessage remove()
  {
    return mQueue.poll();
  }
  
  /***************************************************************************************************************
   * 
   */
  public int getSize()
  {
    return mQueue.size();
  }
  
  /***************************************************************************************************************
   * 
   */
  public String getName()
  {
    return mName;
  }
  
  /***************************************************************************************************************
   * 
   */
  public String toDestinationName()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("queue://")
      .append(mManagerName)
      .append('/')
      .append(mName);
    return sb.toString();
  }
  
  /***************************************************************************************************************
   * 
   */
  public String toString()
  {
    return toDestinationName();
  }
  
  /***************************************************************************************************************
   * 
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    
    sb.append(name()).append("(\n")
      .append(pad).append("  Name=").append(mName).append("\n")
      .append(pad).append("  QueueSize=").append(mQueue.size()).append("\n")
      .append(pad).append(")\n");
    
    return sb.toString();
  }
}
