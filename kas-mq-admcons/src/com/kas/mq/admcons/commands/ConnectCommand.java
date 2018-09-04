package com.kas.mq.admcons.commands;

import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import com.kas.infra.base.KasException;
import com.kas.infra.utils.Validators;
import com.kas.mq.client.IClient;
import com.kas.mq.internal.TokenDeque;

/**
 * A CONNECT command
 * 
 * @author Pippo
 */
public class ConnectCommand extends ACliCommand
{
  static public final Set<String> sCommandVerbs = new TreeSet<String>();
  static
  {
    sCommandVerbs.add("CONNECT");
    sCommandVerbs.add("CONN");
  }
  
  /**
   * Construct a {@link ConnectCommand} passing the command arguments and the client object
   * that will perform actions on behalf of this command.
   * 
   * @param scanner A scanner to be used in case of further interaction is needed 
   * @param args The command arguments specified when command was entered
   * @param client The client that will perform the actual connection
   */
  protected ConnectCommand(Scanner scanner, TokenDeque args, IClient client)
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
      writeln("Execssive command arguments are ignored for HELP CONN");
      writeln(" ");
      return;
    }
    
    writeln("Purpose: ");
    writeln(" ");
    writeln("     Connect to a host - IP address or host name - on a specific port.");
    writeln(" ");
    writeln("Format: ");
    writeln(" ");
    writeln("     >>--- CONNECT|CONN ---+--- host ---+---+------------+---><");
    writeln("                           |            |   |            |");
    writeln("                           +--- ip -----+   +--- port ---+");
    writeln(" ");
    writeln("Description: ");
    writeln(" ");
    writeln("     Connect to the specified host or IP address on the specified port number, or 14560 if no port is specified.");
    writeln("     The host or the IP address are NOT checked for a valid format. However, the command processor does verify");
    writeln("     that the port number has a valid numeric value.");
    writeln("     Once the host and port were validated, the user will be propmpted for its user name and password.");
    writeln(" ");
    writeln("Examples:");
    writeln(" ");
    writeln("     Connect to host name TLVHOSTA on default port number:");
    writeln("          KAS/MQ Admin> CONNECT TLVHOSTA");
    writeln(" ");
    writeln("     Connect to host with IP address of 101.23.2.2 on default port number:");
    writeln("          KAS/MQ Admin> CONN 101.23.2.2");
    writeln(" ");
    writeln("     Connect to host name LONDON1 on port number 24560");
    writeln("          KAS/MQ Admin> CONNECT LONDON1 24560");
    writeln(" ");
  }
  
  /**
   * A connect command.<br>
   * <br>
   * Read the host name and the port number, then input the username and password.
   * Pass all this information to the client and try to connect.
   * 
   * @return {@code false} always because there is no way that this command will terminate the command processor.
   */
  public boolean run()
  {
    if (mCommandArgs.size() == 0)
    {
      writeln("Missing host name");
      writeln(" ");
      return false;
    }
    
    String host = mCommandArgs.poll().toUpperCase();
    if (!(Validators.isHostName(host)) && (!Validators.isIpAddress(host)))
    {
      writeln("Host \"" + host + "\" does not designate a host name nor IP address");
      writeln(" ");
      return false;
    }
    
    String sport = mCommandArgs.poll();
    if (sport == null)
      sport = "14560";
    
    int port = -1;
    try
    {
      port = Integer.valueOf(sport.toUpperCase());
    }
    catch (NumberFormatException e) {}
    
    if (!Validators.isPort(port))
    {
      writeln("Invalid port number \"" + sport + "\"");
      writeln(" ");
      return false;
    }
    
    if (mCommandArgs.size() > 0)
    {
      writeln("Excessive token \"" + mCommandArgs.poll() + "\"");
      writeln(" ");
      return false;
    }
    
    TokenDeque input = read("Enter user name: ");
    String username = input.poll();
    String extra = input.poll();
    if ((username == null) || (username.length() == 0))
    {
      writeln("User name cannot be empty string");
      writeln(" ");
      return false;
    }
    username = username.trim().toUpperCase();
    if (!Validators.isUserName(username))
    {
      writeln("Invalid user name \"" + username + "\"");
      writeln(" ");
      return false;
    }
    
    if (extra != null)
    {
      writeln("Invalid user name \"" + username + ' ' + extra + "\"");
      writeln(" ");
      return false;
    }
    
    input = read("Enter password: ");
    String password = input.getOriginalString();
    
    
    try
    {
      mClient.connect(host, port, username, password);
    }
    catch (KasException e) {}
    
    writeln(mClient.getResponse());
    writeln(" ");
    return false;
  }
}
