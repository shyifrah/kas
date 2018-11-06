package com.kas.mq.admin.commands;

import java.util.Set;
import java.util.TreeSet;
import com.kas.infra.typedef.TokenDeque;
import com.kas.mq.impl.MqContext;

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
   * @param args The command arguments specified when command was entered
   * @param client The client that will perform the actual connection
   */
  protected DefineCommand(TokenDeque args, MqContext client)
  {
    super(args, client);
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
    
    writelnGreen("Purpose: ");
    writeln(" ");
    writeln("     Define a new entity.");
    writeln(" ");
    writelnGreen("Format: ");
    writeln(" ");
    writeln("     >>--- DEFINE|DEF ---+--- QUEUE|Q --------------+---+--- queue ---+---+-----------------+---><");
    writeln("                         |                          |                     |                 |");
    writeln("                         +--- TEMPQUEUE|TEMPQ|TQ ---+                     +--- threshold ---+");
    writeln(" ");
    writelnGreen("Description: ");
    writeln(" ");
    writeln("     Define a new entity.");
    writeln(" ");
    writeln("     -- For QUEUE --");
    writeln("     Define the specified queue.");
    writeln("     The queue will remain defined until a DELETE command is issued.");
    writeln("     Following this command, the user can start putting messages to it and getting them.");
    writeln("     The DEFINE command will fail if a queue with the specified name already exists.");
    writeln("     The threshold is the maximum capacity of the queue.");
    writeln(" ");
    writeln("     -- For TEMPQUEUE --");
    writeln("     Define the specified temporary queue.");
    writeln("     The queue will remain defined until a DELETE command is issued or the KAS/MQ server restarts.");
    writeln("     Other then that, a TEMPQUEUE is just like a regular QUEUE.");
    writeln(" ");
    writelnGreen("Examples:");
    writeln(" ");
    writeln("     Define queue TEMP_Q_A:");
    writeln("          KAS/MQ Admin> DEF QUEUE TEMP_Q_A");
    writeln(" ");
    writeln("     Define queue QUEUE_OF_HEARTS with a threshold of 50,000 messages:");
    writeln("          KAS/MQ Admin> DEF QUEUE QUEUE_OF_HEARTS 50000");
    writeln(" ");
    writeln("     Define a temporary queue TEMPQUEUE_123 with a threshold of 10,000 messages:");
    writeln("          KAS/MQ Admin> DEF TQ TEMPQUEUE_123 10000");
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
      writeln("Missing entity type");
      writeln(" ");
      return false;
    }
    
    String type = mCommandArgs.poll().toUpperCase();
    
    if (type.equals("QUEUE"))
      return new DefQueueCommand(mCommandArgs, mClient).run();
    if (type.equals("Q"))
      return new DefQueueCommand(mCommandArgs, mClient).run();
    
    if (type.equals("TEMPQUEUE"))
      return new DefTempQueueCommand(mCommandArgs, mClient).run();
    if (type.equals("TEMPQ"))
      return new DefTempQueueCommand(mCommandArgs, mClient).run();
    if (type.equals("TQ"))
      return new DefTempQueueCommand(mCommandArgs, mClient).run();
    
    
    writeln("Invalid entity type \"" + type + "\"");
    writeln(" ");
    return false;
  }
}
