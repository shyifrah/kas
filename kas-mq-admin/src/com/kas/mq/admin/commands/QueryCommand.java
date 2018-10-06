package com.kas.mq.admin.commands;

import java.util.Set;
import java.util.TreeSet;
import com.kas.infra.typedef.TokenDeque;
import com.kas.mq.impl.MqContext;

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
   * @param args The command arguments specified when command was entered
   * @param client The client that will perform the actual connection
   */
  protected QueryCommand(TokenDeque args, MqContext client)
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
    writeln("     >>--- QUERY|Q ---+--- QUEUE|Q ---+---+--- prefix ---+---+-------------+--------+---><");
    writeln("                      |                                      |             |        |");
    writeln("                      |                                      +--- ALL -----+        |");
    writeln("                      |                                                             |");
    writeln("                      |                                      +--- ALL ----------+   |");
    writeln("                      |                                      |                  |   |");
    writeln("                      +--- SESSION|SESS ---------------------+------------------+---+");
    writeln("                      |                                      |                  |   |");
    writeln("                      |                                      +--- session-id ---+   |");
    writeln("                      |                                                             |");
    writeln("                      |                                                             |");
    writeln("                      |                                      +--- ALL ----------+   |");
    writeln("                      |                                      |                  |   |");
    writeln("                      +--- CONNECTION|CONN ------------------+------------------+---+");
    writeln("                      |                                      |                  |   |");
    writeln("                      |                                      +--- conn-id ------+   |");
    writeln("                      |                                                             |");
    writeln("                      |                                      +--- ALL ----------+   |");
    writeln("                      |                                      |                  |   |");
    writeln("                      +--- CONFIGURATION|CONFIG|CONF|CFG-----+------------------+---+");
    writeln("                                                             |                  |");
    writeln("                                                             +--- MQ -----------+");
    writeln("                                                             |                  |");
    writeln("                                                             +--- LOGGING ------+");
    writeln("                                                             |                  |");
    writeln("                                                             +--- SERIALIZER ---+");
    writeln(" ");
    writeln("Description: ");
    writeln(" ");
    writeln("     Query entity.");
    writeln(" ");
    writeln("     -- For QUEUE --");
    writeln("     Provide information about all queues matching the prefix (the default).");
    writeln("     If the keyword ALL is used, a more extensive information is shown.");
    writeln(" ");
    writeln("     -- For SESSION --");
    writeln("     Provide information about all or a specific session.");
    writeln(" ");
    writeln("     -- For CONNECTION --");
    writeln("     Provide information about all or a specific connection.");
    writeln(" ");
    writeln("     -- For CONFIGURATION --");
    writeln("     Provide the server's active configuration.");
    writeln(" ");
    writeln("Examples:");
    writeln(" ");
    writeln("     Query basic information about all queues matching regexp \\CLIENT.APP.*\\:");
    writeln("          KAS/MQ Admin> QUERY QUEUE CLIENT.APP.*");
    writeln(" ");
    writeln("     Query all information about the contents of queue QUEUE1");
    writeln("          KAS/MQ Admin> Q Q QUEUE1 ALL");
    writeln(" ");
    writeln("     Query information about all active sessions");
    writeln("          KAS/MQ Admin> Q SESS ALL");
    writeln(" ");
    writeln("     Query information about a specific connection");
    writeln("          KAS/MQ Admin> Q CONN a3a1fa1d-107a-4b73-bb11-7631d65cba13");
    writeln(" ");
    writeln("     Query entire server's configuration");
    writeln("          KAS/MQ Admin> Q CONFIG");
    writeln(" ");
  }
  
  /**
   * A Query command.<br>
   * <br>
   * For only the "QUERY" verb, the command will fail with a missing entity type message.
   * The rest of the arguments are passed to the sub-commands.
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
      return new QryQueueCommand(mCommandArgs, mClient).run();
    if (type.equals("Q"))
      return new QryQueueCommand(mCommandArgs, mClient).run();
    
    if (type.equals("SESSION"))
      return new QrySessionCommand(mCommandArgs, mClient).run();
    if (type.equals("SESS"))
      return new QrySessionCommand(mCommandArgs, mClient).run();
    
    if (type.equals("CONNECTION"))
      return new QryConnectionCommand(mCommandArgs, mClient).run();
    if (type.equals("CONN"))
      return new QryConnectionCommand(mCommandArgs, mClient).run();
    
    if (type.equals("CONFIGURATION"))
      return new QryConfigCommand(mCommandArgs, mClient).run();
    if (type.equals("CONFIG"))
      return new QryConfigCommand(mCommandArgs, mClient).run();
    if (type.equals("CONF"))
      return new QryConfigCommand(mCommandArgs, mClient).run();
    if (type.equals("CFG"))
      return new QryConfigCommand(mCommandArgs, mClient).run();
    
    
    writeln("Invalid entity type \"" + type + "\"");
    writeln(" ");
    return false;
  }
}
