package com.kas.mq.admin.commands;

import com.kas.infra.typedef.TokenDeque;
import com.kas.mq.impl.MqContext;

/**
 * A Factory for creating {@link ICliCommand} objects based on the command verb (the first argument)
 * in the {@link TokenDeque}.
 * 
 * @author Pippo
 */
public class CliCommandFactory
{
  /**
   * Factory method
   * 
   * @param cmdWords The command arguments
   * @return A {@link ICliCommand} object or {@code null} if the command verb is unknown
   */
  static public ICliCommand newCommand(TokenDeque cmdWords, MqContext client)
  {
    String verb = cmdWords.poll().toUpperCase();
    
    if (HelpCommand.sCommandVerbs.contains(verb))
      return new HelpCommand(cmdWords, client);
    
    if (ExitCommand.sCommandVerbs.contains(verb))
      return new ExitCommand(cmdWords, client);
    
    if (ConnectCommand.sCommandVerbs.contains(verb))
      return new ConnectCommand(cmdWords, client);
    
    if (DisconnectCommand.sCommandVerbs.contains(verb))
      return new DisconnectCommand(cmdWords, client);
    
    if (DefineCommand.sCommandVerbs.contains(verb))
      return new DefineCommand(cmdWords, client);
    
    if (DeleteCommand.sCommandVerbs.contains(verb))
      return new DeleteCommand(cmdWords, client);
    
    if (QueryCommand.sCommandVerbs.contains(verb))
      return new QueryCommand(cmdWords, client);
    
    if (PutCommand.sCommandVerbs.contains(verb))
      return new PutCommand(cmdWords, client);
    
    if (GetCommand.sCommandVerbs.contains(verb))
      return new GetCommand(cmdWords, client);
    
    if (TerminateCommand.sCommandVerbs.contains(verb))
      return new TerminateCommand(cmdWords, client);
    
    if (ShutdownCommand.sCommandVerbs.contains(verb))
      return new ShutdownCommand(cmdWords, client);
    
    // Unknown command
    return null;
  }
}
