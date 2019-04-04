package com.kas.mq.admin;

/**
 * There are various command factories - a global one, one for define command,
 * one for alter command, one for delete command etc.
 * 
 * @author Pippo
 */
public interface ICommandFactory
{
  /**
   * Initialize the command factory
   */
  public abstract void init();
  
  /**
   * Factory method
   * 
   * @param cmdText
   *   The command's text
   * @return
   *   The {@link ICommand} that will handle the command
   */
  public abstract ICommand newCommand(String cmdText);
}
