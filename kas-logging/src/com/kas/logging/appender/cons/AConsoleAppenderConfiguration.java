package com.kas.logging.appender.cons;

import com.kas.logging.appender.AAppenderConfiguration;
import com.kas.logging.appender.IAppenderConfiguration;
import com.kas.logging.impl.IAppender;
import com.kas.logging.impl.LoggingConfiguration;

/**
 * The {@link AConsoleAppender} configuration object.
 * 
 * @author Pippo
 */
public abstract class AConsoleAppenderConfiguration extends AAppenderConfiguration
{
  /**
   * Construct the appender's configuration, providing the {@link LoggingConfiguration}
   * 
   * @param name The name of the appender
   * @param loggingConfig The {@link LoggingConfiguration}
   */
  public AConsoleAppenderConfiguration(String name, LoggingConfiguration loggingConfig)
  {
    super(name, loggingConfig);
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
  }
  
  /**
   * Create an appender that uses this {@link IAppenderConfiguration} object.
   * 
   * @return a new {@link IAppender} that is associated with this {@link IAppenderConfiguration}
   */
  public abstract IAppender createAppender();
  
  /**
   * Returns the {@link AConsoleAppenderConfiguration} string representation.
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
      .append(pad).append("  ").append("Enabled=").append(mEnabled).append("\n")
      .append(pad).append("  ").append("LogLevel=").append(mLogLevel.name()).append("\n")
      .append(pad).append(")");
    
    return sb.toString();
  }
}
