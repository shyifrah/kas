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
import javax.jms.JMSException;
import com.kas.comm.IPacketFactory;
import com.kas.comm.impl.PacketHeader;
import com.kas.infra.base.AKasObject;
import com.kas.infra.utils.RunTimeUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.KasqMessage;
import com.kas.q.KasqSession;

public abstract class AKasqDestination extends AKasObject implements IKasqDestination
{
  private static final long serialVersionUID = 1L;
  
  /***************************************************************************************************************
   * 
   */
  private static ILogger sLogger = LoggerFactory.getLogger(AKasqDestination.class);
  private static IPacketFactory sMessageFactory = new KasqMessageFactory();
  
  /***************************************************************************************************************
   * 
   */
  private String  mName;
  private String  mManagerName;
  
  protected transient KasqSession  mSession;
  protected transient MessageDeque mQueue;
  protected transient File    mBackupFile;
  protected transient String  mBackupFileName = null;
  
  /***************************************************************************************************************
   * Constructs a {@code AKasqDestination} object, specifying its name and owning manager
   * 
   * @param name the name associated with this destination
   * @param managerName the name of the manager of this destination
   */
  protected AKasqDestination(String name, String managerName)
  {
    this(name, managerName, null);
  }
  
  /***************************************************************************************************************
   * Constructs a {@code AKasqDestination} object, specifying its name, owning manager and owning session
   * 
   * @param name the name associated with this destination
   * @param managerName the name of the manager of this destination
   */
  protected AKasqDestination(String name, String managerName, KasqSession session)
  {
    mName = name;
    mManagerName = managerName;
    mBackupFile = null;
    mSession = session;
  }
  
  /***************************************************************************************************************
   * 
   */
  public boolean init()
  {
    sLogger.debug("AKasqDestination::init() - IN, name=[" + mName + "]");
    boolean success = true;
    
    mQueue  = new MessageDeque();
    
    sLogger.info("Starting initialization for " + mName);
    try
    {
      String fileName = getBackupFileName();
      sLogger.debug("AKasqDestination::init() - Check if backup file exists. Backup file=[" + fileName + "]...");
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
            sLogger.debug("AKasqDestination::init() - Exception caught while trying to restore " + mName + " contents ", e);
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
      sLogger.debug("AKasqDestination::init() - Exception caught during initialization. Name=" + mName + "; Exception: ", e);
      success = false;
    }
    
    sLogger.debug("AKasqDestination::init() - OUT, Returns=" + success);
    return success;
  }
  
  /***************************************************************************************************************
   * 
   */
  public boolean term()
  {
    sLogger.debug("AKasqDestination::term() - IN, name=[" + mName + "]");
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
      sLogger.debug("AKasqDestination::term() - Exception caught during termination. Name=" + mName + "; Exception: ", e);
      success = false;
    }
    
    sLogger.debug("AKasqDestination::term() - OUT, Returns=" + success);
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
  protected abstract IKasqMessage requestReply(IKasqMessage request) throws JMSException;
  
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
   * Delete this destination.
   * Since all destination implementation types are derived from this class, this method has no effect for
   * the permanent types. 
   * 
   * @throws JMSException 
   */
  protected void internalDelete() throws JMSException
  {
    if ((mSession != null) && (mName.startsWith("KAS.TEMP.")))
    {
      sLogger.debug("AKasqDestination::internalLocateDestination() - IN");
      
      int responseCode = IKasqConstants.cPropertyResponseCode_Fail;
      String msg = "";
      try
      {
        KasqMessage deleteRequest = new KasqMessage();
        deleteRequest.setIntProperty(IKasqConstants.cPropertyRequestType, IKasqConstants.cPropertyRequestType_Delete);
        deleteRequest.setStringProperty(IKasqConstants.cPropertyDestinationName, mName);
        deleteRequest.setIntProperty(IKasqConstants.cPropertyDestinationType, "queue".equals(getType()) ? 
            IKasqConstants.cPropertyDestinationType_Queue : IKasqConstants.cPropertyDestinationType_Topic );
        
        sLogger.debug("AKasqDestination::internalLocateDestination() - Sending delete request via message: " + deleteRequest.toPrintableString(0));
        IKasqMessage deleteResponse = requestReply(deleteRequest);
        
        sLogger.debug("KasqSession::internalLocateDestination() - Got response: " + deleteResponse.toPrintableString(0));
        responseCode = deleteResponse.getIntProperty(IKasqConstants.cPropertyResponseCode);
        msg = deleteResponse.getStringProperty(IKasqConstants.cPropertyResponseMessage);
      }
      catch (Throwable e)
      {
        sLogger.debug("AKasqDestination::internalCreateDestination() - Exception caught: ", e);
      }
      
      if (responseCode == IKasqConstants.cPropertyResponseCode_Fail)
        throw new JMSException("Failed to delete destination " + getFormattedName() + ". " + msg);
      
      sLogger.debug("AKasqDestination::internalLocateDestination() - OUT");
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
      StringBuffer sb = new StringBuffer();              // WINDOWS           LINUX
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
