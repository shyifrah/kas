package com.kas.mq.console.cmds;

import com.kas.mq.console.ACommand;
import com.kas.mq.console.ICommand;
import com.kas.mq.console.ICommandFactory;
import com.kas.mq.console.cmds.term.TermCommandFactory;
import com.kas.mq.internal.MqContextConnection;

/**
 * TERMINATE command
 * 
 * @author Pippo
 */
public class TermCommand extends ACommand
{
  /**
   * A factory responsible for creating {@link ICommand} according to sub-verb
   */
  private ICommandFactory mFactory;
  
  /**
   * Construct the command and setting its verbs
   */
  TermCommand()
  {
    mCommandVerbs.add("TERMINATE");
    mCommandVerbs.add("TERM");
    mCommandVerbs.add("TRM");
    
    mFactory = new TermCommandFactory();
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
    sb.append("TERMINATE");
    return sb.toString();
  }
}
