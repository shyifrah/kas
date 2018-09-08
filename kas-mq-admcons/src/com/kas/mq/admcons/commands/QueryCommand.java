package com.kas.mq.admcons.commands;

import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import com.kas.infra.typedef.TokenDeque;
import com.kas.mq.client.IClient;

/**
 * A QUERY command
 * 
 * @author Pippo
 */
public class QueryCommand extends ACliCommand
{
  static public final Set<String> sCommandVerbs = new TreeSet<String>();
  static
  {
    sCommandVerbs.add("QUERY");
    sCommandVerbs.add("Q");
  }
  
  /**
   * Construct a {@link QueryCommand} passing the command arguments and the client object
   * that will perform actions on behalf of this command.
   * 
   * @param scanner A scanner to be used in case of further interaction is needed 
   * @param args The command arguments specified when command was entered
   * @param client The client that will perform the actual connection
   */
  protected QueryCommand(Scanner scanner, TokenDeque args, IClient client)
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
      writeln("Execssive command arguments are ignored for HELP QUERY");
      writeln(" ");
      return;
    }
    
    writeln("Purpose: ");
    writeln(" ");
    writeln("     Query entity");
    writeln(" ");
    writeln("Format: ");
    writeln(" ");
    writeln("                                                             +--- BASIC ---+");
    writeln("                                                             |             |");
    writeln("     >>--- QUERY|Q ---+--- QUEUE|Q ---+---+--- prefix ---+---+-------------+---><");
    writeln("                                                             |             |");
    writeln("                                                             +--- ALL -----+");
    writeln(" ");
    writeln("Description: ");
    writeln(" ");
    writeln("     Query entity.");
    writeln(" ");
    writeln("     -- For QUEUE --");
    writeln("     Provide basic information about all queues matching the prefix (the default).");
    writeln("     If the keyword ALL is used following the prefix, all messages in that queue are displayed as well.");
    writeln(" ");
    writeln("Examples:");
    writeln(" ");
    writeln("     Query basic information about all queues matching regexp \\CLIENT.APP.*\\:");
    writeln("          KAS/MQ Admin> QUERY QUEUE CLIENT.APP.*");
    writeln(" ");
    writeln("     Query all information about the contents of queue QUEUE1");
    writeln("          KAS/MQ Admin> Q Q QUEUE1 ALL");
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
      return new QryQueueCommand(mScanner, mCommandArgs, mClient).run();
    if (type.equals("Q"))
      return new QryQueueCommand(mScanner, mCommandArgs, mClient).run();
    
    
    writeln("Invalid entity type \"" + type + "\"");
    writeln(" ");
    return false;
  }
}
