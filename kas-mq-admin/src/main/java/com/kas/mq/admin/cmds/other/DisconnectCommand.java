package com.kas.mq.admin.cmds.other;

import java.util.Set;
import java.util.TreeSet;
import com.kas.infra.typedef.TokenDeque;
import com.kas.mq.admin.cmds.ACliCommand;
import com.kas.mq.internal.MqContextConnection;

/**
 * A DISCONNECT command
 * 
 * @author Pippo
 */
public class DisconnectCommand extends ACliCommand
{
  static public final Set<String> sCommandVerbs = new TreeSet<String>();
  static
  {
    sCommandVerbs.add("DISCONNECT");
    sCommandVerbs.add("DISC");
  }
  
  /**
   * Construct a {@link DisconnectCommand} passing the command arguments and the client object
   * that will perform actions on behalf of this command.
   * 
   * @param args The command arguments specified when command was entered
   * @param client The client that will perform the actual disconnection
   */
  public DisconnectCommand(TokenDeque args, MqContextConnection client)
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
      writeln("Execssive command arguments are ignored for HELP DISC");
      writeln(" ");
      return;
    }
    
    writelnGreen("Purpose: ");
    writeln(" ");
    writeln("     Disconnect current active connection.");
    writeln(" ");
    writelnGreen("Format: ");
    writeln(" ");
    writeln("     >>--- DISCONNECT|DISC ---><");
    writeln(" ");
    writelnGreen("Description: ");
    writeln(" ");
    writeln("     Disconnect current active connection. If a connection is not active, the command is simply ignored.");
    writeln(" ");
    writelnGreen("Examples:");
    writeln(" ");
    writeln("     Disconnect current connection:");
    writeln("          KAS/MQ Admin> DISCONNECT");
    writeln(" ");
  }
  
  /**
   * A disconnect command.<br>
   * <br>
   * If a connection is active, close it.<br>
   * If a connection is not active, inform there's no actual connection.
   * 
   * @return {@code false} always because there is no way that this command will terminate the command processor.
   */
  public boolean run()
  {
    if (mCommandArgs.size() > 0)
    {
      writeln("Excessive token \"" + mCommandArgs.poll() + "\"");
      writeln(" ");
      return false;
    }

    mClient.disconnect();
    
    writeln(mClient.getResponse());
    writeln(" ");
    return false;
  }
}

