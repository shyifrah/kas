package com.kas.mq.console.cmds;

import com.kas.mq.console.ACommand;
import com.kas.mq.console.ICommand;
import com.kas.mq.console.cmds.define.DefineCommandFactory;
import com.kas.mq.internal.MqContextConnection;

/**
 * DEFINE command
 * 
 * @author Pippo
 */
public class DefineCommand extends ACommand
{
  /**
   * A factory responsible for creating {@link ICommand} according to sub-verb
   */
  private DefineCommandFactory mFactory;
  
  /**
   * Construct the command and setting its verbs
   */
  DefineCommand()
  {
    mCommandVerbs.add("DEFINE");
    mCommandVerbs.add("DEF");
    
    mFactory = new DefineCommandFactory();
    mFactory.init();
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
    ICommand cmd = mFactory.newCommand(mCommandText);
    if (cmd != null)
      cmd.exec(conn);
  }
  
  /**
   * Get the command text
   * 
   * @return the command text
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("DEFINE...");
    return sb.toString();
  }
}
