package com.kas.mq.admin.cmds.other;

import java.util.Set;
import java.util.TreeSet;
import com.kas.infra.typedef.TokenDeque;
import com.kas.mq.admin.cmds.ACliCommand;
import com.kas.mq.admin.cmds.CliCommandFactory;
import com.kas.mq.admin.cmds.ICliCommand;
import com.kas.mq.impl.MqContext;

/**
 * A HELP command
 * 
 * @author Pippo
 */
public class HelpCommand extends ACliCommand
{
  static public final Set<String> sCommandVerbs = new TreeSet<String>();
  static
  {
    sCommandVerbs.add("HELP");
  }
  
  /**
   * Construct a {@link HelpCommand} passing the command arguments and the client object
   * that will perform actions on behalf of this command.
   * 
   * @param args The command arguments specified when command was entered
   * @param client The client that will perform the actual disconnection
   */
  public HelpCommand(TokenDeque args, MqContext client)
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
      writeln("Execssive command arguments are ignored for HELP HELP");
      writeln(" ");
      return;
    }
    
    writelnGreen("Purpose: ");
    writeln(" ");
    writeln("     Display help information regarding the KAS/MQ Admin Command Line Interface.");
    writeln(" ");
    writelnGreen("Format: ");
    writeln(" ");
    writeln("     >>--- HELP ---+-------------------------------+---><");
    writeln("                   |                               |");
    writeln("                   +--- EXIT|QUIT -----------------+");
    writeln("                   |                               |");
    writeln("                   +--- CONNECT|CONN --------------+");
    writeln("                   |                               |");
    writeln("                   +--- DISCONNECT|DISC -----------+");
    writeln("                   |                               |");
    writeln("                   +--- DEFINE|DEF ----------------+");
    writeln("                   |                               |");
    writeln("                   +--- DELETE|DEL ----------------+");
    writeln("                   |                               |");
    writeln("                   +--- ALTER|ALT -----------------+");
    writeln("                   |                               |");
    writeln("                   +--- QUERY|Q -------------------+");
    writeln("                   |                               |");
    writeln("                   +--- PUT -----------------------+");
    writeln("                   |                               |");
    writeln("                   +--- GET -----------------------+");
    writeln("                   |                               |");
    writeln("                   +--- TERMINATE|TERM ------------+");
    writeln(" ");
    writelnGreen("Description: ");
    writeln(" ");
    writeln("     When invoked with no further arguments, the command will display a short help screen");
    writeln("     detailing all available arguments.");
    writeln("     When invoked with an argument, the argument is first checked to be a valid command verb");
    writeln("     and if it is, a more detailed screen will be presented, detailing the usage of the specific");
    writeln("     command.");
    writeln(" ");
    writelnGreen("Examples:");
    writeln(" ");
    writeln("     Display general help screen:");
    writeln("          KAS/MQ Admin> HELP");
    writeln(" ");
    writeln("     Display help on the query command:");
    writeln("          KAS/MQ Admin> HELP QUERY");
    writeln(" ");
    writeln("     This will display an error message, as KUKU is not a valid command verb:");
    writeln("          KAS/MQ Admin> HELP KUKU");
    writeln(" ");
  }
  
  /**
   * A help command will give some information regarding the requested help.<br>
   * <br>
   * For only the "HELP" verb, the command will output all available commands and another line
   * stating that for more information you should enter the command "HELP verb", where 'verb' is the command
   * for which the user is seeking for assistance.
   * For other HELP commands, we check the argument, and if it's a valid command verb, we display the proper
   * help information.
   * 
   * @return {@code false} always because there is no way that this command will terminate the command processor.
   */
  public boolean run()
  {
    if (mCommandArgs.size() == 0)
    {
      writelnGreen("Purpose: ");
      writeln(" ");
      writeln("     Control your KAS/MQ server by connecting to one and issue administrative commands against it.");
      writeln(" ");
      writelnGreen("Syntax: ");
      writeln(" ");
      writeln("     >>---+--- EXIT|QUIT ------------+---><");
      writeln("          |                          |");
      writeln("          +--- CONNECT|CONN ---------+");
      writeln("          |                          |");
      writeln("          +--- DISCONNECT|DISC ------+");
      writeln("          |                          |");
      writeln("          +--- DEFINE|DEF -----------+");
      writeln("          |                          |");
      writeln("          +--- DELETE|DEL -----------+");
      writeln("          |                          |");
      writeln("          +--- ALTER|ALT ------------+");
      writeln("          |                          |");
      writeln("          +--- QUERY|Q --------------+");
      writeln("          |                          |");
      writeln("          +--- PUT ------------------+");
      writeln("          |                          |");
      writeln("          +--- GET ------------------+");
      writeln("          |                          |");
      writeln("          +--- TERMINATE|TERM -------+");
      writeln(" ");
      writelnGreen("Description: ");
      writeln(" ");
      writeln("     Each command has a specific purpose. For help regarding the specific command, type:");
      writeln("          KAS/MQ Admin> HELP nnnnn");
      writeln("     Where 'nnnnn' is a command verb for which you are seeking help for.");
      writeln(" ");
      writelnGreen("Examples: ");
      writeln(" ");
      writeln("     Terminate current Admin Command Line Interface:");
      writeln("          KAS/MQ Admin> EXIT");
      writeln(" ");
      writeln("     Connect to remote host on a specific port:");
      writeln("          KAS/MQ Admin> CONNECT TLVHOSTA 12344");
      writeln(" ");
    }
    else
    {
      String verb = mCommandArgs.peek();
      ICliCommand command = CliCommandFactory.newCommand(mCommandArgs, mClient);
      if (command == null)
      {
        writeln("Help requested for unknown command verb: \"" + verb + "\". Type HELP to see available commands");
        writeln(" ");
      }
      else 
      {
        command.help();
      }
    }
    
    return false;
  }
}
