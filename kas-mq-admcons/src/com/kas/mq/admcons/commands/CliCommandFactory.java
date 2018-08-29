package com.kas.mq.admcons.commands;

import java.util.Scanner;
import com.kas.mq.client.IClient;
import com.kas.mq.internal.TokenDeque;

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
  static public ICliCommand newCommand(Scanner scanner, TokenDeque cmdWords, IClient client)
  {
    String verb = cmdWords.poll().toUpperCase();
    
    if (HelpCommand.sCommandVerbs.contains(verb))
      return new HelpCommand(scanner, cmdWords, client);
    
    if (ExitCommand.sCommandVerbs.contains(verb))
      return new ExitCommand(scanner, cmdWords, client);
    
    if (ConnectCommand.sCommandVerbs.contains(verb))
      return new ConnectCommand(scanner, cmdWords, client);
    
    if (DisconnectCommand.sCommandVerbs.contains(verb))
      return new DisconnectCommand(scanner, cmdWords, client);
    
    if (DefineCommand.sCommandVerbs.contains(verb))
      return new DefineCommand(scanner, cmdWords, client);
    
    if (DeleteCommand.sCommandVerbs.contains(verb))
      return new DeleteCommand(scanner, cmdWords, client);
    
    if (PutCommand.sCommandVerbs.contains(verb))
      return new PutCommand(scanner, cmdWords, client);
    
    if (GetCommand.sCommandVerbs.contains(verb))
      return new GetCommand(scanner, cmdWords, client);
    
    if (ShutdownCommand.sCommandVerbs.contains(verb))
      return new ShutdownCommand(scanner, cmdWords, client);
    
    // Unknown command
    return null;
  }
}
