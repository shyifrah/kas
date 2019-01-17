package com.kas.mq.console.cmds.alter;

import com.kas.infra.typedef.StringList;
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
  private String mPassword;
  private String mDescription;
  private StringList mGroups;
  
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
   * Get the command text
   * 
   * @return the command text
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("USER").append('\n')
      .append(" NAME(").append(mName).append(")\n")
      .append(" PASSWORD(").append(mPassword).append(")\n")
      .append(" DESCRIPTION(").append(mDescription).append(")\n")
      .append(" GROUPS(").append(mGroups).append(")\n");
    return sb.toString();
  }
}
