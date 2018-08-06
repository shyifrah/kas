package com.kas.logging;

import com.kas.infra.base.IObject;
import com.kas.infra.logging.IBaseLogger;

/**
 * The ILogger interface is used by the kas-logging package.<br>
 * <br>
 * It does not extend the {@link IBaseLogger} interface.
 * 
 * @author Pippo
 */
public interface ILogger extends IBaseLogger
{
  /**
   * Write a DIAG message with the specified log level
   * 
   * @param message The message to write
   * 
   * @see com.kas.infra.logging.ELogLevel#DIAG
   */
  public abstract void diag (String message);
  
  /**
   * Write a DEBUG message with the specified log level
   * 
   * @param message The message to write
   * 
   * @see com.kas.infra.logging.ELogLevel#DEBUG
   */
  public abstract void debug(String message);
  
  /**
   * Write a TRACE message with the specified log level
   * 
   * @param message The message to write
   * 
   * @see com.kas.infra.logging.ELogLevel#TRACE
   */
  public abstract void trace(String message);
  
  /**
   * Write a INFO message with the specified log level
   * 
   * @param message The message to write
   * 
   * @see com.kas.infra.logging.ELogLevel#INFO
   */
  public abstract void info (String message);
  
  /**
   * Write a WARN message with the specified log level
   * 
   * @param message The message to write
   * 
   * @see com.kas.infra.logging.ELogLevel#WARN
   */
  public abstract void warn (String message);
  
  /**
   * Write a ERROR message with the specified log level
   * 
   * @param message The message to write
   * 
   * @see com.kas.infra.logging.ELogLevel#ERROR
   */
  public abstract void error(String message);
  
  /**
   * Write a FATAL message with the specified log level
   * 
   * @param message The message to write
   * 
   * @see com.kas.infra.logging.ELogLevel#FATAL
   */
  public abstract void fatal(String message);
  
  /**
   * Write a DIAG message and append the {@code Throwable} object with the specified log level
   * 
   * @param message The message to write
   * @param e The throwable object to write
   * 
   * @see com.kas.infra.logging.ELogLevel#DIAG
   */
  public abstract void diag (String message, Throwable e);
  
  /**
   * Write a DEBUG message and append the {@code Throwable} object with the specified log level
   * 
   * @param message The message to write
   * @param e The throwable object to write
   * 
   * @see com.kas.infra.logging.ELogLevel#DEBUG
   */
  public abstract void debug(String message, Throwable e);
  
  /**
   * Write a TRACE message and append the {@code Throwable} object with the specified log level
   * 
   * @param message The message to write
   * @param e The throwable object to write
   * 
   * @see com.kas.infra.logging.ELogLevel#TRACE
   */
  public abstract void trace(String message, Throwable e);
  
  /**
   * Write a INFO message and append the {@code Throwable} object with the specified log level
   * 
   * @param message The message to write
   * @param e The throwable object to write
   * 
   * @see com.kas.infra.logging.ELogLevel#INFO
   */
  public abstract void info (String message, Throwable e);
  
  /**
   * Write a WARN message and append the {@code Throwable} object with the specified log level
   * 
   * @param message The message to write
   * @param e The throwable object to write
   * 
   * @see com.kas.infra.logging.ELogLevel#WARN
   */
  public abstract void warn (String message, Throwable e);
  
  /**
   * Write a ERROR message and append the {@code Throwable} object with the specified log level
   * 
   * @param message The message to write
   * @param e The throwable object to write
   * 
   * @see com.kas.infra.logging.ELogLevel#ERROR
   */
  public abstract void error(String message, Throwable e);
  
  /**
   * Write a FATAL message and append the {@code Throwable} object with the specified log level
   * 
   * @param message The message to write
   * @param e The throwable object to write
   * 
   * @see com.kas.infra.logging.ELogLevel#FATAL
   */
  public abstract void fatal(String message, Throwable e);
  
  /**
   * Returns the {@link #IBaseLogger} simple class name enclosed with chevrons.
   * 
   * @return class name enclosed with chevrons.
   */
  public abstract String name();
  
  /**
   * Returns a replica of this {@link #IBaseLogger}.
   * 
   * @return a replica of this {@link #IBaseLogger}
   */
  public abstract IObject replicate();
  
  /**
   * Returns the {@link #IBaseLogger} string representation.
   * 
   * @param level the required level padding
   * 
   * @return the object's printable string representation
   */
  public abstract String toPrintableString(int level);
}
