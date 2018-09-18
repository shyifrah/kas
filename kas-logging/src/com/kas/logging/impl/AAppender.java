package com.kas.logging.impl;

import com.kas.infra.base.AKasObject;
import com.kas.infra.logging.ELogLevel;
import com.kas.infra.utils.StringUtils;

/**
 * A general purpose {@link AAppender}
 * 
 * @author Pippo
 */
public abstract class AAppender extends AKasObject implements IAppender
{
  static protected final String cAppenderMessageFormat = "%s %5d:%-2d %-5s (%s) %s%n";
  
  /**
   * Write a message
   * 
   * @param logger The name of the logger
   * @param level the {@link ELogLevel} of the message
   * @param message The message text to write
   * @param ex A {@link Throwable} object to write to the logger
   * 
   * @see com.kas.logging.impl.IAppender#write(String, ELogLevel, String, Throwable)
   */
  public synchronized void write(String logger, ELogLevel messageLevel, String message, Throwable ex)
  {
    String msgText = message;
    if (ex != null)
    {
      StringBuilder sb = new StringBuilder()
          .append(message)
          .append(StringUtils.format(ex));
      msgText = sb.toString();
    }
    
    write(logger, messageLevel, msgText);
  }
  
  /**
   * Initialize the appender
   * 
   * @return {@code true} if appender was successfully initialized, {@code false} otherwise
   * 
   * @see com.kas.infra.base.IInitializable#init()
   */
  public abstract boolean init();
  
  /**
   * Terminate the appender
   * 
   * @return {@code true} if appender was successfully terminated, {@code false} otherwise
   * 
   * @see com.kas.infra.base.IInitializable#term();
   */
  public abstract boolean term();
  
  /**
   * Internal implementation of the message writing procedure
   * 
   * @param logger The name of the logger
   * @param level the {@link ELogLevel} of the message
   * @param message The message text to write
   */
  protected abstract void write(String logger, ELogLevel level, String message);
}
