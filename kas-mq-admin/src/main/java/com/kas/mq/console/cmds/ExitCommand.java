package com.kas.mq.console.cmds;

import com.kas.infra.utils.ConsoleUtils;
import com.kas.mq.console.ACommand;
import com.kas.mq.internal.MqContextConnection;

/**
 * EXIT command
 * 
 * @author Pippo
 */
public class ExitCommand extends ACommand
{
  /**
   * Construct the command and setting its verbs
   */
  ExitCommand()
  {
    mCommandVerbs.add("EXIT");
    mCommandVerbs.add("QUIT");
  }
  
  /**
   * Execute the command using the specified {@link MqContextConnection}
   * 
   * @param conn The {@link MqContextConnection} that will be used to execute the command
   */
  public void exec(MqContextConnection conn)
  {
    throw new RuntimeException("Terminating admin console");
  }
  
  /**
   * Print HELP screen for the specified command.
   */
  public void help()
  {
    ConsoleUtils.writelnGreen("Purpose: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Terminate KAS/MQ Admin Console.");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Format: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     >>--EXIT|QUIT--><");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Examples:");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Terminates the current KAS/MQ Admin session:");
    ConsoleUtils.writeln("          KAS/MQ Admin> EXIT");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     This will display an error message, as EXIT command does not expect any arguments:");
    ConsoleUtils.writeln("          KAS/MQ Admin> EXIT KUKU");
    ConsoleUtils.writeln(" ");
  }
  
  /**
   * Get the command text
   * 
   * @return the command text
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("EXIT");
    return sb.toString();
  }
}
