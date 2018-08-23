package com.kas.mq.appl.cli;

import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import com.kas.mq.client.IClient;
import com.kas.mq.internal.TokenDeque;

/**
 * A LOGIN command
 * 
 * @author Pippo
 */
public class LoginCommand extends ACliCommand
{
  static public final Set<String> sCommandVerbs = new TreeSet<String>();
  static
  {
    sCommandVerbs.add("LOGIN");
    sCommandVerbs.add("AUTHENTICATE");
    sCommandVerbs.add("AUTH");
  }
  
  /**
   * Construct a {@link LoginCommand} passing the command arguments and the client object
   * that will perform actions on behalf of this command.
   * 
   * @param scanner A scanner to be used in case of further interaction is needed 
   * @param args The command arguments specified when command was entered
   * @param client The client that will perform the actual connection
   */
  protected LoginCommand(Scanner scanner, TokenDeque args, IClient client)
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
    }
    
    writeln("Purpose: ");
    writeln(" ");
    writeln("     Logout the previously entered credentials (via CONNECT or previous LOGIN commands)");
    writeln("     and authenticate as a new user.");        
    writeln(" ");
    writeln("Format: ");
    writeln(" ");
    writeln("     >>--- LOGIN|AUTHENTICATE|AUTH ---><");
    writeln(" ");
    writeln("Description: ");
    writeln(" ");
    writeln("     The command prompts the user for it's name and password and then authenticates it against");
    writeln("     the connected KAS/MQ server.");
    writeln("     If a user's session was already authenticated prior to this command (via primary authentication of"); 
    writeln("     the CONNECT command, or a subsequent LOGIN command), it is logged off.");
    writeln(" ");
    writeln("Examples:");
    writeln(" ");
    writeln("     Login to the KAS/MQ server:");
    writeln("          KAS/MQ Admin> LOGIN");
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
    if (mCommandArgs.size() > 0)
    {
      writeln("Login failed. Excessive token \"" + mCommandArgs.peek().toUpperCase() + "\"");
      writeln(" ");
      return false;
    }

    if (!mClient.isConnected())
    {
      writeln("Not connected");
      writeln(" ");
      return false;
    }
    
    TokenDeque input = read("Enter user name: ");
    String username = input.poll().trim();
    String extra = input.poll();
    if ((username == null) || (username.length() == 0))
    {
      writeln("Invalid user name \"\". Cannot be empty string");
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
    
    mClient.login(username, password);
    writeln(mClient.getResponse());
    writeln(" ");
    return false;
  }
}
