package com.kas.logging.impl;

public class StdoutAppender extends ConsoleAppender
{
  protected StdoutAppender(ConsoleAppenderConfiguration cac)
  {
    super(cac, System.out);
  }
}
