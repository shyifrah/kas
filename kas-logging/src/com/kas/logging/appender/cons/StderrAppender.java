package com.kas.logging.appender.cons;

import com.kas.logging.impl.AConsoleAppenderConfiguration;

/**
 * STDERR appender
 * 
 * @author Pippo
 */
public class StderrAppender extends AConsoleAppender
{
  /**
   * Construct STDERR appender specifying the {@link AConsoleAppenderConfiguration} object
   * 
   * @param cac The {@link AConsoleAppenderConfiguration}
   */
  public StderrAppender(AConsoleAppenderConfiguration cac)
  {
    super(cac, System.err);
  }
}
