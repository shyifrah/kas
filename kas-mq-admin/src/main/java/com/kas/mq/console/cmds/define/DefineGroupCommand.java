package com.kas.mq.console.cmds.define;

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
  }
  
  /**
   * Get the command text
   * 
   * @return the command text
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("DEFINE GROUP").append('\n')
      .append(" NAME(").append(mName).append(")\n")
      .append(" DESCRIPTION(").append(mDescription).append(")\n");
    return sb.toString();
  }
}
