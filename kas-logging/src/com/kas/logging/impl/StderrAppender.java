package com.kas.logging.impl;

public class StderrAppender extends ConsoleAppender
{
  protected StderrAppender(ConsoleAppenderConfiguration cac)
  {
    super(cac, System.err);
  }
}
