package com.kas.mq.admin.cmds.alter;

import java.util.Set;
import java.util.TreeSet;
import com.kas.infra.typedef.TokenDeque;
import com.kas.mq.admin.cmds.ACliCommand;
import com.kas.mq.internal.MqContextConnection;

/**
 * AN ALTER command
 * 
 * @author Chen
 */
public class AltCommand extends ACliCommand
{
  static public final Set<String> sCommandVerbs = new TreeSet<String>();
  static
  {
    sCommandVerbs.add("ALTER");
    sCommandVerbs.add("ALT");
  }	  

  /**
   * Construct an {@link AltCommand} passing the command arguments and the client object
   * that will perform actions on behalf of this command.
   * 
   * @param args The command arguments specified when command was entered
   * @param client The client that will perform the actual connection
   */ 
  public AltCommand(TokenDeque args, MqContextConnection client)
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
      writeln("Execssive command arguments are ignored for HELP ALTER");
      writeln(" ");
      return;
    }
    
    writelnGreen("Purpose: ");
    writeln(" ");
    writeln("     ALTER entity");
    writeln(" ");
    writelnGreen("Format: ");
    writeln(" ");
    writeln("     >>--- ALTER|ALT ---+--- QUEUE|Q ---+--- queue ---+-----------------------------------+---+----------------------+---><");
    writeln("                                                      |                                   |   |                      |");    
    writeln("                                                      +--- THRESHOLD ---+--- threshold ---+   +--- PERMANENT|PERM ---+");
    writeln("                                                                                              |                      |");    
    writeln("                                                                                              +--- TEMPORARY|TEMP ---+");
    writeln(" ");
    writelnGreen("Description: ");
    writeln(" ");
    writeln("     Alter server's entity.");
    writeln("     Entity types are listed below.");
    writeln(" ");
    writeln("     -- For QUEUE --");
    writeln("     Alter queue settings based on the given property.");
    writeln("     Queue can be changed to Permanent/Temporary, and this trehshold can be altered.");
    writeln(" ");    
    writelnGreen("Examples:");
    writeln(" ");
    writeln("     Alter queue to be permanent");
    writeln("          KAS/MQ Admin> ALTER QUEUE queue1 PERMANENT");
    writeln(" ");
    writeln("     Alter queue to be temporary");
    writeln("          KAS/MQ Admin> ALTER QUEUE queue1 TEMPORARY");
    writeln(" ");
    writeln("     Alter queue threshold to the given value");
    writeln("          KAS/MQ Admin> ALTER QUEUE queue1 THRESHOLD 30");
    writeln(" ");
  }

  /**
   * An Alter command.<br>
   * <br>
   * For only the "ALTER" verb, the command will fail with a missing entity type message.
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
      return new AltQueueCommand(mCommandArgs, mClient).run();
    if (type.equals("Q"))
      return new AltQueueCommand(mCommandArgs, mClient).run();   
    
    writeln("Invalid entity type \"" + type + "\"");
    writeln(" ");
    return false;
  }
}
