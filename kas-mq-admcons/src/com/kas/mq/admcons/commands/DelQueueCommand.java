package com.kas.mq.admcons.commands;

import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import com.kas.mq.client.IClient;
import com.kas.mq.internal.TokenDeque;

/**
 * A DELETE command
 * 
 * @author Pippo
 */
public class DelQueueCommand extends ACliCommand
{
  static public final Set<String> sCommandVerbs = new TreeSet<String>();
  static
  {
    sCommandVerbs.add("DELETEQUEUE");
    sCommandVerbs.add("DELQUEUE");
    sCommandVerbs.add("DELQ");
  }
  
  /**
   * Construct a {@link DelQueueCommand} passing the command arguments and the client object
   * that will perform actions on behalf of this command.
   * 
   * @param scanner A scanner to be used in case of further interaction is needed 
   * @param args The command arguments specified when command was entered
   * @param client The client that will perform the actual connection
   */
  protected DelQueueCommand(Scanner scanner, TokenDeque args, IClient client)
  {
    super(scanner, args, client);
  }

  /**
   * Display help screen for this command.
   * Not in use for DELETE QUEUE.
   */
  public void help()
  {
    writeln(" ");
  }
  
  /**
   * A Delete command.<br>
   * <br>
   * For only the "DELETE" verb, the command will fail with a missing queue name message.
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
    
    boolean force = false;
    String opt = mCommandArgs.poll();
    if ((opt != null) && (opt.equalsIgnoreCase("FORCE")))
      force = true;
    
    if (mCommandArgs.size() > 0)
    {
      writeln("Excessive token \"" + mCommandArgs.poll() + "\"");
      writeln(" ");
      return false;
    }
    
    mClient.deleteQueue(queue, force);
    writeln(mClient.getResponse());
    writeln(" ");
    return false;
  }
}
