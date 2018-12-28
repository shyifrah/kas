package com.kas.mq.console.cmds;

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
   * Construct the command
   * 
   * @param verb The command verb
   * @param args The argument string
   */
  public DeleteGroupCommand(String verb, String args)
  {
    super(verb, args);
  }
  
  /**
   * Setting data members
   */
  protected void setup()
  {
    mName = getString("GROUP", null);
  }
  
  /**
   * Verify mandatory arguments
   */
  protected void verify()
  {
    if (mName == null)
      throw new IllegalArgumentException("USER name was not specified");
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
    sb.append("DELETE").append('\n')
      .append(" GROUP(").append(mName).append(")\n");
    return sb.toString();
  }
}
