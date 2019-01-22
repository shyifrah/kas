package com.kas.mq.console.cmds;

import com.kas.mq.console.ACommand;
import com.kas.mq.internal.MqContextConnection;

/**
 * EXIT command
 * 
 * @author Pippo
 */
public class ExitCommand extends ACommand
{
  /**
   * Construct the command and setting its verbs
   */
  ExitCommand()
  {
    mCommandVerbs.add("EXIT");
    mCommandVerbs.add("QUIT");
    mCommandVerbs.add("BYE");
  }
  
  /**
   * Execute the command using the specified {@link MqContextConnection}
   * 
   * @param conn The {@link MqContextConnection} that will be used to execute the command
   */
  public void exec(MqContextConnection conn)
  {
    throw new RuntimeException("Terminating admin console");
  }
  
  /**
   * Get the command text
   * 
   * @return the command text
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("EXIT");
    return sb.toString();
  }
}
