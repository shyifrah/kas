package com.kas.mq.admin.commands;

import java.util.Set;
import java.util.TreeSet;
import com.kas.infra.typedef.TokenDeque;
import com.kas.mq.impl.MqContext;

/**
 * A TERMINATE command
 * 
 * @author Pippo
 */
public class TerminateCommand extends ACliCommand
{
  static public final Set<String> sCommandVerbs = new TreeSet<String>();
  static
  {
    sCommandVerbs.add("TERMINATE");
    sCommandVerbs.add("TERM");
  }
  
  /**
   * Construct a {@link TerminateCommand} passing the command arguments and the client object
   * that will perform actions on behalf of this command.
   * 
   * @param args The command arguments specified when command was entered
   * @param client The client that will perform the actual connection
   */
  protected TerminateCommand(TokenDeque args, MqContext client)
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
      writeln("Execssive command arguments are ignored for HELP TERMINATE");
      writeln(" ");
      return;
    }
    
    writelnGreen("Purpose: ");
    writeln(" ");
    writeln("     Terminate an active session or connection");
    writeln(" ");
    writelnGreen("Format: ");
    writeln(" ");
    writeln("     >>--- TERMINATE|TERM ---+--- CONNECTION|CONN ---+---+--- connection-id ---+---><");
    writeln("                             |                       |   |                     |");
    writeln("                             +--- SESSION|SESS ------+   +--- session-id ------+");
    writeln(" ");
    writelnGreen("Description: ");
    writeln(" ");
    writeln("     Terminate an active session or connection.");
    writeln("     This command is very useful for troubleshooting cases, where you suspect a connection");
    writeln("     is hung or a session is spinning out of control.");
    writeln(" ");
    writeln("     -- For CONNECTION --");
    writeln("     Terminate the active connection.");
    writeln("     Active connections can be shown via Q CONN command.");
    writeln(" ");
    writeln("     -- For SESSION --");
    writeln("     Terminate the active session.");
    writeln("     Active sessions can be shown via Q SESS command.");
    writeln(" ");
    writelnGreen("Examples:");
    writeln(" ");
    writeln("     Terminate the specified connection");
    writeln("          KAS/MQ Admin> TERM CONN a3a1fa1d-107a-4b73-bb11-7631d65cba13");
    writeln(" ");
    writeln("     Terminate the specified session");
    writeln("          KAS/MQ Admin> TERM SESS 43a1fcc3-107a-4589-3331-739019aa9ff3");
    writeln(" ");
  }
  
  /**
   * A Terminate command.<br>
   * <br>
   * For only the "TERMINATE" verb, the command will fail with a missing entity type message.
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
    
    if (type.equals("CONNECTION"))
      return new TrmConnectionCommand(mCommandArgs, mClient).run();
    if (type.equals("CONN"))
      return new TrmConnectionCommand(mCommandArgs, mClient).run();
    
    if (type.equals("SESSION"))
      return new TrmSessionCommand(mCommandArgs, mClient).run();
    if (type.equals("SESS"))
      return new TrmSessionCommand(mCommandArgs, mClient).run();
    
    
    writeln("Invalid entity type \"" + type + "\"");
    writeln(" ");
    return false;
  }
}
