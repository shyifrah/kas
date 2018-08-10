package com.kas.mq.appl.cli;

import com.kas.mq.typedef.TokenQueue;

public class CliCommandFactory
{
  static public ICliCommand newCommand(TokenQueue cmdWords)
  {
    String verb = cmdWords.poll();
    
    if (verb.equalsIgnoreCase(ExitCommand.cCommandVerb))
    {
      return new ExitCommand(cmdWords);
    }
    
    return null;
    
//    if (cVerbExit.equalsIgnoreCase(verb))
//    {
//      stop = true;
//    }
//    else
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
