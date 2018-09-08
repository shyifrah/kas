package com.kas.mq.admcons.commands;

import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import com.kas.infra.typedef.TokenDeque;
import com.kas.infra.utils.Validators;
import com.kas.mq.impl.IMqConstants;
import com.kas.mq.impl.internal.IClient;

/**
 * A GET command
 * 
 * @author Pippo
 */
public class GetCommand extends ACliCommand
{
  static public final Set<String> sCommandVerbs = new TreeSet<String>();
  static
  {
    sCommandVerbs.add("GET");
  }
  
  /**
   * Construct a {@link GetCommand} passing the command arguments and the client object
   * that will perform actions on behalf of this command.
   * 
   * @param scanner A scanner to be used in case of further interaction is needed 
   * @param args The command arguments specified when command was entered
   * @param client The client that will perform the actual connection
   */
  protected GetCommand(Scanner scanner, TokenDeque args, IClient client)
  {
    super(scanner, args, client);
  }

  /**
   * Display help screen for this command.
   */
  public void help()
  {
    if (mCommandArgs.size() > 0)
    {
      writeln("Execssive command arguments are ignored for HELP GET");
      writeln(" ");
      return;
    }
    
    writeln("Purpose: ");
    writeln(" ");
    writeln("     Get a message from queue");
    writeln(" ");
    writeln("Format: ");
    writeln(" ");
    writeln("     >>--- GET ---+--- queue ---+---><");
    writeln(" ");
    writeln("Description: ");
    writeln(" ");
    writeln("     Get a message from the specified queue.");
    writeln("     If no message is available, the command will block until one is put to the queue.");
    writeln(" ");
    writeln("Examples:");
    writeln(" ");
    writeln("     Get a message from queue TEMP1Q:");
    writeln("          KAS/MQ Admin> GET TEMP1Q");
    writeln(" ");
  }
  
  /**
   * A get command.<br>
   * <br>
   * First token is the queue name.
   * If any excessive tokens follow the queue name, the command will fail.
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
    if (!Validators.isQueueName(queue))
    {
      writeln("Invalid queue name \"" + queue + "\"");
      writeln(" ");
      return false;
    }
    
    if (mCommandArgs.size() > 0)
    {
      writeln("Excessive token \"" + mCommandArgs.poll() + "\"");
      writeln(" ");
      return false;
    }
    
    mClient.get(queue, IMqConstants.cDefaultTimeout, IMqConstants.cDefaultPollingInterval);
    writeln(mClient.getResponse());
    writeln(" ");
    return false;
  }
}
