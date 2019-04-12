package com.kas.mq.admin;

/**
 * There are various command factories - one for each command that manages
 * sub-verbs (such as DEFINE, as there are several DEFINE commands: QUEUE,
 * GROUP, USER etc.) - and a global one which manages all command verbs.
 * 
 * @author Pippo
 */
public interface ICommandFactory
{
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
