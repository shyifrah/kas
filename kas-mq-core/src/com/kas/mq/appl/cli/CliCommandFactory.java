package com.kas.mq.appl.cli;

import com.kas.mq.client.IClient;
import com.kas.mq.typedef.TokenDeque;

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
  static public ICliCommand newCommand(TokenDeque cmdWords, IClient client)
  {
    String verb = cmdWords.poll();
    
    if (HelpCommand.sCommandVerbs.contains(verb))
    {
      return new HelpCommand(cmdWords, client);
    }
    
    if (ExitCommand.sCommandVerbs.contains(verb))
    {
      return new ExitCommand(cmdWords, client);
    }
    
    if (ConnectCommand.sCommandVerbs.contains(verb))
    {
      return new ConnectCommand(cmdWords, client);
    }
    
    if (DisconnectCommand.sCommandVerbs.contains(verb))
    {
      return new ConnectCommand(cmdWords, client);
    }
    
    // Unknown command
    return null;
  }
}
