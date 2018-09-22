package com.kas.mq.admin.commands;

import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import com.kas.infra.typedef.TokenDeque;
import com.kas.mq.impl.MqContext;

/**
 * A DELETE command
 * 
 * @author Pippo
 */
public class DeleteCommand extends ACliCommand
{
  static public final Set<String> sCommandVerbs = new TreeSet<String>();
  static
  {
    sCommandVerbs.add("DELETE");
    sCommandVerbs.add("DEL");
  }
  
  /**
   * Construct a {@link DeleteCommand} passing the command arguments and the client object
   * that will perform actions on behalf of this command.
   * 
   * @param scanner A scanner to be used in case of further interaction is needed 
   * @param args The command arguments specified when command was entered
   * @param client The client that will perform the actual connection
   */
  protected DeleteCommand(Scanner scanner, TokenDeque args, MqContext client)
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
    writeln("     >>--- DELETE ---+--- queue ---+---+-------------+---><");
    writeln("                                       |             |");
    writeln("                                       +--- FORCE ---+");
    writeln(" ");
    writeln("Description: ");
    writeln(" ");
    writeln("     Delete the specified queue.");
    writeln("     Once a queue is deleted, you cannot undo this operation. All the contents of the queue");
    writeln("     is permanentely erased, including the backup file.");
    writeln("     The DELETE command will fail if a queue with the specified name does not exist or");
    writeln("     If the queue is not empty. To ignore the queue contents and delete the queue anyway,");
    writeln("     The user must use the FORCE keyword.");
    writeln(" ");
    writeln("Examples:");
    writeln(" ");
    writeln("     Delete queue TEMP_Q_2DEL:");
    writeln("          KAS/MQ Admin> DELETE TEMP_Q_2DEL");
    writeln(" ");
    writeln("     Delete queue TEMP_Q_2DEL and ignore its contents:");
    writeln("          KAS/MQ Admin> DELETE TEMP_Q_2DEL FORCE");
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
      writeln("Missing entity type");
      writeln(" ");
      return false;
    }
    
    String type = mCommandArgs.poll().toUpperCase();
    
    if (type.equals("QUEUE"))
      return new DelQueueCommand(mScanner, mCommandArgs, mClient).run();
    if (type.equals("Q"))
      return new DelQueueCommand(mScanner, mCommandArgs, mClient).run();
    
    
    writeln("Invalid entity type \"" + type + "\"");
    writeln(" ");
    return false;
  }
}
