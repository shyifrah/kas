package com.kas.mq.console;

/**
 * There are various command factories - a global one, one for define command,
 * one for alter command, one for delete command etc.
 * 
 * @author Pippo
 */
public interface ICommandFactory
{
  /**
   * Map {@code verb} to a specific {@link ICommand}
   * 
   * @param verb
   *   The command verb
   * @param cmd
   *   The command object that will handle the specified verb
   */
  public abstract void register(String verb, ICommand cmd);
  
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
