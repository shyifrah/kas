package com.kas.logging.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import com.kas.infra.base.TimeStamp;
import com.kas.infra.utils.FileUtils;
import com.kas.infra.utils.RunTimeUtils;

public class FileAppender extends AAppender
{
  private static final int cBytesPerMB = 1024 * 1024;
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private FileAppenderConfiguration mConfig          = null;
  private FileWriter                mFileWriter      = null;
  private BufferedWriter            mBufferedWriter  = null;
  private int                       mErrorCount      = 0;
  private int                       mWriteCount      = 0;
  private String                    mFileName        = FileAppenderConfiguration.cDefaultLogFileName;
  private File                      mLogFile         = null;
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public FileAppender(FileAppenderConfiguration fac)
  {
    mConfig = fac;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public synchronized boolean init()
  {
    TimeStamp ts = new TimeStamp();
    
    mFileName = RunTimeUtils.getProductHomeDir() + File.separator + "logs" + File.separator + mConfig.getFileNamePattern()
      .replaceAll("%p", Integer.toString(RunTimeUtils.getProcessId()))
      .replaceAll("%u", RunTimeUtils.getUserId())
      .replaceAll("%d", ts.getDateString())
      .replaceAll("%t", ts.getTimeString());
    
    mLogFile = initLogFile();
    
    mErrorCount  = 0;
    mWriteCount  = 0;
    
    return true;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
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
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
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
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  protected synchronized void write(String logger, String message, ELogLevel messageLevel)
  {
    if (mConfig.isEnabled())
    {
      if (mConfig.getLogLevel().isGreaterOrEqual(messageLevel))
      {
        TimeStamp ts = new TimeStamp();
        try
        {
          mBufferedWriter.write(String.format(cAppenderMessageFormat, 
            ts.toString(),
            RunTimeUtils.getProcessId(),
            RunTimeUtils.getThreadId(),
            messageLevel.toString(), 
            logger,
            message));
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
    }
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private void flushAndArchive() throws IOException
  {
    boolean flushed = flush();
    if (flushed)
    {
      archive();
    }
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
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
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
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
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    
    sb.append(name()).append("(\n")
      .append(pad).append("  ").append("Config=").append(mConfig.toPrintableString(level+1)).append("\n")
      .append(pad).append("  ").append("LogFile=").append(mLogFile.getAbsolutePath()).append("\n")
      .append(pad).append("  ").append("WriteCount=").append(mWriteCount).append("\n")
      .append(pad).append("  ").append("ErrorCount=").append(mErrorCount).append("\n")
      .append(pad).append(")");
    
    return sb.toString();
  }
}
