package com.kas.mq.console.cmds;

import com.kas.mq.console.ACommand;
import com.kas.mq.internal.MqContextConnection;

/**
 * ALTER GROUP command
 * 
 * @author Pippo
 */
public class AlterGroupCommand extends ACommand
{
  /**
   * Group attributes
   */
  private String mName;
  private String mDescription;
  
  /**
   * Construct the command
   * 
   * @param verb The command verb
   * @param args The argument string
   */
  public AlterGroupCommand(String verb, String args)
  {
    super(verb, args);
  }
  
  /**
   * Setting data members
   */
  protected void setup()
  {
    mName = getString("GROUP", null);
    mDescription = getString("DESCRIPTION", "");
  }
  
  /**
   * Verify mandatory arguments
   */
  protected void verify()
  {
    if (mName == null)
      throw new IllegalArgumentException("GROUP name was not specified");
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
      .append(" GROUP(").append(mName).append(")\n")
      .append(" DESCRIPTION(").append(mDescription).append(")\n");
    return sb.toString();
  }
}
