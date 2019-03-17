package com.kas.mq.console.cmds.term;

import com.kas.infra.utils.ConsoleUtils;
import com.kas.mq.console.ACommand;
import com.kas.mq.internal.MqContextConnection;

/**
 * TERMINATE SERVER command
 * 
 * @author Pippo
 */
public class TermServerCommand extends ACommand
{
  /**
   * Construct the command and setting its verbs
   */
  TermServerCommand()
  {
    mCommandVerbs.add("SERVER");
    mCommandVerbs.add("SERV");
    mCommandVerbs.add("SRVR");
    mCommandVerbs.add("SRV");
  }
  
  /**
   * Execute the command using the specified {@link MqContextConnection}
   * 
   * @param conn
   *   The {@link MqContextConnection} that will be used to execute the command
   */
  public void exec(MqContextConnection conn)
  {
    boolean shutdown = conn.termServer();
    ConsoleUtils.writeln("%s", conn.getResponse());
    if (shutdown)
    {
      conn.disconnect();
    }
  }
  
  /**
   * Display help screen for this command.
   */
  public void help()
  {
    ConsoleUtils.writelnGreen("Purpose: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Terminate the server.");
    ConsoleUtils.writeln("     This command will terminate the server to which the admin console is connected.");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Format: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("       >>--TERMINATE|TERM|TRM--+--SERVER|SERV|SRVR|SRV--+--><");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Examples:");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Terminate the server:");
    ConsoleUtils.writeln("          KAS/MQ Admin> TERMINATE SERVER");
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
    sb.append("  SERVER");
    return sb.toString();
  }
}
