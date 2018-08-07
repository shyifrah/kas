package com.kas.infra.test;

import com.kas.infra.base.TimeStamp;
import com.kas.infra.logging.ELogLevel;
import com.kas.infra.logging.IBaseLogger;

/**
 * A basic logger used by the testing package.
 * 
 * @author Pippo
 */
class ConsoleLogger implements IBaseLogger
{
  /**
   * Name of the logger
   */
  private String mLoggerName;
  
  /**
   * Construct a logger with the specified name
   * 
   * @param name The name of the logger
   */
  public ConsoleLogger(String name)
  {
    mLoggerName = name;
  }
  
  /**
   * Get the logger's name
   * 
   * @return the logger's name
   */
  public String getLoggerName()
  {
    return mLoggerName;
  }
  
  /**
   * Writing {@code message} with the specified {@code level} to {@code stdout}.
   * 
   * @param level The message's log level
   * @param message The message's text
   */
  private void stdout(ELogLevel level, String message)
  {
    TimeStamp ts = new TimeStamp();
    String msg = String.format("%s (%s) %-5s %s", ts.toString(), mLoggerName, level.toString(), message);
    System.out.println(msg);
  }

  /**
   * Write a DIAG message with the specified log level
   * 
   * @param message The message to write
   * 
   * @see com.kas.infra.logging.ELogLevel#DIAG
   */
  public void diag(String message)
  {
    stdout(ELogLevel.DIAG, message);
  }

  /**
   * Write a DEBUG message with the specified log level
   * 
   * @param message The message to write
   * 
   * @see com.kas.infra.logging.ELogLevel#DEBUG
   */
  public void debug(String message)
  {
    stdout(ELogLevel.DEBUG, message);
  }
  
  /**
   * Write a TRACE message with the specified log level
   * 
   * @param message The message to write
   * 
   * @see com.kas.infra.logging.ELogLevel#TRACE
   */
  public void trace(String message)
  {
    stdout(ELogLevel.TRACE, message);
  }
  
  /**
   * Write a INFO message with the specified log level
   * 
   * @param message The message to write
   * 
   * @see com.kas.infra.logging.ELogLevel#INFO
   */
  public void info(String message)
  {
    stdout(ELogLevel.INFO, message);
  }
  
  /**
   * Write a WARN message with the specified log level
   * 
   * @param message The message to write
   * 
   * @see com.kas.infra.logging.ELogLevel#WARN
   */
  public void warn(String message)
  {
    stdout(ELogLevel.WARN, message);
  }
  
  /**
   * Write a ERROR message with the specified log level
   * 
   * @param message The message to write
   * 
   * @see com.kas.infra.logging.ELogLevel#ERROR
   */
  public void error(String message)
  {
    stdout(ELogLevel.ERROR, message);
  }
  
  /**
   * Write a FATAL message with the specified log level
   * 
   * @param message The message to write
   * 
   * @see com.kas.infra.logging.ELogLevel#FATAL
   */
  public void fatal(String message)
  {
    stdout(ELogLevel.FATAL, message);
  }
  
  /**
   * Write a DIAG message and append the {@code Throwable} object with the specified log level
   * 
   * @param message The message to write
   * @param e The throwable object to write
   * 
   * @see com.kas.infra.logging.ELogLevel#DIAG
   */
  public void diag(String message, Throwable e)
  {
    stdout(ELogLevel.DIAG, message);
    e.printStackTrace();
  }
  
  /**
   * Write a DEBUG message and append the {@code Throwable} object with the specified log level
   * 
   * @param message The message to write
   * @param e The throwable object to write
   * 
   * @see com.kas.infra.logging.ELogLevel#DEBUG
   */
  public void debug(String message, Throwable e)
  {
    stdout(ELogLevel.DEBUG, message);
    e.printStackTrace();
  }
  
  /**
   * Write a TRACE message and append the {@code Throwable} object with the specified log level
   * 
   * @param message The message to write
   * @param e The throwable object to write
   * 
   * @see com.kas.infra.logging.ELogLevel#TRACE
   */
  public void trace(String message, Throwable e)
  {
    stdout(ELogLevel.TRACE, message);
    e.printStackTrace();
  }
  
  /**
   * Write a INFO message and append the {@code Throwable} object with the specified log level
   * 
   * @param message The message to write
   * @param e The throwable object to write
   * 
   * @see com.kas.infra.logging.ELogLevel#INFO
   */
  public void info(String message, Throwable e)
  {
    stdout(ELogLevel.INFO, message);
    e.printStackTrace();
  }
  
  /**
   * Write a WARN message and append the {@code Throwable} object with the specified log level
   * 
   * @param message The message to write
   * @param e The throwable object to write
   * 
   * @see com.kas.infra.logging.ELogLevel#WARN
   */
  public void warn(String message, Throwable e)
  {
    stdout(ELogLevel.WARN, message);
    e.printStackTrace();
  }
  
  /**
   * Write a ERROR message and append the {@code Throwable} object with the specified log level
   * 
   * @param message The message to write
   * @param e The throwable object to write
   * 
   * @see com.kas.infra.logging.ELogLevel#ERROR
   */
  public void error(String message, Throwable e)
  {
    stdout(ELogLevel.ERROR, message);
    e.printStackTrace();
  }
  
  /**
   * Write a FATAL message and append the {@code Throwable} object with the specified log level
   * 
   * @param message The message to write
   * @param e The throwable object to write
   * 
   * @see com.kas.infra.logging.ELogLevel#FATAL
   */
  public void fatal(String message, Throwable e)
  {
    stdout(ELogLevel.FATAL, message);
    e.printStackTrace();
  }
  
  /**
   * Get the object's string representation.
   * 
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(")
      .append("LoggerName=").append(mLoggerName)
      .append(")");
    return sb.toString();
  }

  /**
   * Returns the {@link ConsoleLogger} simple class name enclosed with chevrons.
   * 
   * @return class name enclosed with chevrons.
   * 
   * @see com.kas.infra.base.IObject#name()
   */
  public String name()
  {
    return this.getClass().getSimpleName();
  }
  
  /**
   * Returns a replica of this {@link ConsoleLogger}.
   * 
   * @return a replica of this {@link ConsoleLogger}
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public ConsoleLogger replicate()
  {
    return new ConsoleLogger(mLoggerName);
  }
  
  /**
   * Get the object's detailed string representation. For {@link #toPrintableString(int)} the result
   * is the same as the one returned by {@link #toString()}.
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public String toPrintableString(int level)
  {
    return toString();
  }
}
