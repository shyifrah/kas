package com.kas.logging.appender.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import com.kas.infra.base.TimeStamp;
import com.kas.infra.base.threads.ThreadPool;
import com.kas.infra.logging.ELogLevel;
import com.kas.infra.utils.FileUtils;
import com.kas.infra.utils.RunTimeUtils;
import com.kas.logging.appender.AAppender;
import com.kas.logging.impl.FileAppenderConfiguration;

/**
 * The Appender that responsible for writing to the log file
 * 
 * @author Pippo
 */
public class FileAppender extends AAppender implements IMessageDataWriter
{
  static private final int cBytesPerMB = 1024 * 1024;
  
  /**
   * The appender's configuration
   */
  private FileAppenderConfiguration mConfig = null;
  
  /**
   * The {@link FileWriter}
   */
  private FileWriter mFileWriter = null;
  
  /**
   * The {@link BufferedWriter}
   */
  private BufferedWriter mBufferedWriter = null;
  
  /**
   * Number of errors occurred during writing
   */
  private int mErrorCount = 0;
  
  /**
   * Number of write operations
   */
  private int mWriteCount = 0;
  
  /**
   * The name of the log file
   */
  private String mFileName = "kas.log";
  
  /**
   * The {@link File} referencing the log file
   */
  private File mLogFile = null;
  
  /**
   * Queue of messages, in use only for asynchronous file appender 
   */
  private Queue<MessageData> mMessages = new ConcurrentLinkedDeque<>();
  
  /**
   * The write task
   */
  private FileAppenderWriter mAsyncWriter = null;
  
  /**
   * Construct a {@link FileAppender} with the specified configuration
   * 
   * @param fac The {@link FileAppenderConfiguration}
   */
  public FileAppender(FileAppenderConfiguration fac)
  {
    mConfig = fac;
  }
  
  /**
   * Initialize the {@link FileAppender}
   * 
   * @param fac The {@link FileAppenderConfiguration}
   * @return {@code true} always.
   * 
   * @see com.kas.infra.base.IInitializable#init()
   */
  public synchronized boolean init()
  {
    TimeStamp ts = TimeStamp.now();
    
    mFileName = RunTimeUtils.getProductHomeDir() + File.separator + "logs" + File.separator + mConfig.getFileNamePattern()
      .replaceAll("%p", Integer.toString(RunTimeUtils.getProcessId()))
      .replaceAll("%u", RunTimeUtils.getUserId())
      .replaceAll("%d", ts.getDateString())
      .replaceAll("%t", ts.getTimeString());
    
    mLogFile = initLogFile();
    
    mErrorCount  = 0;
    mWriteCount  = 0;
    
    if (mConfig.isAsyncEnabled())
    {
      mAsyncWriter = new FileAppenderWriter(this, mMessages);
      ThreadPool.scheduleAtFixedRate(mAsyncWriter, 0L, mConfig.getAsyncInterval(), TimeUnit.MILLISECONDS);
    }
    
    return true;
  }
  
  /**
   * Terminate the {@link FileAppender}
   * 
   * @return {@code true} always.
   * 
   * @see com.kas.infra.base.IInitializable#term()
   */
  public synchronized boolean term()
  {
    try
    {
      if (mBufferedWriter != null)
        mBufferedWriter.flush();
      if (mFileWriter != null)
        mFileWriter.close();
    }
    catch (IOException e) {}
    
    mBufferedWriter = null;
    mFileWriter     = null;
    mLogFile        = null;
    mConfig         = null;
    
    return true;
  }
  
  /**
   * Initialize a log file
   * 
   * @return the {@link File} referencing the log file
   */
  private File initLogFile()
  {
    mLogFile = new File(mFileName);
    FileUtils.verifyExists(mLogFile);

    if ((mLogFile != null) && (mLogFile.canWrite()))
    {
      try
      {
        mFileWriter      = new FileWriter(mLogFile, mLogFile.exists());
        mBufferedWriter  = new BufferedWriter(mFileWriter);
      }
      catch (IOException e)
      {
        System.out.println("Failed to create Writer for file " + mLogFile.getAbsolutePath());
        e.printStackTrace();
        mLogFile        = null;
        mFileWriter     = null;
        mBufferedWriter = null;
      }
    }
    
    return mLogFile;
  }
  
  /**
   * Write a message to the log file
   * 
   * @param logger The name of the logger
   * @param level the {@link ELogLevel} of the message
   * @param message The message text to write
   */
  protected synchronized void write(String logger, ELogLevel messageLevel, String message)
  {
    if (mConfig.isEnabled())
    {
      if (mConfig.getLogLevel().isGreaterOrEqual(messageLevel))
      {
        MessageData md = new MessageData(TimeStamp.now(), RunTimeUtils.getThreadId(), messageLevel, logger, message);
        
        if (mConfig.isAsyncEnabled())
          mMessages.offer(md);
        else
          write(md);
      }
    }
  }
  
  /**
   * Actual writing is done via this method
   * 
   * @param md The {@link MessageData} describing the message to write
   */
  public void write(MessageData md)
  {
    try
    {
      mBufferedWriter.write(md.format());
      ++mWriteCount;
      
      flushAndArchive(); // might throw IOException if archiving fails
    }
    catch (IOException e)
    {
      ++mErrorCount;
      if (mErrorCount >= mConfig.getMaxWriteErrors())
      {
        term();
      }
    }
  }
  
  /**
   * Flush writings and archive current log file if necessary
   *  
   * @throws IOException if an I/O error occurs
   */
  private void flushAndArchive() throws IOException
  {
    boolean flushed = flush();
    if (flushed)
    {
      archive();
    }
  }
  
  /**
   * Flush buffered writings.<br>
   * <br>
   * Flushing takes place every few writing operations, as configured.
   * 
   * @return {@code true} if the {@link BufferedWriter#flush()} was called, {@code false} otherwise
   * 
   * @see com.kas.logging.impl.FileAppenderConfiguration#getFlushRate()
   */
  private boolean flush()
  {
    boolean flushed = false;
    try
    {
      if (mWriteCount % mConfig.getFlushRate() == 0)
      {
        mBufferedWriter.flush();
        flushed = true;
      }
    }
    catch (IOException e) {}
    return flushed;
  }
  
  /**
   * Archive current log file.<br>
   * <br>
   * When a log file size has reached the maximum size, as configured, it is archived by renaming
   * its suffix to .X, where "X" is a number between 1 to a value configured.<br>
   * The oldest log file, if necessary, are deleted.
   * 
   * @throws IOException if an I/O error occurs
   * 
   * @see com.kas.logging.impl.FileAppenderConfiguration#getArchiveTestSizeRate()
   * @see com.kas.logging.impl.FileAppenderConfiguration#getArchiveMaxGenerations()
   * @see com.kas.logging.impl.FileAppenderConfiguration#getArchiveMaxFileSizeMb()
   */
  private void archive() throws IOException
  {
    if (mWriteCount % mConfig.getArchiveTestSizeRate() == 0) // should test file size?
    {
      long logFileSize = mLogFile.length();

      if (logFileSize >= mConfig.getArchiveMaxFileSizeMb() * cBytesPerMB)  // is file size > max file size?
      {
        // scan all generations from the oldest to the newest
        for (int generationIndex = mConfig.getArchiveMaxGenerations(); generationIndex >= 1; --generationIndex)
        {
          // get generation file
          String generationFileName = mFileName + "." + Integer.toString(generationIndex);
          File   generationFile     = new File(generationFileName);
          
          // if the generation exists
          if (generationFile.exists())
          {
            // delete it (if it's the last generation) or rename it
            if (generationIndex == mConfig.getArchiveMaxGenerations())
            {
              boolean deleted = generationFile.delete();
              if (!deleted)
              {
                throw new IOException("Failed to delete last generation file " + generationFileName);
              }
            }
            else
            {
              int nextGenerationIndex = generationIndex + 1;
              String nextGenerationFileName = mFileName + "." + Integer.toString(nextGenerationIndex);
              File   nextGenerationFile     = new File(nextGenerationFileName);
              boolean renamed = generationFile.renameTo(nextGenerationFile);
              if (!renamed)
              {
                throw new IOException("Failed to rename generation file " + generationFileName + " to " + nextGenerationFileName);
              }
            }
          }
        }
        
        // the "current file" - close buffer, rename to 1st generation and create new current file
        try
        {
          mFileWriter.close();
        }
        catch (IOException e)
        {
          throw new IOException("Failed to close file writer. Message: " + e.getMessage(), e);
        }
        String nextGenerationFileName = mFileName + ".1";
        File   nextGenerationFile     = new File(nextGenerationFileName);
        boolean renamed = mLogFile.renameTo(nextGenerationFile);
        if (!renamed)
        {
          throw new IOException("Failed to rename generation file " + mFileName + " to " + nextGenerationFileName);
        }
        
        mBufferedWriter = null;
        mFileWriter     = null;
        mLogFile        = null;
        
        mLogFile = initLogFile();
      }
    }
  }
  
  /**
   * Returns the {@link FileAppender} string representation.
   * 
   * @param level the required level padding
   * @return the object's printable string representation
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n")
      .append(pad).append("  ").append("Config=").append(mConfig.toPrintableString(level+1)).append("\n")
      .append(pad).append("  ").append("LogFile=").append(mLogFile.getAbsolutePath()).append("\n")
      .append(pad).append("  ").append("WriteCount=").append(mWriteCount).append("\n")
      .append(pad).append("  ").append("ErrorCount=").append(mErrorCount).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
