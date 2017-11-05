package com.kas.logging.impl;

public interface Constants
{
  static final String cAppenderMessageFormat = "%s %d:%d %-5s [%s] %s%n";
  
  static final String cStdoutAppenderName = "stdout";
  static final String cStderrAppenderName = "stderr";
  static final String cFileAppenderName   = "file";
  
  static final int      cBytesPerMB = 1024 * 1024;
  
  static final String   cLoggingConfigPrefix    = "kas.logging.";
  static final String   cConfigAppenderPrefix = cLoggingConfigPrefix + "appender."; 
  
  static final boolean  cDefaultEnabled  = true;
  static final LogLevel cDefaultLogLevel = LogLevel.INFO;
  
  static final String cDefaultLogFileName           = "kas-%u-%d.log";
  static final int    cDefaultMaxWriteErrors        = 10;
  static final int    cDefaultFlushRate             = 10;
  static final int    cDefaultArchiveMaxFileSizeMb  = 10;
  static final int    cDefaultArchiveMaxGenerations = 5;
  static final int    cDefaultArchiveTestSizeRate   = 20;
}
