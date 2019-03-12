package com.kas.mq.console.cmds;

import com.kas.infra.utils.ConsoleUtils;
import com.kas.mq.console.ACommand;
import com.kas.mq.console.ICommand;
import com.kas.mq.console.ICommandFactory;
import com.kas.mq.console.cmds.term.TermCommandFactory;
import com.kas.mq.internal.MqContextConnection;

/**
 * TERMINATE command
 * 
 * @author Pippo
 */
public class TermCommand extends ACommand
{
  /**
   * A factory responsible for creating {@link ICommand} according to sub-verb
   */
  private ICommandFactory mFactory;
  
  /**
   * Construct the command and setting its verbs
   */
  TermCommand()
  {
    mCommandVerbs.add("TERMINATE");
    mCommandVerbs.add("TERM");
    mCommandVerbs.add("TRM");
    
    mFactory = new TermCommandFactory();
    mFactory.init();
  }
  
  /**
   * Overriding the default {@link ACommand#parse(String)}
   * 
   * @param text The text passed to this command
   */
  public void parse(String text)
  {
    mCommandText = text;
  }
  
  /**
   * Execute the command using the specified {@link MqContextConnection}
   * 
   * @param conn The {@link MqContextConnection} that will be used to execute the command
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
        ConsoleUtils.writelnGreen("No help for command [TERMINATE %s]: ", mCommandText);
      }
    }
    else
    {
      ConsoleUtils.writelnGreen("Purpose: ");
      ConsoleUtils.writeln(" ");
      ConsoleUtils.writeln("     Terminate active entities. Type HELP TERMINATE vvvvv for specifics.");
      ConsoleUtils.writeln(" ");
      ConsoleUtils.writelnGreen("Format: ");
      ConsoleUtils.writeln(" ");
      ConsoleUtils.writeln("     >>--TERMINATE|TERM|TRM--+------------------------------+--><");
      ConsoleUtils.writeln("                             |                              |");
      ConsoleUtils.writeln("                             +--SERVER--server_options------+");
      ConsoleUtils.writeln("                             |                              |");
      ConsoleUtils.writeln("                             +--CONNECTION--conn_options----+");
      ConsoleUtils.writeln("                             |                              |");
      ConsoleUtils.writeln("                             +--SESSION--sess_options-------+");
      ConsoleUtils.writeln(" ");
    }
  }
  
  /**
   * Get the command text
   * 
   * @return the command text
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("TERMINATE");
    return sb.toString();
  }
}