package com.kas.mq.appl.cli;

import java.util.Set;
import java.util.TreeSet;
import com.kas.mq.client.IClient;
import com.kas.mq.internal.TokenDeque;

/**
 * An EXIT command
 * 
 * @author Pippo
 */
public class ExitCommand extends ACliCommand
{
  static public final Set<String> sCommandVerbs = new TreeSet<String>();
  static
  {
    sCommandVerbs.add("EXIT");
    sCommandVerbs.add("TERM");
    sCommandVerbs.add("QUIT");
  }
  
  /**
   * Construct an {@link ExitCommand} passing the command arguments and the client object
   * that will perform actions on behalf of this command.
   * 
   * @param args The command arguments specified when command was entered
   * @param client The client that will perform the actual disconnection
   */
  protected ExitCommand(TokenDeque args, IClient client)
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
      writeln("Execssive command arguments are ignored for HELP EXIT");
      writeln(" ");
    }
    
    writeln("Purpose: ");
    writeln(" ");
    writeln("     Terminate KAS/MQ Admin Command Line Interface.");
    writeln(" ");
    writeln("Format: ");
    writeln(" ");
    writeln("     >>--- EXIT|TERM|QUIT ---><");
    writeln(" ");
    writeln("Description: ");
    writeln(" ");
    writeln("     Terminate KAS/MQ Admin Command Line Interface.");
    writeln(" ");
    writeln("Examples:");
    writeln(" ");
    writeln("     Terminates the current KAS/MQ Admin session:");
    writeln("          KAS/MQ Admin> EXIT");
    writeln(" ");
    writeln("     This will display an error message, as EXIT command does not expect any arguments:");
    writeln("          KAS/MQ Admin> EXIT KUKU");
    writeln(" ");
  }
  
  /**
   * A stop command will return {@code true} to order the {@link MqCommandProcessor processor} to stop further processing.<br>
   * The command does not accept any arguments, so if a user specify arguments to the STOP command, 
   * its execution will fail and the command processor won't terminate.
   * 
   * @return {@code true} upon successful processing, {@code false} otherwise
   */
  public boolean run()
  {
    if (mCommandArgs.size() > 0)
    {
      mClient.setResponse("Excessive argument specified: \"" + mCommandArgs.poll() + "\". Type HELP EXIT to see available command options");
      return false;
    }
    return true;
  }
}
