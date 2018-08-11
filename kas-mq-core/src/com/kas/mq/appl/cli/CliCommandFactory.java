package com.kas.mq.appl.cli;

import com.kas.mq.typedef.TokenQueue;

/**
 * A Factory for creating {@link ICliCommand} objects based on the command verb (the first element)
 * in the {@link TokenQueue}.
 * 
 * @author Pippo
 */
public class CliCommandFactory
{
  static public ICliCommand newCommand(TokenQueue cmdWords)
  {
    String verb = cmdWords.poll().toUpperCase();
    
    // Is it a HELP command
    if (verb.equals(HelpCommand.cCommandVerb))
    {
      return new HelpCommand(cmdWords);
    }
    
    // Is it a EXIT command
    if (verb.equals(ExitCommand.cCommandVerb))
    {
      return new ExitCommand(cmdWords);
    }
    
    // Unknown command
    writeln("Unknown command verb: \"" + verb.toUpperCase() + "\"");
    writeln(" ");
    
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
  
  /**
   * Writing a message to STDOUT. The message will be followed by a Newline character.
   * 
   * @param message the message to print
   */
  static private void writeln(String message)
  {
    System.out.println(message);
  }
}
