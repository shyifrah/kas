package com.kas.mq.console.cmds;

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
   * Construct the command
   * 
   * @param verb The command verb
   * @param args The argument string
   */
  public DeleteQueueCommand(String verb, String args)
  {
    super(verb, args);
  }
  
  /**
   * Setting data members
   */
  protected void setup()
  {
    mName = getString("QUEUE", null);
    mForce = getBoolean("FORCE", false);
  }
  
  /**
   * Verify mandatory arguments
   */
  protected void verify()
  {
    if (!Validators.isQueueName(mName))
      throw new IllegalArgumentException("QUEUE was not specified or invalid queue name: [" + mName + "]");
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
    sb.append("DELETE").append('\n')
      .append(" QUEUE(").append(mName).append(")\n")
      .append(" FORCE(").append(mForce).append(")\n");
    return sb.toString();
  }
}
