package com.kas.mq.admin.commands;

import java.util.Scanner;
import com.kas.infra.base.UniqueId;
import com.kas.infra.typedef.TokenDeque;
import com.kas.mq.impl.MqContext;

/**
 * A TERMINATE CONNECTION command
 * 
 * @author Pippo
 */
public class TrmConnectionCommand extends ACliCommand
{
  /**
   * Construct a {@link TrmConnectionCommand} passing the command arguments and the client object
   * that will perform actions on behalf of this command.
   * 
   * @param scanner A scanner to be used in case of further interaction is needed 
   * @param args The command arguments specified when command was entered
   * @param client The client that will perform the actual connection
   */
  protected TrmConnectionCommand(Scanner scanner, TokenDeque args, MqContext client)
  {
    super(scanner, args, client);
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
   * A terminate connection command.<br>
   * <br>
   * The command expects the "TERMINATE CONNECTION" followed by one argument. For zero arguments -
   * The command will fail with "missing connection id" message.<br>
   * For more than that, the command will fail with an "excessive arguments" message.<br>
   * The single argument must be a valid UUID or the command will fail with "invalid connection id" message. 
   * 
   * @return {@code false} always because there is no way that this command will terminate the command processor.
   */
  public boolean run()
  {
    if (mCommandArgs.size() == 0)
    {
      writeln("Missing connection ID");
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
      writeln("Invalid connection id \"" + id + "\"");
      writeln(" ");
      return false;
    }
    
    if (mCommandArgs.size() > 0)
    {
      writeln("Excessive token \"" + mCommandArgs.poll() + "\"");
      writeln(" ");
      return false;
    }
    
    mClient.termConn(uuid);
    writeln(mClient.getResponse());
    writeln(" ");
    return false;
  }
}
