package com.kas.mq.console.cmds.delete;

import com.kas.infra.utils.ConsoleUtils;
import com.kas.infra.utils.Validators;
import com.kas.mq.console.ACommand;
import com.kas.mq.internal.MqContextConnection;

/**
 * DELETE QUEUE command
 * 
 * @author Pippo
 */
public class DeleteQueueCommand extends ACommand
{
  /**
   * Queue attributes
   */
  private String mName;
  private Boolean mForce;
  
  /**
   * Construct the command and setting its verbs
   */
  DeleteQueueCommand()
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
    mForce = getBoolean("FORCE", false);
  }
  
  /**
   * Verify mandatory arguments
   */
  protected void verify()
  {
    if (!Validators.isQueueName(mName))
      throw new IllegalArgumentException("Name was not specified or invalid: [" + mName + "]");
  }
  
  /**
   * Execute the command using the specified {@link MqContextConnection}
   * 
   * @param conn The {@link MqContextConnection} that will be used to execute the command
   */
  public void exec(MqContextConnection conn)
  {
    conn.deleteQueue(mName, mForce);
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
    sb.append("DELETE QUEUE").append('\n')
      .append(" NAME(").append(mName).append(")\n")
      .append(" FORCE(").append(mForce).append(")\n");
    return sb.toString();
  }
}
