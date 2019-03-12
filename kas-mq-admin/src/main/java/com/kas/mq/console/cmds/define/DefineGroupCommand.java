package com.kas.mq.console.cmds.define;

import com.kas.infra.utils.ConsoleUtils;
import com.kas.infra.utils.Validators;
import com.kas.mq.console.ACommand;
import com.kas.mq.internal.MqContextConnection;

/**
 * DEFINE GROUP command
 * 
 * @author Pippo
 */
public class DefineGroupCommand extends ACommand
{
  /**
   * Group attributes
   */
  private String mName;
  private String mDescription;
  
  /**
   * Construct the command and setting its verbs
   */
  DefineGroupCommand()
  {
    mCommandVerbs.add("GROUP");
    mCommandVerbs.add("GRP");
    mCommandVerbs.add("G");
  }
  
  /**
   * Setting data members
   */
  protected void setup()
  {
    mName = getString("NAME", null);
    mDescription = getString("DESCRIPTION", "");
  }
  
  /**
   * Execute the command using the specified {@link MqContextConnection}
   * 
   * @param conn The {@link MqContextConnection} that will be used to execute the command
   */
  public void exec(MqContextConnection conn)
  {
    if (!Validators.isUserName(mName))
      throw new IllegalArgumentException("NAME was not specified or invalid: [" + mName + ']');
    if (!Validators.isUserDesc(mDescription))
      throw new IllegalArgumentException("Invalid DESCRIPTION: [" + mDescription + "]; Value cannot exceed 256 characters");
    
    conn.defineGroup(mName, mDescription);
    ConsoleUtils.writeln("%s", conn.getResponse());
  }
  
  /**
   * Display help screen for this command.
   */
  public void help()
  {
    ConsoleUtils.writelnGreen("Purpose: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Define a group entity.");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Format: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("                                                      +--DESCRIPTION()------+");
    ConsoleUtils.writeln("                                                      |                     |");
    ConsoleUtils.writeln("       >>--DEFINE|DEF--+--GROUP|GRP|G--+--NAME(name)--+---------------------+--><");
    ConsoleUtils.writeln("                                                      |                     |");
    ConsoleUtils.writeln("                                                      +--DESCRIPTION(desc)--+");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Where: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeRed("    name:       ");
    ConsoleUtils.writeln("Group name.");
    ConsoleUtils.writeRed("    desc:       ");
    ConsoleUtils.writeln("Group description.");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Examples:");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Define a group without description:");
    ConsoleUtils.writeln("          KAS/MQ Admin> DEFINE GROUP NAME(STARKS)");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Define a group with a description:");
    ConsoleUtils.writeln("          KAS/MQ Admin> DEF G NAME(DOSHKHALEEN) DESCRIPTION(Widows of Khals)");
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
    sb.append("  GROUP").append('\n')
      .append("    NAME(").append(mName).append(")\n")
      .append("    DESCRIPTION(").append(mDescription).append(")\n");
    return sb.toString();
  }
}
