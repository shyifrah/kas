package com.kas.mq.console.cmds.term;

import com.kas.infra.utils.ConsoleUtils;
import com.kas.mq.console.ACommand;
import com.kas.mq.internal.MqContextConnection;

/**
 * TERMINATE SERVER command
 * 
 * @author Pippo
 */
public class TermServerCommand extends ACommand
{
  /**
   * Construct the command and setting its verbs
   */
  TermServerCommand()
  {
    mCommandVerbs.add("SERVER");
    mCommandVerbs.add("SERV");
    mCommandVerbs.add("SRVR");
    mCommandVerbs.add("SRV");
  }
  
  /**
   * Execute the command using the specified {@link MqContextConnection}
   * 
   * @param conn The {@link MqContextConnection} that will be used to execute the command
   */
  public void exec(MqContextConnection conn)
  {
    boolean shutdown = conn.termServer();
    ConsoleUtils.writeln("%s", conn.getResponse());
    if (shutdown)
    {
      conn.disconnect();
    }
  }
  
  /**
   * Get the command text
   * 
   * @return the command text
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("  SERVER");
    return sb.toString();
  }
}
