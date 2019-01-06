package com.kas.mq.console.cmds.define;

import com.kas.infra.utils.ConsoleUtils;
import com.kas.infra.utils.Validators;
import com.kas.mq.console.ACommand;
import com.kas.mq.internal.EQueueDisp;
import com.kas.mq.internal.MqContextConnection;

/**
 * DEFINE QUEUE command
 * 
 * @author Pippo
 */
public class DefineQueueCommand extends ACommand
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
  DefineQueueCommand()
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
   * Verify mandatory arguments
   */
  protected void verify()
  {
    if (!Validators.isQueueName(mName))
      throw new IllegalArgumentException("Name was not specified or invalid: [" + mName + "]");
    if (!Validators.isThreshold(mThreshold))
      throw new IllegalArgumentException("Threshold was not specified or invalid: [" + mThreshold + "]");
  }
  
  /**
   * Execute the command using the specified {@link MqContextConnection}
   * 
   * @param conn The {@link MqContextConnection} that will be used to execute the command
   */
  public void exec(MqContextConnection conn)
  {
    conn.defineQueue(mName, mDescription, mThreshold, mDisposition);
    ConsoleUtils.writeln("%s", conn.getResponse());
  }
  
  /**
   * Get the command text
   * 
   * @return the command text
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("DEFINE QUEUE").append('\n')
      .append(" NAME(").append(mName).append(")\n")
      .append(" DESCRIPTION(").append(mDescription).append(")\n")
      .append(" THRESHOLD(").append(mThreshold).append(")\n")
      .append(" DISPOSITION(").append(mDisposition).append(")\n");
    return sb.toString();
  }
}
