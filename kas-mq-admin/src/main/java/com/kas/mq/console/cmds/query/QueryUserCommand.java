package com.kas.mq.console.cmds.query;

import com.kas.infra.utils.ConsoleUtils;
import com.kas.mq.console.ACommand;
import com.kas.mq.internal.MqContextConnection;

/**
 * QUERY USER command
 * 
 * @author Pippo
 */
public class QueryUserCommand extends ACommand
{
  /**
   * User attributes
   */
  private String mName;
  
  /**
   * Construct the command and setting its verbs
   */
  QueryUserCommand()
  {
    mCommandVerbs.add("USER");
    mCommandVerbs.add("USR");
    mCommandVerbs.add("U");
  }
  
  /**
   * Setting data members
   */
  protected void setup()
  {
    mName = getString("NAME", null);
  }
  
  /**
   * Execute the command using the specified {@link MqContextConnection}
   * 
   * @param conn The {@link MqContextConnection} that will be used to execute the command
   */
  public void exec(MqContextConnection conn)
  {
  }
  
  /**
   * Display help screen for this command.
   */
  public void help()
  {
    ConsoleUtils.writelnGreen("Purpose: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Query a user entity.");
    ConsoleUtils.writeln("     This command will display information (passwords excluded) on the user identified by NAME.");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Format: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("       >>--QUERY|Q--+--USER|USR|U--+--NAME(name)--><");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Where: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeRed("    name:       ");
    ConsoleUtils.writeln("User name.");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Examples:");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Query user ARYA:");
    ConsoleUtils.writeln("          KAS/MQ Admin> QUERY USER NAME(ARYA)");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Query all users whose name is prefixed with U01:");
    ConsoleUtils.writeln("          KAS/MQ Admin> Q USER NAME(U01*)");
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
    sb.append("  USER").append('\n')
      .append("    NAME(").append(mName).append(")\n");
    return sb.toString();
  }
}
