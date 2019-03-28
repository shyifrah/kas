package com.kas.mq.admin.cmds;

import com.kas.infra.utils.ConsoleUtils;
import com.kas.mq.admin.ACommand;
import com.kas.mq.admin.ICommand;
import com.kas.mq.internal.MqContextConnection;

/**
 * HELP command
 * 
 * @author Pippo
 */
public class HelpCommand extends ACommand
{
  /**
   * Construct the command and setting its verbs
   */
  HelpCommand()
  {
    mCommandVerbs.add("HELP");
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
    ICommand cmd = MainCommandFactory.getInstance().newCommand(mCommandText);
    if (cmd == null)
    {
      ConsoleUtils.writeln("No help for command [%s]", mCommandText);
    }
    else
    {
      cmd.help();
    } 
  }
  
  /**
   * Display help screen for this command.
   */
  public void help()
  {
    ConsoleUtils.writelnGreen("Purpose: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Display help screen regarding KAS/MQ Admin Console commands.");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Format: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     >>--HELP--+--------------+--><");
    ConsoleUtils.writeln("               |              |");
    ConsoleUtils.writeln("               +--ALTER-------+");
    ConsoleUtils.writeln("               |              |");
    ConsoleUtils.writeln("               +--CONNECT-----+");
    ConsoleUtils.writeln("               |              |");
    ConsoleUtils.writeln("               +--DEFINE------+");
    ConsoleUtils.writeln("               |              |");
    ConsoleUtils.writeln("               +--DELETE------+");
    ConsoleUtils.writeln("               |              |");
    ConsoleUtils.writeln("               +--DISCONNECT--+");
    ConsoleUtils.writeln("               |              |");
    ConsoleUtils.writeln("               +--EXIT--------+");
    ConsoleUtils.writeln("               |              |");
    ConsoleUtils.writeln("               +--GET---------+");
    ConsoleUtils.writeln("               |              |");
    ConsoleUtils.writeln("               +--HELP--------+");
    ConsoleUtils.writeln("               |              |");
    ConsoleUtils.writeln("               +--PUT---------+");
    ConsoleUtils.writeln("               |              |");
    ConsoleUtils.writeln("               +--QUERY-------+");
    ConsoleUtils.writeln("               |              |");
    ConsoleUtils.writeln("               +--TERM--------+");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Examples:");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Display general help screen:");
    ConsoleUtils.writeln("          KAS/MQ Admin> HELP");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Display help on the query command:");
    ConsoleUtils.writeln("          KAS/MQ Admin> HELP QUERY");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     This will display an error message, as KUKU is not a valid command verb:");
    ConsoleUtils.writeln("          KAS/MQ Admin> HELP KUKU");
    ConsoleUtils.writeln(" ");
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
    sb.append("HELP");
    return sb.toString();
  }
}
