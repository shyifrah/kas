package com.kas.mq.admcons.commands;

import java.util.Scanner;
import com.kas.mq.client.IClient;
import com.kas.mq.internal.TokenDeque;

/**
 * A DEFINE QUEUE command
 * 
 * @author Pippo
 */
public class DefQueueCommand extends ACliCommand
{
  /**
   * Construct a {@link DefQueueCommand} passing the command arguments and the client object
   * that will perform actions on behalf of this command.
   * 
   * @param scanner A scanner to be used in case of further interaction is needed 
   * @param args The command arguments specified when command was entered
   * @param client The client that will perform the actual connection
   */
  protected DefQueueCommand(Scanner scanner, TokenDeque args, IClient client)
  {
    super(scanner, args, client);
  }

  /**
   * Display help screen for this command.
   * Not in use for DEFINE QUEUE.
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
      writeln("Missing queue name");
      writeln(" ");
      return false;
    }
    
    String queue = mCommandArgs.poll().toUpperCase();
    
    String sthreshold = mCommandArgs.poll();
    if (sthreshold == null)
      sthreshold = "1000";
    
    int threshold = -1;
    try
    {
      threshold = Integer.valueOf(sthreshold.toUpperCase());
    }
    catch (NumberFormatException e) {}
    
    if (threshold == -1)
    {
      writeln("Invalid threshold number \"" + sthreshold + "\"");
      writeln(" ");
      return false;
    }
    
    if (mCommandArgs.size() > 0)
    {
      writeln("Excessive token \"" + mCommandArgs.poll() + "\"");
      writeln(" ");
      return false;
    }
    
    mClient.defineQueue(queue, threshold);
    writeln(mClient.getResponse());
    writeln(" ");
    return false;
  }
}
