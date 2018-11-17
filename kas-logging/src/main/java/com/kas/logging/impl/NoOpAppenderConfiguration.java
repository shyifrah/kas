package com.kas.logging.impl;

import com.kas.logging.appender.AAppenderConfiguration;
import com.kas.logging.appender.IAppenderConfiguration;
import com.kas.logging.appender.noop.NoOpAppender;

/**
 * A No-Operation appender configuration
 * 
 * @author Pippo
 */
public class NoOpAppenderConfiguration extends AAppenderConfiguration
{
  /**
   * Construct the appender's configuration, providing the {@link LoggingConfiguration}
   * 
   * @param name The name of the appender
   * @param loggingConfig The {@link LoggingConfiguration}
   */
  NoOpAppenderConfiguration(String name, LoggingConfiguration loggingConfig)
  {
    super(name, loggingConfig);
  }
  
  /**
   * Create an appender that uses this {@link IAppenderConfiguration} object
   * 
   * @return a new {@link IAppender} that is associated with this {@link IAppenderConfiguration}
   */
  public IAppender createAppender()
  {
    return new NoOpAppender(this);
  }

  /**
   * Returns the object's detailed string representation.
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
      .append(pad).append(")");
    return sb.toString();
  }
}
