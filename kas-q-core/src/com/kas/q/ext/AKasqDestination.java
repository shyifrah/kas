package com.kas.q.ext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.LinkedBlockingDeque;
import com.kas.comm.IPacketFactory;
import com.kas.comm.impl.PacketHeader;
import com.kas.infra.base.AKasObject;
import com.kas.infra.utils.RunTimeUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;

public abstract class AKasqDestination extends AKasObject implements IKasqDestination
{
  private static final long serialVersionUID = 1L;
  private static ILogger sLogger = LoggerFactory.getLogger(AKasqDestination.class);
  private static IPacketFactory sMessageFactory = new KasqMessageFactory();
  
  /***************************************************************************************************************
   * 
   */
  private String  mName;
  private String  mManagerName;
  private File    mBackupFile;
  private LinkedBlockingDeque<IKasqMessage> mQueue;
  
  private String  mBackupFileName = null;
  
  /***************************************************************************************************************
   * Constructs a {@code KasqDestination} object, specifying its name
   * 
   * @param name the name associated with this destination
   * @param managerName the name of the manager of this destination
   */
  protected AKasqDestination(String name, String managerName)
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
      String fileName = getBackupFileName();
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
            IKasqMessage message = (IKasqMessage)sMessageFactory.createFromStream(istream);
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
        
        // closing the file
        try
        {
          fis.close();
          istream.close();
        }
        catch (IOException e) {}
        
        // deleting it so we won't read again the same messages 
        String abspath = mBackupFile.getAbsolutePath();
        try
        {
          Path path = Paths.get(abspath);
          Files.delete(path);
        }
        catch (IOException e)
        {
          sLogger.warn("Failed to delete backup file " + abspath + ", exception: ", e);
        }
        
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
        String fileName = getBackupFileName();
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
            
            PacketHeader header = msg.createHeader();
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
  protected void internalDelete()
  {
    if (mName.startsWith("KAS.TEMP."))
    {
      // TODO: delete the queue/topic
    }
  }
  
  /***************************************************************************************************************
   * Constructs (if wasn't done before) and returns the backup file name of the destination
   * 
   * @returns full-path backup file name
   */
  protected String getBackupFileName()
  {
    if (mBackupFileName == null)
    {
      StringBuffer sb = new StringBuffer();
      sb.append(RunTimeUtils.getProductHomeDir())        // C:\app\kas        /opt/kas          << path of kasq
        .append(File.separatorChar)                      // \                 /
        .append("repo")                                  // repo              repo              << repo directory
        .append(File.separatorChar)                      // \                 /
        .append(getName())                               // shy.admin.queue   /shy.test.topic   << name of destination
        .append('.')                                     // .                 .
        .append(getType().substring(0,1))                // q                 t                 << first char of type
        .append("bk");                                   // bk                bk                << "bk", designating backup
      mBackupFileName = sb.toString();
    }
    return mBackupFileName;
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
