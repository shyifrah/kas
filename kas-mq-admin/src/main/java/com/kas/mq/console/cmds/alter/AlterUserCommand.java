package com.kas.mq.console.cmds.alter;

import com.kas.infra.typedef.StringList;
import com.kas.infra.utils.ConsoleUtils;
import com.kas.mq.console.ACommand;
import com.kas.mq.internal.MqContextConnection;

/**
 * ALTER USER command
 * 
 * @author Pippo
 */
public class AlterUserCommand extends ACommand
{
  /**
   * User attributes
   */
  private String mName;
  private String mNewName;
  private String mPassword;
  private String mDescription;
  private StringList mAddGroups;
  private StringList mRemGroups;
  
  /**
   * Construct the command and setting its verbs
   */
  AlterUserCommand()
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
    mNewName = getString("NEWNAME", null);
    mPassword = getString("PASSWORD", null);
    mDescription = getString("DESCRIPTION", "");
    String addgrps = getString("ADDGROUPS", null);
    mAddGroups = StringList.fromString(addgrps, false);
    String remgrps = getString("REMGROUPS", null);
    mRemGroups = StringList.fromString(remgrps, false);
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
    ConsoleUtils.writeln("     Alter a user entity.");
    ConsoleUtils.writeln("     You can alter a user's name by using the NEWNAME attribute or reset its password using the PASSWORD attribute.");
    ConsoleUtils.writeln("     Other than that, you can alter the list of groups the user is part of, or the user's description.");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Format: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("       >>--ALTER|ALT|AL--+--USER|USR|U--+--NAME(name)--+--------------------+--+---------------------+--+---------------------+- - - -");
    ConsoleUtils.writeln("                                                       |                    |  |                     |  |                     |");
    ConsoleUtils.writeln("                                                       +--NEWNAME(newname)--+  +--PASSWORD(newpass)--+  +--DESCRIPTION(desc)--+");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("                   - - - +---------------------------------+--+---------------------------------+--><");
    ConsoleUtils.writeln("                         |                                 |  |                                 |");
    ConsoleUtils.writeln("                         +--ADDGROUPS(grp1,grp2,...,grpN)--+  +--REMGROUPS(grp1,grp2,...,grpN)--+");
    ConsoleUtils.writelnGreen("Where: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeRed("    name:       ");
    ConsoleUtils.writeln("User name.");
    ConsoleUtils.writeRed("    newname:    ");
    ConsoleUtils.writeln("The new name to assign to this user.");
    ConsoleUtils.writeRed("    newpass:    ");
    ConsoleUtils.writeln("The new password to assign to this user.");
    ConsoleUtils.writeRed("    desc:       ");
    ConsoleUtils.writeln("User description.");
    ConsoleUtils.writeRed("    grpX:       ");
    ConsoleUtils.writeln("List of comma-seperated groups to add or remove from the user definition.");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Examples:");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Alter user description:");
    ConsoleUtils.writeln("          KAS/MQ Admin> ALTER U NAME(ARYA) DESCRIPTION(Arya Stark)");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Alter add user to one group and remove it from another:");
    ConsoleUtils.writeln("          KAS/MQ Admin> ALTER U NAME(ARYA) ADDGROUP(Starks) REMGROUPS(Assassins)");
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
      .append("    NEWNAME(").append(mNewName).append(")\n")
      .append("    PASSWORD(").append(mPassword).append(")\n")
      .append("    DESCRIPTION(").append(mDescription).append(")\n")
      .append("    ADDGROUPS(").append(mAddGroups).append(")\n")
      .append("    REMGROUPS(").append(mRemGroups).append(")\n");
    return sb.toString();
  }
}
