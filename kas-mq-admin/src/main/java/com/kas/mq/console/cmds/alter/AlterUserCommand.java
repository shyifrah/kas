package com.kas.mq.console.cmds;

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
   * Construct the command
   * 
   * @param verb The command verb
   * @param args The argument string
   */
  public AlterUserCommand(String verb, String args)
  {
    super(verb, args);
  }
  
  /**
   * Setting data members
   */
  protected void setup()
  {
    mName = getString("USER", null);
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
    if (mName == null)
      throw new IllegalArgumentException("USER name was not specified");
    if (mPassword == null)
      throw new IllegalArgumentException("USER password was not specified");
    if (mGroups.isEmpty())
      throw new IllegalArgumentException("USER must be a member of at least one group");
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
    sb.append("ALTER").append('\n')
      .append(" USER(").append(mName).append(")\n")
      .append(" PASSWORD(").append(mPassword).append(")\n")
      .append(" DESCRIPTION(").append(mDescription).append(")\n")
      .append(" GROUPS(").append(mGroups).append(")\n");
    return sb.toString();
  }
}
