package com.kas.logging.appender.cons;

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
