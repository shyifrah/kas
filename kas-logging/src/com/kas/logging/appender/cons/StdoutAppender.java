package com.kas.logging.appender.cons;

import com.kas.logging.impl.AConsoleAppenderConfiguration;

/**
 * STDOUT appender
 * 
 * @author Pippo
 */
public class StdoutAppender extends AConsoleAppender
{
  /**
   * Construct STDOUT appender specifying the {@link AConsoleAppenderConfiguration} object
   * 
   * @param cac The {@link AConsoleAppenderConfiguration}
   */
  public StdoutAppender(AConsoleAppenderConfiguration cac)
  {
    super(cac, System.out);
  }
}
