package com.kas.mq.admin.cmds.term;

import com.kas.infra.base.UniqueId;
import com.kas.infra.typedef.TokenDeque;
import com.kas.mq.admin.cmds.ACliCommand;
import com.kas.mq.internal.MqContextConnection;

/**
 * A TERMINATE SESSION command
 * 
 * @author Pippo
 */
public class TermSessCommand extends ACliCommand
{
  /**
   * Construct a {@link TermSessCommand} passing the command arguments and the client object
   * that will perform actions on behalf of this command.
   * 
   * @param args The command arguments specified when command was entered
   * @param client The client that will perform the actual connection
   */
  protected TermSessCommand(TokenDeque args, MqContextConnection client)
  {
    super(args, client);
  }

  /**
   * Display help screen for this command.
   * Not in use for TERMINATE CONNECTION.
   */
  public void help()
  {
    writeln(" ");
  }
  
  /**
   * A terminate session command.<br>
   * <br>
   * The command expects the "TERMINATE SESSION" followed by one argument. For zero arguments -
   * The command will fail with "missing session id" message.<br>
   * For more than that, the command will fail with an "excessive arguments" message.<br>
   * The single argument must be a valid UUID or the command will fail with "invalid session id" message. 
   * 
   * @return {@code false} always because there is no way that this command will terminate the command processor.
   */
  public boolean run()
  {
    if (mCommandArgs.size() == 0)
    {
      writeln("Missing session ID");
      writeln(" ");
      return false;
    }
    
    String id = mCommandArgs.poll();
    UniqueId uuid = null;
    try
    {
      uuid = UniqueId.fromString(id);
    }
    catch (IllegalArgumentException e)
    {
      writeln("Invalid session id \"" + id + "\"");
      writeln(" ");
      return false;
    }
    
    if (mCommandArgs.size() > 0)
    {
      writeln("Excessive token \"" + mCommandArgs.poll() + "\"");
      writeln(" ");
      return false;
    }
    
    mClient.termSess(uuid);
    writeln(mClient.getResponse());
    writeln(" ");
    return false;
  }
}
