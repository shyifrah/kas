package com.kas.mq.appl.cli;

import com.kas.mq.client.IClient;
import com.kas.mq.typedef.TokenDeque;

/**
 * A CONNECT command
 * 
 * @author Pippo
 */
public class ConnectCommand extends ACliCommand
{
  static public final String cCommandVerb = "CONNECT";
  
  /**
   * Construct a {@link ConnectCommand} passing the command arguments.
   * 
   * @param args The command arguments specified when command was entered
   */
  protected ConnectCommand(TokenDeque args, IClient client)
  {
    super(args, client);
  }

  /**
   * Display help screen for this command.
   */
  public void help()
  {
    writeln("Purpose: ");
    writeln(" ");
    writeln("     Connect to a host - IP address or host name - on a specific port.");
    writeln(" ");
    writeln("Format: ");
    writeln(" ");
    writeln("     >>--- CONNECT ---+--- host ---+---+------------+---><");
    writeln("                      |            |   |            |");
    writeln("                      +--- ip -----+   +--- port ---+");
    writeln(" ");
    writeln("Description: ");
    writeln(" ");
    writeln("     Connect to the specified host or IP address on the specified port number, or 14560 if no port is specified");
    writeln("     When invoked with an argument, the argument is first checked to be a valid command verb");
    writeln("     and if it is, a more detailed screen will be presented, detailing the usage of the specific");
    writeln("     command.");
    writeln(" ");
    writeln("Examples:");
    writeln(" ");
    writeln("     Connect to host name TLVHOSTA on default port number:");
    writeln("          KAS/MQ Admin> CONNECT TLVHOSTA");
    writeln(" ");
    writeln("     Connect to host with IP address of 101.23.2.2 on default port number:");
    writeln("          KAS/MQ Admin> CONNECT 101.23.2.2");
    writeln(" ");
    writeln("     Connect to host name LONDON1 on port number 24560");
    writeln("          KAS/MQ Admin> CONNECT LONDON1 24560");
    writeln(" ");
  }
  
  /**
   * A connect command.<br>
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
      writeln("Connect failed. Missing host name");
      writeln(" ");
      return false;
    }
    
    String host = mCommandArgs.poll();
    String sport = mCommandArgs.poll();
    if (sport == null)
      sport = "14560";
    
    int port = Integer.valueOf(sport);
    mClient.connect(host, port);
    
    return false;
  }
}
