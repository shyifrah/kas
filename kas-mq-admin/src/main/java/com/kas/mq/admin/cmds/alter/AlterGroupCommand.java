package com.kas.mq.admin.cmds.alter;

import com.kas.infra.utils.ConsoleUtils;
import com.kas.mq.admin.ACommand;
import com.kas.mq.internal.MqContextConnection;

/**
 * ALTER GROUP command
 * 
 * @author Pippo
 */
public class AlterGroupCommand extends ACommand
{
  /**
   * Group attributes
   */
  private String mName;
  private String mNewName;
  private String mDescription;
  
  /**
   * Construct the command and setting its verbs
   */
  AlterGroupCommand()
  {
    mCommandVerbs.add("ALTER GROUP");
    mCommandVerbs.add("ALTER GRP");
    mCommandVerbs.add("ALTER G");
  }
  
  /**
   * Setting data members
   */
  protected void setup()
  {
    mName = getString("NAME", null);
    mNewName = getString("NEWNAME", null);
    mDescription = getString("DESCRIPTION", "");
  }
  
  /**
   * Execute the command using the specified {@link MqContextConnection}
   * 
   * @param conn
   *   The {@link MqContextConnection} that will be used to execute the command
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
    ConsoleUtils.writeln("     Alter a group entity.");
    ConsoleUtils.writeln("     You can alter a group's name by using the NEWNAME attribute or its DESCRIPTION.");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Format: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("       >>--ALTER|ALT|AL--+--GROUP|GRP|G--+--NAME(name)--+--------------------+--+---------------------+--><");
    ConsoleUtils.writeln("                                                        |                    |  |                     |");
    ConsoleUtils.writeln("                                                        +--NEWNAME(newname)--+  +--DESCRIPTION(desc)--+");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Where: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeRed("    name:       ");
    ConsoleUtils.writeln("Group name.");
    ConsoleUtils.writeRed("    newname:    ");
    ConsoleUtils.writeln("The new name to assign to this group.");
    ConsoleUtils.writeRed("    desc:       ");
    ConsoleUtils.writeln("Group description.");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Examples:");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Alter group description:");
    ConsoleUtils.writeln("          KAS/MQ Admin> ALTER GROUP NAME(STARKS) DESCRIPTION(The North Remembers)");
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
    sb.append("  GROUP").append('\n')
      .append("    NAME(").append(mName).append(")\n")
      .append("    NEWNAME(").append(mNewName).append(")\n")
      .append("    DESCRIPTION(").append(mDescription).append(")\n");
    return sb.toString();
  }
}
