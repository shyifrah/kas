package com.kas.mq.console.cmds;

import com.kas.infra.utils.ConsoleUtils;
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
   * Setting data members
   */
  protected void setup()
  {
  }
  
  /**
   * Verify mandatory arguments
   */
  protected void verify()
  {
  }
  
  /**
   * Execute the command using the specified {@link MqContextConnection}
   * 
   * @param conn The {@link MqContextConnection} that will be used to execute the command
   */
  public void exec(MqContextConnection conn)
  {
    ICommand cmd = mFactory.newCommand(mArgumentText);
    if (cmd == null)
    {
      ConsoleUtils.writeln("Unknown command: [DEFINE %s]", mArgumentText);
    }
    else
    {
      cmd.exec(conn);
      ConsoleUtils.writeln(conn.getResponse());
    }
  }
}
