package com.kas.q;

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
import java.util.concurrent.TimeUnit;
import javax.jms.JMSException;
import com.kas.comm.IPacketFactory;
import com.kas.comm.impl.PacketHeader;
import com.kas.infra.base.AKasObject;
import com.kas.infra.utils.RunTimeUtils;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.ext.EDestinationType;
import com.kas.q.ext.IKasqConstants;
import com.kas.q.ext.IKasqDestination;
import com.kas.q.ext.IKasqMessage;
import com.kas.q.ext.KasqMessageFactory;

public class KasqDestination extends AKasObject implements IKasqDestination
{
  /***************************************************************************************************************
   * "typedef" class
   */
  class MessageDeque extends LinkedBlockingDeque<IKasqMessage>
  {
    private static final long serialVersionUID = 1L;
  }
  
  private static final long serialVersionUID = 1L;
  
  /***************************************************************************************************************
   * 
   */
  private static ILogger sLogger = LoggerFactory.getLogger(KasqDestination.class);
  private static IPacketFactory sMessageFactory = new KasqMessageFactory();
  
  /***************************************************************************************************************
   * 
   */
  private EDestinationType mType;
  
  private String  mName;
  private String  mManagerName;
  
  protected transient KasqSession  mSession;
  protected transient MessageDeque [] mQueues;
  protected transient File    mBackupFile;
  protected transient String  mBackupFileName = null;
  
  /***************************************************************************************************************
   * Constructs a {@code KasqDestination} object, specifying its name and owning manager
   * 
   * @param name the name associated with this destination
   * @param managerName the name of the manager of this destination
   */
  protected KasqDestination(EDestinationType type, String name, String managerName)
  {
    this(type, name, managerName, null);
  }
  
  /***************************************************************************************************************
   * Constructs a {@code KasqDestination} object, specifying its name, owning manager and owning session
   * 
   * @param name the name associated with this destination
   * @param managerName the name of the manager of this destination
   */
  protected KasqDestination(EDestinationType type, String name, String managerName, KasqSession session)
  {
    mType = type;
    mName = name;
    mManagerName = managerName;
    mBackupFile = null;
    mSession = session;
    
    mQueues = new MessageDeque [10];
    for (int i = 0; i < mQueues.length; ++i)
      mQueues[i] = new MessageDeque();
  }
  
  /***************************************************************************************************************
   * 
   */
  public boolean init()
  {
    sLogger.debug("KasqDestination::init() - IN, name=[" + mName + "]");
    boolean success = true;
    
    if (mQueues == null)
    {
      mQueues = new MessageDeque [10];
      for (int i = 0; i < mQueues.length; ++i)
        mQueues[i] = new MessageDeque();
    }
    
    sLogger.info("Starting initialization for " + mName);
    try
    {
      String fileName = getBackupFileName();
      sLogger.debug("KasqDestination::init() - Check if backup file exists. Backup file=[" + fileName + "]...");
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
            sLogger.debug("KasqDestination::init() - Exception caught while trying to restore " + mName + " contents ", e);
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
        
        sLogger.trace("Contents successfully restored; Total read messages [" + size() + "]");
      }
    }
    catch (Throwable e)
    {
      sLogger.debug("KasqDestination::init() - Exception caught during initialization. Name=" + mName + "; Exception: ", e);
      success = false;
    }
    
    sLogger.debug("KasqDestination::init() - OUT, Returns=" + success);
    return success;
  }
  
  /***************************************************************************************************************
   * 
   */
  public boolean term()
  {
    sLogger.debug("KasqDestination::term() - IN, name=[" + mName + "]");
    boolean success = true;
    
    sLogger.info("Starting termination for " + mName);
    try
    {
      if (!isEmpty())
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
          for (int i = 0; i < mQueues.length; ++i)
          {
            MessageDeque msgDeq = mQueues[i];
            while (!msgDeq.isEmpty())
            {
              IKasqMessage msg = msgDeq.poll();
              
              PacketHeader header = msg.createHeader();
              header.serialize(ostream);
              msg.serialize(ostream);
              
              ++msgCounter;
            }
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
      sLogger.debug("KasqDestination::term() - Exception caught during termination. Name=" + mName + "; Exception: ", e);
      success = false;
    }
    
    sLogger.debug("KasqDestination::term() - OUT, Returns=" + success);
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
  public EDestinationType getType()
  {
    return mType;
  }
  
  /***************************************************************************************************************
   * 
   */
  public void put(IKasqMessage message) throws JMSException
  {
    int prio = message.getJMSPriority();
    mQueues[prio].offer(message);
  }
  
  /***************************************************************************************************************
   * 
   */
  public IKasqMessage get(long timeout) throws InterruptedException
  {
    MessageDeque msgDeq = null;
    for (int i = 9; i >= 0; --i)
    {
      msgDeq = mQueues[i];
      if (!msgDeq.isEmpty())
        break;
    }
    
    return msgDeq.poll(timeout, TimeUnit.MILLISECONDS);
  }
  
  /***************************************************************************************************************
   * 
   */
  public IKasqMessage getMatching(boolean noLocal, String session, String selector)
  {
    sLogger.debug("KasqDestination::getMatching() - IN");
    
    IKasqMessage message = null;
    MessageDeque msgDeq = null;
    for (int i = 9; i >= 0; --i)
    {
      sLogger.debug("KasqDestination::getMatching() - Looking for matches in Queue P" + i);
      msgDeq = mQueues[i];
      if (!msgDeq.isEmpty())
      {
        message = getMatchingMessage(msgDeq, noLocal, session, selector);
        if (message != null)
          break;
      }
    }
    
    sLogger.debug("KasqDestination::getMatching() - OUT, Result=" + StringUtils.asPrintableString(message));
    return message;
  }
  
  /***************************************************************************************************************
   * 
   */
  private IKasqMessage getMatchingMessage(MessageDeque queue, boolean noLocal, String session, String selector)
  {
    sLogger.diag("KasqDestination::getMatchingMessage() - IN");
    
    IKasqMessage message = null;
    for (IKasqMessage candidate : queue)
    {
      String prodSession = null;
      Long prodDeliveryDelay = null;
      Long prodTimestamp = null;
      Long expirationTimestamp = null;
      try
      {
        prodSession = candidate.getStringProperty(IKasqConstants.cPropertyProducerSession);
        prodDeliveryDelay = candidate.getLongProperty(IKasqConstants.cPropertyProducerDeliveryDelay);
        prodTimestamp = candidate.getLongProperty(IKasqConstants.cPropertyProducerTimestamp);
        expirationTimestamp = candidate.getJMSExpiration();
      }
      catch (JMSException e) {}
      
      // if no-local messages are allowed AND consumer & producer sessions are the same - skip this message
      if ((noLocal) && (session != null))
      {
        if (session.equals(prodSession))
        {
          sLogger.diag("KasqDestination::getMatchingMessage() - NoLocal messages are forbidden, continue...");
          continue;
        }
      }
      
      long now = System.currentTimeMillis();
      
      // if delivery delay not reached - skip this message
      if ((prodDeliveryDelay != null) && (prodTimestamp != null))
      {
        if (now < prodTimestamp + prodDeliveryDelay)
        {
          sLogger.diag("KasqDestination::getMatchingMessage() - DeliveryDelay not yet expired, continue...");
          continue;
        }
      }
      
      // if candidate message has expiration date and it's expired - skip this message
      if ((expirationTimestamp != null) && (expirationTimestamp > 0))
      {
        if (now > expirationTimestamp)
        {
          sLogger.diag("KasqDestination::getMatchingMessage() - Cannot deliver expired message, continue...");
          continue;
        }
      }
      
      // if candidate message does not pass selector - skip this message;
      if (selector != null)
      {
        //
        // TODO: implement
        // need to verify if the candidate message matches the selector expression
        //
        boolean matched = true;
        if (!matched)
        {
          sLogger.diag("KasqDestination::getMatchingMessage() - Message not matching selector, continue...");
          continue;
        }
      }
      
      message = candidate;
      break;
    }
    
    // if loop was broken because we found a message, remove it from queue
    if (message != null)
      remove(message);
    
    sLogger.diag("KasqDestination::getMatchingMessage() - OUT, Result=" + StringUtils.asPrintableString(message));
    return message;
  }
  
  /***************************************************************************************************************
   * 
   */
  private void remove(IKasqMessage message)
  {
    boolean removed = false;
    for (MessageDeque q : mQueues)
    {
      removed = q.remove(message);
      if (removed)
        return;
    }
  }
  
  /***************************************************************************************************************
   * 
   */
  public boolean isEmpty()
  {
    for (MessageDeque q : mQueues)
    {
      if (q.size() > 0)
        return false;
    }
    return true;
  }
  
  /***************************************************************************************************************
   * 
   */
  public int size()
  {
    int sum = 0;
    for (MessageDeque q : mQueues)
    {
      sum += q.size();
    }
    return sum;
  }

  /***************************************************************************************************************
   * Delete this destination.<br>
   * Since all destination implementation types are derived from this class, this method has no effect for
   * the permanent types. 
   * 
   * @throws JMSException 
   */
  protected void internalDelete() throws JMSException
  {
    sLogger.debug("KasqDestination::internalLocateDestination() - IN");
    
    if ((mSession != null) && (mName.startsWith("KAS.TEMP.")))
    {
      mSession.internalDeleteTemporaryDestination(mName, mType);
    }
    
    sLogger.debug("KasqDestination::internalLocateDestination() - OUT");
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
        .append(getType().toString().substring(0,1))     // q                 t                 << first char of type
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
    sb.append(getType().toString())
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
      .append(pad).append("  Type=").append(getType().toString()).append("\n")
      .append(pad).append("  Queues=(\n");
    
    for (int i = 0; i < mQueues.length; ++i)
      sb.append(pad).append("    P").append(String.format("%02d", i)).append("-size=[").append(mQueues[i].size()).append("]\n");
    
    sb.append(pad).append("  )\n")
      .append(pad).append("  TotalSize=").append(size()).append("\n")
      .append(pad).append(")\n");
    return sb.toString();
  }
}
