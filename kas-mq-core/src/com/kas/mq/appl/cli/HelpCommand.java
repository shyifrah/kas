package com.kas.mq.appl.cli;

import com.kas.mq.client.IClient;
import com.kas.mq.typedef.TokenDeque;

/**
 * A HELP command
 * 
 * @author Pippo
 */
public class HelpCommand extends ACliCommand
{
  static public final String cCommandVerb = "HELP";
  
  /**
   * Construct a {@link HelpCommand} passing the command arguments and the client object
   * that will perform actions on behalf of this command.
   * 
   * @param args The command arguments specified when command was entered
   * @param client The client that will perform the actual disconnection
   */
  protected HelpCommand(TokenDeque args, IClient client)
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
    }
    
    writeln("Purpose: ");
    writeln(" ");
    writeln("     Display help information regarding the KAS/MQ Admin Command Line Interface.");
    writeln(" ");
    writeln("Format: ");
    writeln(" ");
    writeln("     >>--- HELP ---+---------------+---><");
    writeln("                   |               |");
    writeln("                   +--- EXIT ------+");
    writeln("                   |               |");
    writeln("                   +--- CONNECT ---+");
    writeln(" ");
    writeln("Description: ");
    writeln(" ");
    writeln("     When invoked with no further arguments, the command will display a short help screen");
    writeln("     detailing all available arguments.");
    writeln("     When invoked with an argument, the argument is first checked to be a valid command verb");
    writeln("     and if it is, a more detailed screen will be presented, detailing the usage of the specific");
    writeln("     command.");
    writeln(" ");
    writeln("Examples:");
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
      writeln("Purpose: ");
      writeln(" ");
      writeln("     Control your KAS/MQ server by connecting to one and issue administrative commands against it.");
      writeln(" ");
      writeln("Syntax: ");
      writeln(" ");
      writeln("     >>---+--- EXIT ------+---><");
      writeln("          |               |");
      writeln("          +--- CONNECT ---+");
      writeln(" ");
      writeln("Description: ");
      writeln(" ");
      writeln("     Each command has a specific purpose. For help regarding the specific command, type:");
      writeln("          KAS/MQ Admin> HELP nnnnn");
      writeln("     Where 'nnnnn' is a command verb for which you are seeking help for.");
      writeln(" ");
      writeln("Examples: ");
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
