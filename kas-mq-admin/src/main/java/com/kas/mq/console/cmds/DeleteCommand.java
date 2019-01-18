package com.kas.mq.console.cmds;

import com.kas.mq.console.ACommand;
import com.kas.mq.console.ICommand;
import com.kas.mq.console.ICommandFactory;
import com.kas.mq.console.cmds.delete.DeleteCommandFactory;
import com.kas.mq.internal.MqContextConnection;

/**
 * DELETE command
 * 
 * @author Pippo
 */
public class DeleteCommand extends ACommand
{
  /**
   * A factory responsible for creating {@link ICommand} according to sub-verb
   */
  private ICommandFactory mFactory;
  
  /**
   * Construct the command and setting its verbs
   */
  DeleteCommand()
  {
    mCommandVerbs.add("DELETE");
    mCommandVerbs.add("DEL");
    
    mFactory = new DeleteCommandFactory();
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
    sb.append("DELETE");
    return sb.toString();
  }
}