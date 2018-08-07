package com.kas.logging.impl;

import com.kas.infra.base.AKasObject;
import com.kas.infra.base.IInitializable;
import com.kas.infra.base.ThrowableFormatter;
import com.kas.infra.logging.ELogLevel;

/**
 * A general purpose {@link AAppender}
 * 
 * @author Pippo
 */
public abstract class AAppender extends AKasObject implements IAppender, IInitializable
{
  static protected final String cAppenderMessageFormat = "%s %d:%d %-5s [%s] %s%n";
  
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
      StringBuffer sb = new StringBuffer().append(message);
      ThrowableFormatter formatter = new ThrowableFormatter(ex);
      sb.append(formatter.toString());
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
  
  /**
   * Returns a replica of this {@link AAppender}.
   * 
   * @return a replica of this {@link AAppender}
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public abstract AAppender replicate();
  
  /**
   * Returns the {@link AAppender} string representation.
   * 
   * @param level the required level padding
   * @return the object's printable string representation
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public abstract String toPrintableString(int level);
}
