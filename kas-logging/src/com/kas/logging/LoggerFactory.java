package com.kas.logging;

import com.kas.logging.impl.AppenderManager;
import com.kas.logging.impl.IAppender;
import com.kas.logging.impl.Logger;

/**
 * A factory for creating loggers
 * 
 * @author Pippo
 */
public class LoggerFactory
{
  /**
   * Get a logger based on the the requesting class
   * 
   * @param requestorClass {@code Class} of the requestor
   * @return returned logger object
   */
  static public ILogger getLogger(Class<?> requestorClass)
  {
    IAppender appender = AppenderManager.getInstance().getAppender(requestorClass);
    return new Logger(requestorClass.getCanonicalName(), appender);
  }
  
  /**
   * Get STDOUT logger
   * 
   * @param requestorClass {@code Class} of the requestor
   * @return returned logger object
   */
  static public ILogger getStdout(Class<?> requestorClass)
  {
    IAppender appender = AppenderManager.getInstance().getAppender(AppenderManager.cStdoutAppenderName);
    return new Logger(requestorClass.getCanonicalName(), appender);
  }
  
  /**
   * Get STDERR logger
   * 
   * @param requestorClass {@code Class} of the requestor
   * @return returned logger object
   */
  static public ILogger getStderr(Class<?> requestorClass)
  {
    IAppender appender = AppenderManager.getInstance().getAppender(AppenderManager.cStderrAppenderName);
    return new Logger(requestorClass.getCanonicalName(), appender);
  }
}
