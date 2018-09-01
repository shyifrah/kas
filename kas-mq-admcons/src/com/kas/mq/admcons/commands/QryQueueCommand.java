package com.kas.mq.admcons.commands;

import java.util.Scanner;
import com.kas.mq.client.IClient;
import com.kas.mq.internal.TokenDeque;

/**
 * A QUERY QUEUE command
 * 
 * @author Pippo
 */
public class QryQueueCommand extends ACliCommand
{
  /**
   * Construct a {@link QryQueueCommand} passing the command arguments and the client object
   * that will perform actions on behalf of this command.
   * 
   * @param scanner A scanner to be used in case of further interaction is needed 
   * @param args The command arguments specified when command was entered
   * @param client The client that will perform the actual connection
   */
  protected QryQueueCommand(Scanner scanner, TokenDeque args, IClient client)
  {
    super(scanner, args, client);
  }

  /**
   * Display help screen for this command.
   * Not in use for QUERY QUEUE.
   */
  public void help()
  {
    writeln(" ");
  }
  
  /**
   * A Define command.<br>
   * <br>
   * For only the "DEFINE" verb, the command will fail with a missing queue name message.
   * For more than a single argument, the command will fail with excessive arguments message.
   * 
   * @return {@code false} always because there is no way that this command will terminate the command processor.
   */
  public boolean run()
  {
    if (mCommandArgs.size() == 0)
    {
      writeln("Missing regular expression");
      writeln(" ");
      return false;
    }
    
    String prefix = mCommandArgs.poll().toUpperCase();
    
    boolean alldata = false;
    String opt = mCommandArgs.poll();
    if ((opt != null) && (opt.equalsIgnoreCase("ALL")))
      alldata = true;
    
    if (mCommandArgs.size() > 0)
    {
      writeln("Excessive token \"" + mCommandArgs.poll() + "\"");
      writeln(" ");
      return false;
    }
    
    mClient.queryQueue(prefix, alldata);
    writeln(mClient.getResponse());
    writeln(" ");
    return false;
  }
}
