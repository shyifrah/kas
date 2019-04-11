package com.kas.mq.admin.cmds.query;

import com.kas.infra.utils.ConsoleUtils;
import com.kas.infra.utils.Validators;
import com.kas.mq.admin.ACommand;
import com.kas.mq.impl.messages.MqStringMessage;
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
   * @param conn
   *   The {@link MqContextConnection} that will be used to execute the command
   */
  public void exec(MqContextConnection conn)
  {
    boolean prefix = false;
    if (mName.endsWith("*"))
    {
      mName = mName.substring(0, mName.length()-1);
      prefix = true;
    }
    
    if ((mName.length() > 0) && (!Validators.isUserName(mName)))
      throw new IllegalArgumentException("NAME was not specified or invalid: [" + mName + ']');
    
    MqStringMessage result = conn.queryUser(mName, prefix);
    if (result != null)
      ConsoleUtils.writeln("%s", result.getBody());
    ConsoleUtils.writeln("%s", conn.getResponse());
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
   * @return
   *   the command text
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("  USER").append('\n')
      .append("    NAME(").append(mName).append(")\n");
    return sb.toString();
  }
}
