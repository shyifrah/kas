package com.kas.mq.console.cmds.define;

import com.kas.infra.typedef.StringList;
import com.kas.infra.utils.Validators;
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
   * Verify mandatory arguments
   */
  protected void verify()
  {
    if (!Validators.isUserName(mName))
      throw new IllegalArgumentException("Name was not specified or invalid: [" + mName + "]");
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
    sb.append("DEFINE USER").append('\n')
      .append(" NAME(").append(mName).append(")\n")
      .append(" PASSWORD(").append(mPassword).append(")\n")
      .append(" DESCRIPTION(").append(mDescription).append(")\n")
      .append(" GROUPS(").append(mGroups).append(")\n");
    return sb.toString();
  }
}
