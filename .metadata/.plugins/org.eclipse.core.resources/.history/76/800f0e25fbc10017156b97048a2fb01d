package com.kas.logging;

import com.kas.logging.impl.AppenderManager;
import com.kas.logging.impl.IAppender;
import com.kas.logging.impl.Logger;

public class LoggerFactory
{
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public static ILogger getLogger(Class<?> requestorClass)
  {
    IAppender appender = AppenderManager.getInstance().getAppender(requestorClass);
    return new Logger(requestorClass.getCanonicalName(), appender);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public static ILogger getConsole(Class<?> requestorClass)
  {
    IAppender appender = AppenderManager.getInstance().getAppender("console");
    return new Logger(requestorClass.getCanonicalName(), appender);
  }
}
