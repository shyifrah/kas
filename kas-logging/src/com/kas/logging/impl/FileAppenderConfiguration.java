package com.kas.logging.impl;

public class FileAppenderConfiguration extends AAppenderConfiguration
{
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private String mFileNamePattern       = Constants.cDefaultLogFileName;
  private int    mMaxWriteErrors        = Constants.cDefaultMaxWriteErrors;
  private int    mFlushRate             = Constants.cDefaultFlushRate;
  private int    mArchiveMaxFileSizeMb  = Constants.cDefaultArchiveMaxFileSizeMb;
  private int    mArchiveMaxGenerations = Constants.cDefaultArchiveMaxGenerations;
  private int    mArchiveTestSizeRate   = Constants.cDefaultArchiveTestSizeRate;
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public FileAppenderConfiguration(LoggingConfiguration loggingConfig)
  {
    super(Constants.cFileAppenderName, loggingConfig);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void refresh()
  {
    super.refresh();
    
    mFileNamePattern       = sMainConfig.getStringProperty ( Constants.cLoggingConfigPrefix + "appender." + mName + ".fileNamePattern"        , mFileNamePattern       );
    mMaxWriteErrors        = sMainConfig.getIntProperty    ( Constants.cLoggingConfigPrefix + "appender." + mName + ".maxWriteErrors"         , mMaxWriteErrors        );
    mFlushRate             = sMainConfig.getIntProperty    ( Constants.cLoggingConfigPrefix + "appender." + mName + ".flushRate"              , mFlushRate             );
    mArchiveMaxFileSizeMb  = sMainConfig.getIntProperty    ( Constants.cLoggingConfigPrefix + "appender." + mName + ".archive.maxFileSizeMb"  , mArchiveMaxFileSizeMb  );
    mArchiveMaxGenerations = sMainConfig.getIntProperty    ( Constants.cLoggingConfigPrefix + "appender." + mName + ".archive.maxGenerations" , mArchiveMaxGenerations );
    mArchiveTestSizeRate   = sMainConfig.getIntProperty    ( Constants.cLoggingConfigPrefix + "appender." + mName + ".archive.testSizeRate"   , mArchiveTestSizeRate   );
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public int getMaxWriteErrors()
  {
    return mMaxWriteErrors;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public int getFlushRate()
  {
    return mFlushRate;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public int getArchiveMaxFileSizeMb()
  {
    return mArchiveMaxFileSizeMb;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public int getArchiveMaxGenerations()
  {
    return mArchiveMaxGenerations;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public int getArchiveTestSizeRate()
  {
    return mArchiveTestSizeRate;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String getFileNamePattern()
  {
    return mFileNamePattern;
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    
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
