package com.kas.mq.admin;

import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import com.kas.mq.client.IClient;
import com.kas.mq.internal.TokenDeque;

/**
 * A DEFINE command
 * 
 * @author Pippo
 */
public class DefineCommand extends ACliCommand
{
  static public final Set<String> sCommandVerbs = new TreeSet<String>();
  static
  {
    sCommandVerbs.add("DEFINE");
    sCommandVerbs.add("DEF");
  }
  
  /**
   * Construct a {@link DefineCommand} passing the command arguments and the client object
   * that will perform actions on behalf of this command.
   * 
   * @param scanner A scanner to be used in case of further interaction is needed 
   * @param args The command arguments specified when command was entered
   * @param client The client that will perform the actual connection
   */
  protected DefineCommand(Scanner scanner, TokenDeque args, IClient client)
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
      writeln("Execssive command arguments are ignored for HELP DEFINE");
      writeln(" ");
      return;
    }
    
    writeln("Purpose: ");
    writeln(" ");
    writeln("     Define a new queue");
    writeln(" ");
    writeln("Format: ");
    writeln(" ");
    writeln("     >>--- DEFINE ---+--- queue ---+---><");
    writeln(" ");
    writeln("Description: ");
    writeln(" ");
    writeln("     Define the specified queue.");
    writeln("     The queue will remain defined until a DELETE command is issued.");
    writeln("     Following this command, the user can start putting messages to it and getting them.");
    writeln("     The DEFINE command will fail if a queue with the specified name already exists.");
    writeln(" ");
    writeln("Examples:");
    writeln(" ");
    writeln("     Define queue TEMP_Q_A:");
    writeln("          KAS/MQ Admin> DEF TEMP_Q_A");
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
      writeln("Define failed. Missing queue name");
      writeln(" ");
      return false;
    }
    
    String queue = mCommandArgs.poll().toUpperCase();
    String extra = mCommandArgs.poll();
    if (extra != null)
    {
      writeln("Define failed. Excessive token \"" + extra + "\"");
      writeln(" ");
      return false;
    }
    
    mClient.define(queue);
    writeln(mClient.getResponse());
    writeln(" ");
    return false;
  }
}
