package com.kas.logging.impl;

public class StderrAppenderConfiguration extends ConsoleAppenderConfiguration
{
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public StderrAppenderConfiguration(LoggingConfiguration loggingConfig)
  {
    super(AppenderManager.cStderrAppenderName, loggingConfig);
  }
}
