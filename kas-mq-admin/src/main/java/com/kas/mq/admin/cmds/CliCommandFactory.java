package com.kas.mq.admin.cmds;

import com.kas.infra.typedef.TokenDeque;
import com.kas.mq.admin.cmds.alter.AltCommand;
import com.kas.mq.admin.cmds.def.DefCommand;
import com.kas.mq.admin.cmds.del.DelCommand;
import com.kas.mq.admin.cmds.msgs.GetCommand;
import com.kas.mq.admin.cmds.msgs.PutCommand;
import com.kas.mq.admin.cmds.other.ConnectCommand;
import com.kas.mq.admin.cmds.other.DisconnectCommand;
import com.kas.mq.admin.cmds.other.ExitCommand;
import com.kas.mq.admin.cmds.other.HelpCommand;
import com.kas.mq.admin.cmds.query.QueryCommand;
import com.kas.mq.admin.cmds.term.TermCommand;
import com.kas.mq.internal.MqContextConnection;

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
   * @param client The {@link MqContextConnection} object to be used
   * @return A {@link ICliCommand} object or {@code null} if the command verb is unknown
   */
  static public ICliCommand newCommand(TokenDeque cmdWords, MqContextConnection client)
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
    
    if (DefCommand.sCommandVerbs.contains(verb))
      return new DefCommand(cmdWords, client);
    
    if (DelCommand.sCommandVerbs.contains(verb))
      return new DelCommand(cmdWords, client);
    
    if (QueryCommand.sCommandVerbs.contains(verb))
      return new QueryCommand(cmdWords, client);
    
    if (PutCommand.sCommandVerbs.contains(verb))
      return new PutCommand(cmdWords, client);
    
    if (GetCommand.sCommandVerbs.contains(verb))
      return new GetCommand(cmdWords, client);
    
    if (TermCommand.sCommandVerbs.contains(verb))
      return new TermCommand(cmdWords, client);
    
    if (AltCommand.sCommandVerbs.contains(verb))
        return new AltCommand(cmdWords, client);
    
    return null;
  }
}
