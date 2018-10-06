package com.kas.mq.admin.commands;

import java.util.Set;
import java.util.TreeSet;
import com.kas.infra.base.KasException;
import com.kas.infra.typedef.TokenDeque;
import com.kas.mq.impl.MqContext;

/**
 * A SHUTDOWN command
 * 
 * @author Pippo
 */
public class ShutdownCommand extends ACliCommand
{
  static public final Set<String> sCommandVerbs = new TreeSet<String>();
  static
  {
    sCommandVerbs.add("SHUTDOWN");
  }
  
  /**
   * Construct a {@link ShutdownCommand} passing the command arguments and the client object
   * that will perform actions on behalf of this command.
   * 
   * @param args The command arguments specified when command was entered
   * @param client The client that will perform the actual connection
   */
  protected ShutdownCommand(TokenDeque args, MqContext client)
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
      writeln("Execssive command arguments are ignored for HELP GET");
      writeln(" ");
      return;
    }
    
    writelnGreen("Purpose: ");
    writeln(" ");
    writeln("     Shutdown the KAS/MQ server");
    writeln(" ");
    writelnGreen("Format: ");
    writeln(" ");
    writeln("     >>--- SHUTDOWN ---><");
    writeln(" ");
    writelnGreen("Description: ");
    writeln(" ");
    writeln("     Shutdown the KAS/MQ server.");
    writeln("     If the admin console is not connected to a server, the command will fail.");
    writeln("     Following the shutdown command, the connection to the KAS/MQ server is terminated.");
    writeln(" ");
    writelnGreen("Examples:");
    writeln(" ");
    writeln("     Shutdown the remote KAS/MQ server:");
    writeln("          KAS/MQ Admin> SHUTDOWN");
    writeln(" ");
  }
  
  /**
   * A shutdown command.<br>
   * <br>
   * If the command is called with arguments, the command will fail with excessive arguments message.
   * If the client is not connected to a KAS/MQ server the command will fail.
   * If the active user is not "admin", the command will also fail.
   * If the client received a response from the server that the shutdown command was accepted, the client
   * will automatically disconnect from the server to allow it to shutdown without any delays.
   * 
   * @return {@code false} always because there is no way that this command will terminate the command processor.
   */
  public boolean run()
  {
    if (mCommandArgs.size() > 0)
    {
      writeln("Shutdown failed. Excessive token \"" + mCommandArgs.peek().toUpperCase() + "\"");
      writeln(" ");
      return false;
    }
    
    boolean shutdown = mClient.shutdown();
    String resp = mClient.getResponse();
    if (shutdown)
    {
      try
      {
        mClient.disconnect();
      }
      catch (KasException e) {}
    }
    
    writeln(resp);
    writeln(" ");
    return false;
  }
}
