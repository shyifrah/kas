package com.kas.mq.console.cmds;

import com.kas.infra.utils.ConsoleUtils;
import com.kas.mq.console.ACommand;
import com.kas.mq.internal.MqContextConnection;

/**
 * DISCONNECT command
 * 
 * @author Pippo
 */
public class DisconnectCommand extends ACommand
{
  /**
   * Construct the command and setting its verbs
   */
  DisconnectCommand()
  {
    mCommandVerbs.add("DISCONNECT");
    mCommandVerbs.add("DISCONN");
    mCommandVerbs.add("DISC");
  }
  
  /**
   * Setting data members
   */
  protected void setup()
  {
  }
  
  /**
   * Execute the command using the specified {@link MqContextConnection}
   * 
   * @param conn The {@link MqContextConnection} that will be used to execute the command
   */
  public void exec(MqContextConnection conn)
  {
    conn.disconnect();
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
    sb.append("DISCONNECT");
    return sb.toString();
  }
}
