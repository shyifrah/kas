package com.kas.logging.impl;

import com.kas.infra.base.AKasObject;
import com.kas.infra.base.IInitializable;
import com.kas.infra.base.IObject;
import com.kas.infra.base.ThrowableFormatter;
import com.kas.infra.logging.ELogLevel;

/**
 * A general purpose {@link AAppender}
 * 
 * @author Pippo
 */
public abstract class AAppender extends AKasObject implements IAppender
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
   * @see IAppender#write(String, ELogLevel, String, Throwable)
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
   * Initialize the appender.<br>
   * <br>
   * Note this is a placeholder.
   * 
   * @return always {@code true}
   * 
   * @see IInitializable#init()
   */
  public boolean init()
  {
    return true;
  }
  
  /**
   * Terminate the appender.<br>
   * <br>
   * Note this is a placeholder.
   * 
   * @return always {@code true}
   * 
   * @see IInitializable#term()
   */
  public boolean term()
  {
    return true;
  }
  
  /**
   * Internal implementation of the message writing procedure
   * 
   * @param logger The name of the logger
   * @param level the {@link ELogLevel} of the message
   * @param message The message text to write
   */
  protected abstract void write(String logger, ELogLevel level, String message);
  
  /**
   * Returns a replica of this {@link #AAppender}.
   * 
   * @return a replica of this {@link #AAppender}
   */
  public abstract AAppender replicate();
  
  /**
   * Returns the {@link #AAppender} string representation.
   * 
   * @param level the required level padding
   * @return the object's printable string representation
   * 
   * @see IObject#toPrintableString(int)
   */
  public abstract String toPrintableString(int level);
}
