package com.kas.mq.console.cmds;

import com.kas.infra.utils.ConsoleUtils;
import com.kas.mq.console.ACommand;
import com.kas.mq.console.ICommand;
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
  private DeleteCommandFactory mFactory;
  
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
    ICommand cmd = mFactory.newCommand(mArgumentText);
    if (cmd == null)
    {
      ConsoleUtils.writeln("Unknown command: [DELETE %s]", mArgumentText);
    }
    else
    {
      cmd.exec(conn);
      ConsoleUtils.writeln(conn.getResponse());
    }
  }
}
