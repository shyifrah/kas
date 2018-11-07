package com.kas.logging.impl;

import com.kas.infra.base.AKasObject;
import com.kas.infra.logging.ELogLevel;
import com.kas.logging.ILogger;

/**
 * A {@link Logger} is the object that the user creates and holds in order to write messages
 * to the console or to a log file.<br>
 * <br>
 * It is created using one of {@link LoggerFactory} factory methods.
 * 
 * @author Pippo
 */
public class Logger extends AKasObject implements ILogger
{
  /**
   * Logger name
   */
  private String mName;
  
  /**
   * Associated appender
   * 
   * @see com.kas.logging.impl.IAppender
   */
  private IAppender mAppender;
  
  /**
   * Construct a {@link Logger} object with the specified name and associated appender
   * 
   * @param name The logger's name
   * @param appender The appender which will be used to write messages
   */
  public Logger(String name, IAppender appender)
  {
    mName     = name;
    mAppender = appender;
  }
  
  /**
   * Write a message. All public message writing methods are actually calling this one
   * 
   * @param level Message's log level
   * @param message The message's text
   * @param ex A throwable object which needs to be formatted and written as well
   */
  private void write(ELogLevel level, String message, Throwable ex)
  {
    if (mAppender != null) mAppender.write(mName, level, message, ex);
  }

  /**
   * Write a DIAG message
   * 
   * @param message The message's text
   * 
   * @see com.kas.infra.logging.ELogLevel#DIAG
   */
  public void diag(String message)
  {
    write(ELogLevel.DIAG, message, null);
  }
  
  /**
   * Write a DIAG message
   * 
   * @param message The message's text
   * @param ex A throwable object which needs to be formatted and written as well
   * 
   * @see com.kas.infra.logging.ELogLevel#DIAG
   */
  public void diag(String message, Throwable ex)
  {
    write(ELogLevel.DIAG, message, ex);
  }

  /**
   * Write a DEBUG message
   * 
   * @param message The message's text
   * 
   * @see com.kas.infra.logging.ELogLevel#DEBUG
   */
  public void debug(String message)
  {
    write(ELogLevel.DEBUG, message, null);
  }
  
  /**
   * Write a DEBUG message
   * 
   * @param message The message's text
   * @param ex A throwable object which needs to be formatted and written as well
   * 
   * @see com.kas.infra.logging.ELogLevel#DEBUG
   */
  public void debug(String message, Throwable ex)
  {
    write(ELogLevel.DEBUG, message, ex);
  }

  /**
   * Write a TRACE message
   * 
   * @param message The message's text
   * 
   * @see com.kas.infra.logging.ELogLevel#TRACE
   */
  public void trace(String message)
  {
    write(ELogLevel.TRACE, message, null);
  }
  
  /**
   * Write a TRACE message
   * 
   * @param message The message's text
   * @param ex A throwable object which needs to be formatted and written as well
   * 
   * @see com.kas.infra.logging.ELogLevel#TRACE
   */
  public void trace(String message, Throwable ex)
  {
    write(ELogLevel.TRACE, message, ex);
  }

  /**
   * Write a INFO message
   * 
   * @param message The message's text
   * 
   * @see com.kas.infra.logging.ELogLevel#INFO
   */
  public void info(String message)
  {
    write(ELogLevel.INFO, message, null);
  }
  
  /**
   * Write a INFO message
   * 
   * @param message The message's text
   * @param ex A throwable object which needs to be formatted and written as well
   * 
   * @see com.kas.infra.logging.ELogLevel#INFO
   */
  public void info(String message, Throwable ex)
  {
    write(ELogLevel.INFO, message, ex);
  }

  /**
   * Write a WARN message
   * 
   * @param message The message's text
   * 
   * @see com.kas.infra.logging.ELogLevel#WARN
   */
  public void warn(String message)
  {
    write(ELogLevel.WARN, message, null);
  }
  
  /**
   * Write a WARN message
   * 
   * @param message The message's text
   * @param ex A throwable object which needs to be formatted and written as well
   * 
   * @see com.kas.infra.logging.ELogLevel#WARN
   */
  public void warn(String message, Throwable ex)
  {
    write(ELogLevel.WARN, message, ex);
  }

  /**
   * Write a ERROR message
   * 
   * @param message The message's text
   * 
   * @see com.kas.infra.logging.ELogLevel#ERROR
   */
  public void error(String message)
  {
    write(ELogLevel.ERROR, message, null);
  }
  
  /**
   * Write a ERROR message
   * 
   * @param message The message's text
   * @param ex A throwable object which needs to be formatted and written as well
   * 
   * @see com.kas.infra.logging.ELogLevel#ERROR
   */
  public void error(String message, Throwable ex)
  {
    write(ELogLevel.ERROR, message, ex);
  }

  /**
   * Write a FATAL message
   * 
   * @param message The message's text
   * 
   * @see com.kas.infra.logging.ELogLevel#FATAL
   */
  public void fatal(String message)
  {
    write(ELogLevel.FATAL, message, null);
  }
  
  /**
   * Write a FATAL message
   * 
   * @param message The message's text
   * @param ex A throwable object which needs to be formatted and written as well
   * 
   * @see com.kas.infra.logging.ELogLevel#FATAL
   */
  public void fatal(String message, Throwable ex)
  {
    write(ELogLevel.FATAL, message, ex);
  }
  
  /**
   * Returns the {@link Logger} string representation.
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
      .append(pad).append("  Name=").append(mName).append("\n")
      .append(pad).append("  Appender=").append(mAppender.name()).append("\n")
      .append(pad).append(")\n");
    
    return sb.toString();
  }
}
