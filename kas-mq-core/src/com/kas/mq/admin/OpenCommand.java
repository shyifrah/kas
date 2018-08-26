package com.kas.mq.admin;

import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import com.kas.infra.base.KasException;
import com.kas.mq.client.IClient;
import com.kas.mq.internal.TokenDeque;

/**
 * A OPEN command
 * 
 * @author Pippo
 */
public class OpenCommand extends ACliCommand
{
  static public final Set<String> sCommandVerbs = new TreeSet<String>();
  static
  {
    sCommandVerbs.add("OPEN");
  }
  
  /**
   * Construct a {@link OpenCommand} passing the command arguments and the client object
   * that will perform actions on behalf of this command.
   * 
   * @param scanner A scanner to be used in case of further interaction is needed 
   * @param args The command arguments specified when command was entered
   * @param client The client that will perform the actual connection
   */
  protected OpenCommand(Scanner scanner, TokenDeque args, IClient client)
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
      writeln("Execssive command arguments are ignored for HELP OPEN");
      writeln(" ");
    }
    
    writeln("Purpose: ");
    writeln(" ");
    writeln("     Open a queue for put/get operations");
    writeln(" ");
    writeln("Format: ");
    writeln(" ");
    writeln("     >>--- OPEN ---+--- queue ---+---><");
    writeln(" ");
    writeln("Description: ");
    writeln(" ");
    writeln("     Open the specified queue for get or put operations.");
    writeln("     The opened queue will remain open until a CLOSE command is issued.");
    writeln(" ");
    writeln("Examples:");
    writeln(" ");
    writeln("     Open queue TEMP_Q_A for get or put operations:");
    writeln("          KAS/MQ Admin> OPEN TEMP_Q_A");
    writeln(" ");
  }
  
  /**
   * An open command.<br>
   * <br>
   * For only the "OPEN" verb, the command will fail with a missing queue name message.
   * For more than a single argument, the command will fail with excessive arguments message.
   * 
   * @return {@code false} always because there is no way that this command will terminate the command processor.
   */
  public boolean run()
  {
    if (mCommandArgs.size() == 0)
    {
      writeln("Open failed. Missing queue name");
      writeln(" ");
      return false;
    }
    
    String queue = mCommandArgs.poll().toUpperCase();
    String extra = mCommandArgs.poll();
    if (extra != null)
    {
      writeln("Open failed. Excessive token \"" + extra + "\"");
      writeln(" ");
      return false;
    }
    
    try
    {
      mClient.open(queue);
    }
    catch (KasException e) {}
    
    writeln(mClient.getResponse());
    writeln(" ");
    return false;
  }
}
