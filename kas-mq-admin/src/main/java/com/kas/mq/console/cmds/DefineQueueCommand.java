package com.kas.mq.console.cmds;

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
   * Construct the command
   * 
   * @param verb The command verb
   * @param args The argument string
   */
  public DefineQueueCommand(String verb, String args)
  {
    super(verb, args);
  }
  
  /**
   * Setting data members
   */
  protected void setup()
  {
    mName = getString("QUEUE", null);
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
      throw new IllegalArgumentException("NAME was not specified or invalid queue name: [" + mName + "]");
    if (!Validators.isThreshold(mThreshold))
      throw new IllegalArgumentException("THRESHOLD is invalid: [" + mThreshold + "]");
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
    sb.append("DEFINE").append('\n')
      .append(" QUEUE(").append(mName).append(")\n")
      .append(" DESCRIPTION(").append(mDescription).append(")\n")
      .append(" THRESHOLD(").append(mThreshold).append(")\n")
      .append(" DISPOSITION(").append(mDisposition).append(")\n");
    return sb.toString();
  }
}
