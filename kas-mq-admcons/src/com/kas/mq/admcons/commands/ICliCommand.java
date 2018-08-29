package com.kas.mq.admcons.commands;

import com.kas.infra.base.IObject;

/**
 * A general purpose command processed by the {@link com.kas.mq.admcons.KasMqAdmin KasMqAdmin}
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
   * @return {@code true} if {@link com.kas.mq.admcons.KasMqAdmin KasMqAdmin} should stop further processing following
   * the processing of this command, {@code false} otherwise
   */
  public abstract boolean run();
}
