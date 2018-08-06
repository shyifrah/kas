package com.kas.logging.impl;

import com.kas.infra.base.IInitializable;
import com.kas.infra.base.IObject;
import com.kas.infra.logging.ELogLevel;

/**
 * An appender is a sort of a media manager which is responsible actually writing the messages
 * to the appropriate media.
 * 
 * @author Pippo
 */
public interface IAppender extends IInitializable
{
  /**
   * Write a message
   * 
   * @param logger The name of the logger
   * @param message The message text to write
   * @param ex A {@link Throwable} object to write to the logger
   * @param level the {@link ELogLevel} of the message
   */
  public abstract void write(String logger, ELogLevel level, String message, Throwable ex);
  
  /**
   * Initialize the object
   * 
   * @return {@code true} if object was initialized successfully, {@code false} otherwise
   */
  public abstract boolean init();
  
  /**
   * Terminate the object
   * 
   * @return {@code true} if object was terminated successfully, {@code false} otherwise
   */
  public abstract boolean term();
  
  /**
   * Returns the {@link #IAppender} simple class name enclosed with chevrons.
   * 
   * @return class name enclosed with chevrons.
   */
  public abstract String name();
  
  /**
   * Returns a replica of this {@link #IAppender}.
   * 
   * @return a replica of this {@link #IAppender}
   */
  public abstract IAppender replicate();
  
  /**
   * Returns the {@link #IAppender} string representation.
   * 
   * @param level the required level padding
   * @return the object's printable string representation
   * 
   * @see IObject#toPrintableString(int)
   */
  public abstract String toPrintableString(int level);
}
