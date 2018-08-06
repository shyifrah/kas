package com.kas.logging.impl;

import com.kas.infra.base.IObject;

/**
 * The {@link ConsoleAppender} configuration object.
 * 
 * @author Pippo
 */
public class ConsoleAppenderConfiguration extends AAppenderConfiguration
{
  /**
   * Construct the appender's configuration, providing the appender's name and the {@link LoggingConfiguration}
   */
  public ConsoleAppenderConfiguration(String appenderName, LoggingConfiguration loggingConfig)
  {
    super(appenderName, loggingConfig);
  }
  
  /**
   * Refreshing the appender's configuration.<br>
   * <br>
   * When this method is called, it calls the {@link com.kas.config.MainConfiguration MainConfiguration} in order to re-read the values
   * of the relevant properties.
   * 
   * @see AAppenderConfiguration#refresh()
   */
  public void refresh()
  {
    super.refresh();
  }

  /**
   * Returns a replica of this {@link ConsoleAppenderConfiguration}.
   * 
   * @return a replica of this {@link ConsoleAppenderConfiguration}
   */
  public ConsoleAppenderConfiguration replicate()
  {
    return new ConsoleAppenderConfiguration(mName, mLoggingConfig);
  }
  
  /**
   * Returns the {@link ConsoleAppenderConfiguration} string representation.
   * 
   * @param level the required level padding
   * @return the object's printable string representation
   * 
   * @see IObject#toPrintableString(int)
   */
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
