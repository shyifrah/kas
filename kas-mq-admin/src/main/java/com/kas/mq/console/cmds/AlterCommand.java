package com.kas.mq.console.cmds;

import com.kas.infra.utils.ConsoleUtils;
import com.kas.mq.console.ACommand;
import com.kas.mq.console.ICommand;
import com.kas.mq.console.ICommandFactory;
import com.kas.mq.console.cmds.alter.AlterCommandFactory;
import com.kas.mq.internal.MqContextConnection;

/**
 * DELETE command
 * 
 * @author Pippo
 */
public class AlterCommand extends ACommand
{
  /**
   * A factory responsible for creating {@link ICommand} according to sub-verb
   */
  private ICommandFactory mFactory;
  
  /**
   * Construct the command and setting its verbs
   */
  AlterCommand()
  {
    mCommandVerbs.add("ALTER");
    mCommandVerbs.add("ALT");
    mCommandVerbs.add("AL");
    
    mFactory = new AlterCommandFactory();
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
    if (cmd == null)
    {
      ConsoleUtils.writeln("Unknown sub-verb: [%s]", mCommandText);
      return;
    }
    
    cmd.exec(conn);
  }

  /**
   * Print HELP screen for the specified command.
   */
  public void help()
  {
    ICommand cmd = mFactory.newCommand(mCommandText);
    if (cmd != null)
      cmd.help();
  }
  
  /**
   * Get the command text
   * 
   * @return the command text
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("ALTER");
    return sb.toString();
  }
}
