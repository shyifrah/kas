package com.kas.q;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.LinkedBlockingDeque;
import com.kas.comm.MessageFactory;
import com.kas.comm.impl.MessageHeader;
import com.kas.infra.base.KasObject;
import com.kas.infra.utils.RunTimeUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.ext.IKasqDestination;
import com.kas.q.ext.IKasqMessage;

public abstract class KasqDestination extends KasObject implements IKasqDestination
{
  private static final long serialVersionUID = 1L;
  private static ILogger sLogger = LoggerFactory.getLogger(KasqDestination.class);
  
  /***************************************************************************************************************
   * 
   */
  private String  mName;
  private String  mManagerName;
  private File    mBackupFile;
  private LinkedBlockingDeque<IKasqMessage> mQueue;
  
  /***************************************************************************************************************
   * Constructs a {@code KasqDestination} object, specifying its name
   * 
   * @param name the name associated with this destination
   * @param managerName the name of the manager of this destination
   */
  protected KasqDestination(String name, String managerName)
  {
    mName   = name;
    mManagerName = managerName;
    mQueue  = new LinkedBlockingDeque<IKasqMessage>();
    mBackupFile = null;
  }
  
  /***************************************************************************************************************
   * 
   */
  public boolean init()
  {
    sLogger.debug("KasqQueue::init() - IN, name=[" + mName + "]");
    boolean success = true;
    
    sLogger.info("Starting initialization for " + mName);
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
            IKasqMessage message = (IKasqMessage)MessageFactory.getInstance().createFromStream(istream);
            put(message);
          }
          catch (IOException e)
          {
            eof = true;
          }
          catch (Throwable e)
          {
            sLogger.debug("KasqQueue::init() - Exception caught while trying to restore " + mName + " contents ", e);
            error = true;
          }
        }
        
        // delete the file so we won't read the same messages upon next startup
        mBackupFile.delete();
        sLogger.trace("Contents successfully restored; Total read messages [" + mQueue.size() + "]");
      }
    }
    catch (Throwable e)
    {
      sLogger.debug("KasqQueue::init() - Exception caught during initialization. Name=" + mName + "; Exception: ", e);
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
    
    sLogger.info("Starting termination for " + mName);
    try
    {
      if (!mQueue.isEmpty())
      {
        String fileName = RunTimeUtils.getProductHomeDir() + "/repo/" + mName;
        sLogger.trace(mName + " is not empty. Saving messages to backup file=[" + fileName + "]");
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
            IKasqMessage msg = mQueue.poll();
            
            MessageHeader header = new MessageHeader(msg.getMessageType());
            header.serialize(ostream);
            msg.serialize(ostream);
            
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
      sLogger.debug("KasqQueue::term() - Exception caught during termination. Name=" + mName + "; Exception: ", e);
      success = false;
    }
    
    sLogger.debug("KasqQueue::term() - OUT, Returns=" + success);
    return success;
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
  public abstract String getType();
  
  /***************************************************************************************************************
   * 
   */
  public void put(IKasqMessage message)
  {
    mQueue.offer(message);
  }
  
  /***************************************************************************************************************
   * 
   */
  public IKasqMessage peek()
  {
    return mQueue.peek();
  }
  
  /**************************************************************************************************************
   * 
   */
  public IKasqMessage get() 
  {
    IKasqMessage message = null;
    while (message == null)
    {
      try
      {
        message = mQueue.take();
      }
      catch (InterruptedException e) {}
    }
    return message;
  }
  
  /***************************************************************************************************************
   * 
   */
  public IKasqMessage getAndWait(long timeout)
  {
    long toWait = timeout;
    IKasqMessage message = getNoWait();
    while ((message == null) && (toWait > 0))
    {
      toWait -= 500;
      try
      {
        Thread.sleep(500);
      }
      catch (Throwable e) {}
      message = getNoWait();
    }
    return message;
  }

  /***************************************************************************************************************
   * 
   */
  public IKasqMessage getNoWait()
  {
    return mQueue.poll();
  }

  /***************************************************************************************************************
   * 
   */
  public int size()
  {
    return mQueue.size();
  }

  /***************************************************************************************************************
   * 
   */
  public String getFormattedName()
  {
    StringBuffer sb = new StringBuffer();
    sb.append(getType())
      .append("://")
      .append(mManagerName)
      .append('/')
      .append(mName);
    return sb.toString();
  }
  
  /***************************************************************************************************************
   * 
   */
  protected void internalDelete()
  {
    if (mName.startsWith("KAS.TEMP."))
    {
      // TODO: delete the queue/topic
    }
  }
  
  /***************************************************************************************************************
   * 
   */
  public String toString()
  {
    return getFormattedName();
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
      .append(pad).append("  Type=").append(getType()).append("\n")
      .append(pad).append("  Size=").append(mQueue.size()).append("\n")
      .append(pad).append(")\n");
    return sb.toString();
  }
}
