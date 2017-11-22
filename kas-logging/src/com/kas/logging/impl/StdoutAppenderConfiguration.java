package com.kas.logging.impl;

public class StdoutAppenderConfiguration extends ConsoleAppenderConfiguration
{
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public StdoutAppenderConfiguration(LoggingConfiguration loggingConfig)
  {
    super(AppenderManager.cStdoutAppenderName, loggingConfig);
  }
}
