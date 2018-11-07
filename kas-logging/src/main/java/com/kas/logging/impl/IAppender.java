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
public interface IAppender extends IInitializable, IObject
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
}
