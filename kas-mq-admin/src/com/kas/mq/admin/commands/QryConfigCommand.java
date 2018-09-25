package com.kas.mq.admin.commands;

import java.util.Scanner;
import com.kas.infra.typedef.TokenDeque;
import com.kas.mq.impl.MqContext;
import com.kas.mq.impl.MqTextMessage;
import com.kas.mq.impl.IMqGlobals.EQueryType;

/**
 * A QUERY CONFIGURATION command
 * 
 * @author Pippo
 */
public class QryConfigCommand extends ACliCommand
{
  /**
   * Construct a {@link QryConfigCommand} passing the command arguments and the client object
   * that will perform actions on behalf of this command.
   * 
   * @param scanner A scanner to be used in case of further interaction is needed 
   * @param args The command arguments specified when command was entered
   * @param client The client that will perform the actual connection
   */
  protected QryConfigCommand(Scanner scanner, TokenDeque args, MqContext client)
  {
    super(scanner, args, client);
  }

  /**
   * Display help screen for this command.
   * Not in use for QUERY CONFIG.
   */
  public void help()
  {
    writeln(" ");
  }
  
  /**
   * A query configuration command.<br>
   * <br>
   * The command expects only the "QUERY CONFIGURATION" verb. If more than that is specified,
   * the command terminates with a excessive token error message.
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
    
    MqTextMessage result = mClient.queryServer(EQueryType.cQueryConfig);
    if (result != null)
      writeln(result.getBody());
    writeln(mClient.getResponse());
    writeln(" ");
    return false;
  }
}
