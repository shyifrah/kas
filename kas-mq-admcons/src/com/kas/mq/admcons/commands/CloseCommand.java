package com.kas.mq.admcons.commands;

import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import com.kas.infra.base.KasException;
import com.kas.mq.client.IClient;
import com.kas.mq.internal.TokenDeque;

/**
 * A CLOSE command
 * 
 * @author Pippo
 */
public class CloseCommand extends ACliCommand
{
  static public final Set<String> sCommandVerbs = new TreeSet<String>();
  static
  {
    sCommandVerbs.add("CLOSE");
  }
  
  /**
   * Construct a {@link CloseCommand} passing the command arguments and the client object
   * that will perform actions on behalf of this command.
   * 
   * @param scanner A scanner to be used in case of further interaction is needed 
   * @param args The command arguments specified when command was entered
   * @param client The client that will perform the actual connection
   */
  protected CloseCommand(Scanner scanner, TokenDeque args, IClient client)
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
      writeln("Execssive command arguments are ignored for HELP CLOSE");
      writeln(" ");
      return;
    }
    
    writeln("Purpose: ");
    writeln(" ");
    writeln("     Close previously opened queue");
    writeln(" ");
    writeln("Format: ");
    writeln(" ");
    writeln("     >>--- CLOSE ---><");
    writeln(" ");
    writeln("Description: ");
    writeln(" ");
    writeln("     Close a queue the was previously opened via the OPEN command.");
    writeln("     Following a close command, all PUT or GET operations will fail.");
    writeln(" ");
    writeln("Examples:");
    writeln(" ");
    writeln("     Close previously opened queue:");
    writeln("          KAS/MQ Admin> CLOSE");
    writeln(" ");
  }
  
  /**
   * A close command.<br>
   * <br>
   * For more than a single argument, the command will fail with excessive arguments message.
   * For only the command verb, a previously opened queue will be closed for all GET/PUT operations
   * until a queue is opened again.
   * If no queue was previously opened, the command will fail with a message stating there's no
   * open queue.
   * 
   * @return {@code false} always because there is no way that this command will terminate the command processor.
   */
  public boolean run()
  {
    if (mCommandArgs.size() > 0)
    {
      writeln("Close failed. Excessive token \"" + mCommandArgs.peek().toUpperCase() + "\"");
      writeln(" ");
      return false;
    }
    
    try
    {
      mClient.close();
    }
    catch (KasException e) {}
    
    writeln(mClient.getResponse());
    writeln(" ");
    return false;
  }
}
