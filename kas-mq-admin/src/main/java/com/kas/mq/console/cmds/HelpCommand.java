package com.kas.mq.console.cmds;

import com.kas.infra.utils.ConsoleUtils;
import com.kas.mq.console.ACommand;
import com.kas.mq.console.ICommand;
import com.kas.mq.internal.MqContextConnection;

/**
 * EXIT command
 * 
 * @author Pippo
 */
public class HelpCommand extends ACommand
{
  /**
   * Construct the command and setting its verbs
   */
  HelpCommand()
  {
    mCommandVerbs.add("HELP");
  }
  
  /**
   * Overriding the default {@link ACommand#parse(String)}
   * 
   * @param text The text passed to this command
   */
  public void parse(String text)
  {
    mCommandText = text;
  }
  
  /**
   * Execute the command using the specified {@link MqContextConnection}
   * 
   * @param conn The {@link MqContextConnection} that will be used to execute the command
   */
  public void exec(MqContextConnection conn)
  {
    ICommand cmd = MainCommandFactory.getInstance().newCommand(mCommandText);
    if (cmd == null)
    {
      ConsoleUtils.writeln("No help for command [%s]", mCommandText);
    }
    else
    {
      cmd.help();
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
    sb.append("HELP");
    return sb.toString();
  }
}
