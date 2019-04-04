package com.kas.mq.admin.cmds;

import com.kas.infra.utils.ConsoleUtils;
import com.kas.mq.admin.ACommand;
import com.kas.mq.internal.MqContextConnection;

/**
 * DISCONNECT command
 * 
 * @author Pippo
 */
public class DisconnectCommand extends ACommand
{
  /**
   * Construct the command and setting its verbs
   */
  DisconnectCommand()
  {
    mCommandVerbs.add("DISCONNECT");
    mCommandVerbs.add("DISCONN");
    mCommandVerbs.add("DISC");
  }
  
  /**
   * Setting data members
   */
  protected void setup()
  {
  }
  
  /**
   * Execute the command using the specified {@link MqContextConnection}
   * 
   * @param conn
   *   The {@link MqContextConnection} that will be used to execute the command
   */
  public void exec(MqContextConnection conn)
  {
    conn.disconnect();
    ConsoleUtils.writeln("%s", conn.getResponse());
  }
  
  /**
   * Print HELP screen for the specified command.
   */
  public void help()
  {
    ConsoleUtils.writelnGreen("Purpose: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Disconnect current active connection.");
    ConsoleUtils.writeln("     If a connection is not active, the command is simply ignored.");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Format: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     >>--DISCONNECT|DISCONN|DISC--><");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Examples:");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Disconnect current connection:");
    ConsoleUtils.writeln("          KAS/MQ Admin> DISCONNECT");
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
    sb.append("DISCONNECT");
    return sb.toString();
  }
}
