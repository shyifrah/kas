package com.kas.mq.admin.cmds.term;

import com.kas.infra.typedef.TokenDeque;
import com.kas.mq.admin.cmds.ACliCommand;
import com.kas.mq.internal.MqContextConnection;

/**
 * A TERMINATE SERVER (SHUTDOWN) command
 * 
 * @author Pippo
 */
public class TermServerCommand extends ACliCommand
{
  /**
   * Construct a {@link TermServerCommand} passing the command arguments and the client object
   * that will perform actions on behalf of this command.
   * 
   * @param args The command arguments specified when command was entered
   * @param client The client that will perform the actual connection
   */
  protected TermServerCommand(TokenDeque args, MqContextConnection client)
  {
    super(args, client);
  }

  /**
   * Display help screen for this command.
   * Not in use for TERMINATE SERVER.
   */
  public void help()
  {
    writeln(" ");
  }
  
  /**
   * A terminate server (shutdown) command.<br>
   * <br>
   * The command expects the "TERMINATE SERVER" followed by zero arguments. If arguments are received,
   * the command will fail with an "excessive arguments" message.<br> 
   * 
   * @return {@code false} always because there is no way that this command will terminate the command processor.
   */
  public boolean run()
  {
    if (mCommandArgs.size() > 0)
    {
      writeln("Excessive token \"" + mCommandArgs.peek().toUpperCase() + "\"");
      writeln(" ");
      return false;
    }
    
    boolean shutdown = mClient.termServer();
    String resp = mClient.getResponse();
    if (shutdown)
    {
      mClient.disconnect();
    }
    
    writeln(resp);
    writeln(" ");
    return false;
  }
}
