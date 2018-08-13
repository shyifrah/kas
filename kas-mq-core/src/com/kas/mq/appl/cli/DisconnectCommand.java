package com.kas.mq.appl.cli;

import com.kas.mq.client.IClient;
import com.kas.mq.typedef.TokenDeque;

/**
 * A DISCONNECT command
 * 
 * @author Pippo
 */
public class DisconnectCommand extends ACliCommand
{
  static public final String cCommandVerb = "DISCONNECT";
  
  /**
   * Construct a {@link DisconnectCommand} passing the command arguments and the client object
   * that will perform actions on behalf of this command.
   * 
   * @param args The command arguments specified when command was entered
   * @param client The client that will perform the actual disconnection
   */
  protected DisconnectCommand(TokenDeque args, IClient client)
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
    writeln("     Disconnect current active connection.");
    writeln(" ");
    writeln("Format: ");
    writeln(" ");
    writeln("     >>--- DISCONNECT ---><");
    writeln(" ");
    writeln("Description: ");
    writeln(" ");
    writeln("     Disconnect current active connection. If a connection is not active, just ignore the command.");
    writeln(" ");
    writeln("Examples:");
    writeln(" ");
    writeln("     Disconnect current connection:");
    writeln("          KAS/MQ Admin> DISCONNECT");
    writeln(" ");
  }
  
  /**
   * A connect command.<br>
   * <br>
   * If a connection is not active, display a message showing no actual connection.<br>
   * If a connection is active, close it.
   * 
   * @return {@code false} always because there is no way that this command will terminate the command processor.
   */
  public boolean run()
  {
    if (mCommandArgs.size() > 0)
    {
      mClient.setResponse("Excessive argument specified: \"" + mCommandArgs.poll() + "\". Type HELP DISCONNECT to see available command options");
      return false;
    }
    
    String response;
    if (!mClient.isConnected())
    {
      mClient.setResponse("Not connected");
    }
    else
    {
      mClient.disconnect();
      response = 
      mClient.setResponse("Excessive argument specified: \"" + mCommandArgs.poll() + "\". Type HELP DISCONNECT to see available command options");
    }
    
    return false;
    
    String host = mCommandArgs.poll();
    String sport = mCommandArgs.poll();
    if (sport == null)
      sport = "14560";
    
    int port = -1;
    try
    {
      port = Integer.valueOf(sport);
    }
    catch (NumberFormatException e) {}
    if (port == -1)
    {
      mClient.setResponse("Connect failed. Invalid port number \"" + sport + "\"");
      return false;
    }
    
    mClient.connect(host, port);
    if (mClient.isConnected())
    {
      mClient.setResponse("Successfully connected to host at " + host + ':' + port);
    }
    else
    {
      mClient.setResponse("Failed to connect to " + host + ':' + port + ". See log file for further information");
    }
    
    return false;
  }
}
