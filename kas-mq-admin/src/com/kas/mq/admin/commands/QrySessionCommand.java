package com.kas.mq.admin.commands;

import java.util.Scanner;
import com.kas.infra.typedef.TokenDeque;
import com.kas.mq.impl.internal.MqConnection;

/**
 * A QUERY SESSION command
 * 
 * @author Pippo
 */
public class QrySessionCommand extends ACliCommand
{
  /**
   * Construct a {@link QrySessionCommand} passing the command arguments and the client object
   * that will perform actions on behalf of this command.
   * 
   * @param scanner A scanner to be used in case of further interaction is needed 
   * @param args The command arguments specified when command was entered
   * @param client The client that will perform the actual connection
   */
  protected QrySessionCommand(Scanner scanner, TokenDeque args, MqConnection client)
  {
    super(scanner, args, client);
  }

  /**
   * Display help screen for this command.
   * Not in use for QUERY SESSION.
   */
  public void help()
  {
    writeln(" ");
  }
  
  /**
   * A query session command.<br>
   * <br>
   * If more tokens than the "QUERY SESSION" are specified, the command will fail with an excessive arguments message.
   * 
   * @return {@code false} always because there is no way that this command will terminate the command processor.
   */
  public boolean run()
  {
    if (mCommandArgs.size() > 0)
    {
      writeln("Excessive token \"" + mCommandArgs.poll() + "\"");
      writeln(" ");
      return false;
    }
    
    writeln(mClient.toString());
    writeln(" ");
    return false;
  }
}
