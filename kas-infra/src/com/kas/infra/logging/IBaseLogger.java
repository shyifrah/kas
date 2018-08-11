package com.kas.infra.logging;

import com.kas.infra.base.IObject;

/**
 * Base logger interface. All loggers should adhere to this interface.
 * 
 * @author Pippo
 */
public interface IBaseLogger extends IObject
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
   * Write a DIAG message and append the {@link Throwable} object with the specified log level
   * 
   * @param message The message to write
   * @param e The throwable object to write
   * 
   * @see com.kas.infra.logging.ELogLevel#DIAG
   */
  public abstract void diag (String message, Throwable e);
  
  /**
   * Write a DEBUG message and append the {@link Throwable} object with the specified log level
   * 
   * @param message The message to write
   * @param e The throwable object to write
   * 
   * @see com.kas.infra.logging.ELogLevel#DEBUG
   */
  public abstract void debug(String message, Throwable e);
  
  /**
   * Write a TRACE message and append the {@link Throwable} object with the specified log level
   * 
   * @param message The message to write
   * @param e The throwable object to write
   * 
   * @see com.kas.infra.logging.ELogLevel#TRACE
   */
  public abstract void trace(String message, Throwable e);
  
  /**
   * Write a INFO message and append the {@link Throwable} object with the specified log level
   * 
   * @param message The message to write
   * @param e The throwable object to write
   * 
   * @see com.kas.infra.logging.ELogLevel#INFO
   */
  public abstract void info (String message, Throwable e);
  
  /**
   * Write a WARN message and append the {@link Throwable} object with the specified log level
   * 
   * @param message The message to write
   * @param e The throwable object to write
   * 
   * @see com.kas.infra.logging.ELogLevel#WARN
   */
  public abstract void warn (String message, Throwable e);
  
  /**
   * Write a ERROR message and append the {@link Throwable} object with the specified log level
   * 
   * @param message The message to write
   * @param e The throwable object to write
   * 
   * @see com.kas.infra.logging.ELogLevel#ERROR
   */
  public abstract void error(String message, Throwable e);
  
  /**
   * Write a FATAL message and append the {@link Throwable} object with the specified log level
   * 
   * @param message The message to write
   * @param e The throwable object to write
   * 
   * @see com.kas.infra.logging.ELogLevel#FATAL
   */
  public abstract void fatal(String message, Throwable e);
}
