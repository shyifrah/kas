package com.kas.logging.impl;

public class ConsoleAppenderConfiguration extends AbstractAppenderConfiguration
{
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public ConsoleAppenderConfiguration(LoggingConfiguration loggingConfig)
  {
    super(Constants.cConsoleAppenderName, loggingConfig);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void refresh()
  {
    super.refresh();
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    
    sb.append(name()).append("(\n")
      .append(pad).append("  ").append("Enabled=").append(mEnabled).append("\n")
      .append(pad).append("  ").append("LogLevel=").append(mLogLevel.name()).append("\n")
      .append(pad).append(")");
    
    return sb.toString();
  }
}
