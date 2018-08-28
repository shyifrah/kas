package com.kas.mq.admcons.commands;

import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import com.kas.mq.client.IClient;
import com.kas.mq.impl.IMqConstants;
import com.kas.mq.internal.TokenDeque;

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
    }
    
    writeln("Purpose: ");
    writeln(" ");
    writeln("     Get a message from a previously opened queue");
    writeln(" ");
    writeln("Format: ");
    writeln(" ");
    writeln("     >>--- GET ---><");
    writeln(" ");
    writeln("Description: ");
    writeln(" ");
    writeln("     Get a message from a queue the was previously opened via the OPEN command.");
    writeln(" ");
    writeln("Examples:");
    writeln(" ");
    writeln("     Get a message from the previously opened queue:");
    writeln("          KAS/MQ Admin> GET");
    writeln(" ");
  }
  
  /**
   * A get command.<br>
   * <br>
   * If the command is called with arguments, the command will fail with excessive arguments message.
   * For only the command verb, the command will access the latest opened queue and try to retrieve
   * a message from that queue.
   * If no queue was previously opened, the command will fail with a message stating there's no
   * open queue.
   * 
   * @return {@code false} always because there is no way that this command will terminate the command processor.
   */
  public boolean run()
  {
    if (mCommandArgs.size() > 0)
    {
      writeln("Get failed. Excessive token \"" + mCommandArgs.peek().toUpperCase() + "\"");
      writeln(" ");
      return false;
    }
    
    mClient.get(IMqConstants.cDefaultTimeout, IMqConstants.cDefaultPollingInterval);
    writeln(mClient.getResponse());
    writeln(" ");
    return false;
  }
}
