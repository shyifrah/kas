package com.kas.logging.appender.file;

import com.kas.logging.appender.AAppenderConfiguration;
import com.kas.logging.impl.LogSystem;
import com.kas.logging.impl.LoggingConfiguration;

/**
 * The {@link FileAppender} configuration object.
 * 
 * @author Pippo
 */
public class FileAppenderConfiguration extends AAppenderConfiguration
{
  static final String  cDefaultLogFileName           = "kas-%p-%d.log";
  static final int     cDefaultMaxWriteErrors        = 10;
  static final int     cDefaultFlushRate             = 10;
  static final int     cDefaultArchiveMaxFileSizeMb  = 10;
  static final int     cDefaultArchiveMaxGenerations = 5;
  static final int     cDefaultArchiveTestSizeRate   = 20;
  static final boolean cDefaultAsyncEnabled          = false;
  static final long    cDefaultAsyncInterval         = 1000L;
  
  /**
   * The log file name pattern
   */
  private String mFileNamePattern = cDefaultLogFileName;
  
  /**
   * The maximum number of write errors
   */
  private int mMaxWriteErrors = cDefaultMaxWriteErrors;
  
  /**
   * The number of write operations before flushing the appender
   */
  private int mFlushRate = cDefaultFlushRate;
  
  /**
   * The maximum log file size before archiving
   */
  private int mArchiveMaxFileSizeMb = cDefaultArchiveMaxFileSizeMb;
  
  /**
   * The maximum number of log file generations 
   */
  private int mArchiveMaxGenerations = cDefaultArchiveMaxGenerations;
  
  /**
   * The number of write operations before testing if the log file should be archived 
   */
  private int mArchiveTestSizeRate = cDefaultArchiveTestSizeRate;
  
  /**
   * Is async-appender enabled
   */
  protected boolean mAsyncEnabled = cDefaultAsyncEnabled;
  
  /**
   * Async interval length in Milliseconds
   */
  protected long mAsyncInterval = cDefaultAsyncInterval;
  
  /**
   * Construct the appender's configuration, providing the {@link LoggingConfiguration}
   */
  public FileAppenderConfiguration(LoggingConfiguration loggingConfig)
  {
    super(LogSystem.cFileAppenderName, loggingConfig);
  }
  
  /**
   * Refreshing the appender's configuration.<br>
   * <br>
   * When this method is called, it calls the {@link com.kas.config.MainConfiguration MainConfiguration} in order to re-read the values
   * of the relevant properties.
   * 
   * @see com.kas.logging.appender.AAppenderConfiguration#refresh()
   */
  public void refresh()
  {
    super.refresh();
    
    mFileNamePattern       = sMainConfig.getStringProperty ( cLoggingConfigPrefix + "appender." + mName + ".fileNamePattern"        , mFileNamePattern       );
    mMaxWriteErrors        = sMainConfig.getIntProperty    ( cLoggingConfigPrefix + "appender." + mName + ".maxWriteErrors"         , mMaxWriteErrors        );
    mFlushRate             = sMainConfig.getIntProperty    ( cLoggingConfigPrefix + "appender." + mName + ".flushRate"              , mFlushRate             );
    mArchiveMaxFileSizeMb  = sMainConfig.getIntProperty    ( cLoggingConfigPrefix + "appender." + mName + ".archive.maxFileSizeMb"  , mArchiveMaxFileSizeMb  );
    mArchiveMaxGenerations = sMainConfig.getIntProperty    ( cLoggingConfigPrefix + "appender." + mName + ".archive.maxGenerations" , mArchiveMaxGenerations );
    mArchiveTestSizeRate   = sMainConfig.getIntProperty    ( cLoggingConfigPrefix + "appender." + mName + ".archive.testSizeRate"   , mArchiveTestSizeRate   );
    mAsyncEnabled          = sMainConfig.getBoolProperty   ( cLoggingConfigPrefix + "appender." + mName + ".async.enabled"          , mAsyncEnabled          );
    mAsyncInterval         = sMainConfig.getLongProperty   ( cLoggingConfigPrefix + "appender." + mName + ".async.interval"         , mAsyncInterval         );
  }
  
  /**
   * Get the maximum number of write errors before shutting down the logger
   * 
   * @return the maximum number of write errors before shutting down the logger
   */
  public int getMaxWriteErrors()
  {
    return mMaxWriteErrors;
  }
  
  /**
   * Get the number of writes before a flushing the {@link java.io.BufferedWriter BufferedWriter}
   * 
   * @return the number of writes before a flushing the {@link java.io.BufferedWriter BufferedWriter}
   */
  public int getFlushRate()
  {
    return mFlushRate;
  }
  
  /**
   * Get the maximum size of a file, in MBs, before it's eligible for archiving
   * 
   * @return the maximum size of a file, in MBs, before it's eligible for archiving
   */
  public int getArchiveMaxFileSizeMb()
  {
    return mArchiveMaxFileSizeMb;
  }
  
  /**
   * Get the number of archive files
   * 
   * @return the number of archive files
   */
  public int getArchiveMaxGenerations()
  {
    return mArchiveMaxGenerations;
  }
  
  /**
   * Get the number of writes before checking if a log file should be archived
   * 
   * @return the number of writes before checking if a log file should be archived
   */
  public int getArchiveTestSizeRate()
  {
    return mArchiveTestSizeRate;
  }
  
  /**
   * Get the log file name pattern
   * 
   * @return the log file name pattern
   */
  public String getFileNamePattern()
  {
    return mFileNamePattern;
  }
  
  /**
   * Is appender work asynchronously
   * 
   * @return {@code true} if the appender is asynchronous, {@code false}
   */
  public boolean isAsyncEnabled()
  {
    return mAsyncEnabled;
  }
  
  /**
   * Get asynchronous writes interval length in milliseconds
   * 
   * @return asynchronous writes interval length in milliseconds
   */
  public long getAsyncInterval()
  {
    return mAsyncInterval;
  }
  
  /**
   * Returns the {@link FileAppenderConfiguration} string representation.
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
      .append(pad).append("  Enabled=").append(mEnabled).append("\n")
      .append(pad).append("  LogLevel=").append(mLogLevel.name()).append("\n")
      .append(pad).append("  FileNamePattern=").append(mFileNamePattern).append("\n")
      .append(pad).append("  MaxWriteErrors=").append(mMaxWriteErrors).append("\n")
      .append(pad).append("  FlushRate=").append(mFlushRate).append(" writes\n")
      .append(pad).append("  Archive.MaxFileSize=").append(mArchiveMaxFileSizeMb).append(" MBs\n")
      .append(pad).append("  Archive.MaxGenerations=").append(mArchiveMaxGenerations).append("\n")
      .append(pad).append("  Archive.TestSizeRate=").append(mArchiveTestSizeRate).append(" writes\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
