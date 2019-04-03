package com.kas.mq.admin.cmds;

import com.kas.infra.utils.ConsoleUtils;
import com.kas.mq.admin.ACommand;
import com.kas.mq.admin.ICommand;
import com.kas.mq.admin.ICommandFactory;
import com.kas.mq.admin.cmds.query.QueryCommandFactory;
import com.kas.mq.internal.MqContextConnection;

/**
 * QUERY command
 * 
 * @author Pippo
 */
public class QueryCommand extends ACommand
{
  /**
   * A factory responsible for creating {@link ICommand} according to sub-verb
   */
  private ICommandFactory mFactory;
  
  /**
   * Construct the command and setting its verbs
   */
  QueryCommand()
  {
    mCommandVerbs.add("QUERY");
    mCommandVerbs.add("Q");
    
    mFactory = new QueryCommandFactory();
    mFactory.init();
  }
  
  /**
   * Overriding the default {@link ACommand#parse(String)}
   * 
   * @param text
   *   The text passed to this command
   */
  public void parse(String text)
  {
    mCommandText = text;
  }
  
  /**
   * Execute the command using the specified {@link MqContextConnection}
   * 
   * @param conn
   *   The {@link MqContextConnection} that will be used to execute the command
   */
  public void exec(MqContextConnection conn)
  {
    ICommand cmd = mFactory.newCommand(mCommandText);
    if (cmd == null)
    {
      ConsoleUtils.writeln("Unknown sub-verb: [%s]", mCommandText);
      return;
    }
    
    cmd.exec(conn);
  }
  
  /**
   * Display help screen for this command.
   */
  public void help()
  {
    if ((mCommandText != null) && (mCommandText.length() > 0))
    {
      ICommand cmd = mFactory.newCommand(mCommandText);
      if (cmd != null)
      {
        cmd.help();
      }
      else
      {
        ConsoleUtils.writelnGreen("No help for command [QUERY %s]: ", mCommandText);
      }
    }
    else
    {
      ConsoleUtils.writelnGreen("Purpose: ");
      ConsoleUtils.writeln(" ");
      ConsoleUtils.writeln("     Query entities. Type HELP QUERY vvvvv for specifics.");
      ConsoleUtils.writeln(" ");
      ConsoleUtils.writelnGreen("Format: ");
      ConsoleUtils.writeln(" ");
      ConsoleUtils.writeln("     >>--QUERY|Q--+------------------------------+--><");
      ConsoleUtils.writeln("                  |                              |");
      ConsoleUtils.writeln("                  +--CONFIG--config_options------+");
      ConsoleUtils.writeln("                  |                              |");
      ConsoleUtils.writeln("                  +--CONNECTION--conn_options----+");
      ConsoleUtils.writeln("                  |                              |");
      ConsoleUtils.writeln("                  +--SESSION--sess_options-------+");
      ConsoleUtils.writeln("                  |                              |");
      ConsoleUtils.writeln("                  +--QUEUE--queue_options--------+");
      ConsoleUtils.writeln("                  |                              |");
      ConsoleUtils.writeln("                  +--GROUP--group_options--------+");
      ConsoleUtils.writeln("                  |                              |");
      ConsoleUtils.writeln("                  +--USER---user_options---------+");
      ConsoleUtils.writeln(" ");
    }
  }
  
  /**
   * Get the command text
   * 
   * @return
   *   the command text
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("QUERY");
    return sb.toString();
  }
}
