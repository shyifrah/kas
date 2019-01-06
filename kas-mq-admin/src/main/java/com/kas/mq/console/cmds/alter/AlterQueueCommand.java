package com.kas.mq.console.cmds.alter;

import com.kas.mq.console.ACommand;
import com.kas.mq.internal.EQueueDisp;
import com.kas.mq.internal.MqContextConnection;

/**
 * ALTER QUEUE command
 * 
 * @author Pippo
 */
public class AlterQueueCommand extends ACommand
{
  /**
   * Queue attributes
   */
  private String mName;
  private String mDescription;
  private Integer mThreshold;
  private EQueueDisp mDisposition;
  
  /**
   * Construct the command and setting its verbs
   */
  AlterQueueCommand()
  {
    mCommandVerbs.add("QUEUE");
    mCommandVerbs.add("Q");
  }
  
  /**
   * Setting data members
   */
  protected void setup()
  {
    mName = getString("NAME", null);
    mDescription = getString("DESCRIPTION", "");
    mThreshold = getInteger("THRESHOLD", 1000);
    mDisposition = getEnum("DISPOSITION", EQueueDisp.class, EQueueDisp.TEMPORARY);
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
    sb.append("ALTER QUEUE").append('\n')
      .append(" NAME(").append(mName).append(")\n")
      .append(" DESCRIPTION(").append(mDescription).append(")\n")
      .append(" THRESHOLD(").append(mThreshold).append(")\n")
      .append(" DISPOSITION(").append(mDisposition).append(")\n");
    return sb.toString();
  }
}
