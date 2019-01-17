package com.kas.mq.console.cmds;

import com.kas.mq.console.ACommand;
import com.kas.mq.console.ICommand;
import com.kas.mq.console.cmds.query.QueryCommandFactory;
import com.kas.mq.internal.MqContextConnection;

/**
 * QUERY command
 * 
 * @author Pippo
 */
public class QueryCommand extends ACommand
{
  /**
   * A factory responsible for creating {@link ICommand} according to sub-verb
   */
  private QueryCommandFactory mFactory;
  
  /**
   * Construct the command and setting its verbs
   */
  QueryCommand()
  {
    mCommandVerbs.add("QUERY");
    mCommandVerbs.add("QRY");
    mCommandVerbs.add("QU");
    mCommandVerbs.add("Q");
    
    mFactory = new QueryCommandFactory();
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
    sb.append("QUERY");
    return sb.toString();
  }
}
