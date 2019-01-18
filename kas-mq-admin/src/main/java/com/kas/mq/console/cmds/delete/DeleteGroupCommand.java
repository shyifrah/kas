package com.kas.mq.console.cmds.delete;

import com.kas.mq.console.ACommand;
import com.kas.mq.internal.MqContextConnection;

/**
 * DELETE GROUP command
 * 
 * @author Pippo
 */
public class DeleteGroupCommand extends ACommand
{
  /**
   * Group attributes
   */
  private String mName;
  
  /**
   * Construct the command and setting its verbs
   */
  DeleteGroupCommand()
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
    sb.append("  GROUP").append('\n')
      .append("    NAME(").append(mName).append(")\n");
    return sb.toString();
  }
}
