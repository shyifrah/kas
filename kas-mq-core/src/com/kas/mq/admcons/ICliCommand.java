package com.kas.mq.admcons;

import com.kas.infra.base.IObject;

/**
 * A general purpose command processed by the {@link AdminConsoleProcessor}
 * 
 * @author Pippo
 */
public interface ICliCommand extends IObject
{
  /**
   * Display help for the specific command.
   */
  public abstract void help();
  
  /**
   * Running the command.
   * 
   * @return {@code true} if {@link AdminConsoleProcessor} should stop further processing following
   * the processing of this command, {@code false} otherwise
   */
  public abstract boolean run();
}
