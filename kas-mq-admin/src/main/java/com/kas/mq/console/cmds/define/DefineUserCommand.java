package com.kas.mq.console.cmds.define;

import com.kas.infra.typedef.StringList;
import com.kas.infra.utils.ConsoleUtils;
import com.kas.mq.console.ACommand;
import com.kas.mq.internal.MqContextConnection;

/**
 * DEFINE USER command
 * 
 * @author Pippo
 */
public class DefineUserCommand extends ACommand
{
  /**
   * User attributes
   */
  private String mName;
  private String mPassword;
  private String mDescription;
  private StringList mGroups;
  
  /**
   * Construct the command and setting its verbs
   */
  DefineUserCommand()
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
    mPassword = getString("PASSWORD", null);
    mDescription = getString("DESCRIPTION", "");
    String grps = getString("GROUPS", null);
    mGroups = StringList.fromString(grps, false);
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
    ConsoleUtils.writeln("     Define a user entity.");
    ConsoleUtils.writeln("     The NAME and the PASSWORD attributes are mandatory.");
    ConsoleUtils.writeln("     The DESCRIPTION is a free text that describes the user, and is completely ignored.");
    ConsoleUtils.writeln("     The GROUPS is a list a comma-seperated list of groups that the user is to be part of. The list of groups determines");
    ConsoleUtils.writeln("     the user's permissions across the system.");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Format: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("                                                                            +--DESCRIPTION()------+  +--GROUPS()-------------------+");
    ConsoleUtils.writeln("                                                                            |                     |  |                             |");
    ConsoleUtils.writeln("       >>--DEFINE|DEF--+--USER|USR|U--+--NAME(name)--+--PASSWORD(password)--+---------------------+--+-----------------------------+--><");
    ConsoleUtils.writeln("                                                                            |                     |  |                             |");
    ConsoleUtils.writeln("                                                                            +--DESCRIPTION(desc)--+  +--GROUPS(grp1,grp2,..,grpN)--+");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Where: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeRed("    name:       ");
    ConsoleUtils.writeln("User name.");
    ConsoleUtils.writeRed("    desc:       ");
    ConsoleUtils.writeln("User description.");
    ConsoleUtils.writeRed("    grp1-N:     ");
    ConsoleUtils.writeln("List of comma-seperated groups the user it to be a member of.");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Examples:");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Define a user with a password, description and no groups:");
    ConsoleUtils.writeln("          KAS/MQ Admin> DEFINE USER NAME(ARYA) PASSWORD(123456) DESCRIPTION(Arya Stark)");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Define a user with a password, description and a single group:");
    ConsoleUtils.writeln("          KAS/MQ Admin> DEF USR NAME(HOUND) PASSWORD(123456) DESCRIPTION(Gregor Clegane) GROUPS(ADMINS)");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Define a user with a password, description and a single group:");
    ConsoleUtils.writeln("          KAS/MQ Admin> DEF U NAME(DAENERYS) PASSWORD(123456) DESCRIPTION(Daenerys Targaryen) GROUPS(TARGARYENS,DOTHRAKIS,DRAGON-MOTHERS)");
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
      .append("    NAME(").append(mName).append(")\n")
      .append("    PASSWORD(").append(mPassword).append(")\n")
      .append("    DESCRIPTION(").append(mDescription).append(")\n")
      .append("    GROUPS(").append(mGroups).append(")\n");
    return sb.toString();
  }
}
