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
    
    // Is it a HELP command
    if (verb.equals(HelpCommand.cCommandVerb))
    {
      return new HelpCommand(cmdWords, client);
    }
    
    // Is it a EXIT command
    if (verb.equals(ExitCommand.cCommandVerb))
    {
      return new ExitCommand(cmdWords, client);
    }
    
    // Is it a CONNECT command
    if (verb.equals(ConnectCommand.cCommandVerb))
    {
      return new ConnectCommand(cmdWords, client);
    }
    
    // Is it a DISCONNECT command
    if (verb.equals(DisconnectCommand.cCommandVerb))
    {
      return new DisconnectCommand(cmdWords, client);
    }
    
    // Unknown command
    return null;
    
//    if (cVerbDefine.equalsIgnoreCase(verb))
//    {
//      DefineCommand command = new DefineCommand(mConnection, cmdWords);
//      command.run();
//    }
//    else
//    if (cVerbDelete.equalsIgnoreCase(verb))
//    {
//      DeleteCommand command = new DeleteCommand(mConnection, cmdWords);
//      command.run();
//    }
//    else
//    if (cVerbQuery.equalsIgnoreCase(verb))
//    {
//      try
//      {
//        QueryCommand command = new QueryCommand(mConnection, cmdWords);
//        command.run();
//      }
//      catch (IllegalArgumentException e)
//      {
//        writeln("Error occurred while running QUERY command: " + e.getMessage());
//      }
//    }
//    else
//    if (cVerbHelp.equalsIgnoreCase(verb))
//    {
//      HelpCommand command = new HelpCommand(mConnection, cmdWords);
//      command.run();
//    }
//    else
//    {
//      writeln("Unknown command verb: [" + verb + "]");
//      writeln(" ");
//    }
  }
}
