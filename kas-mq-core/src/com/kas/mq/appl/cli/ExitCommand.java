package com.kas.mq.appl.cli;

import com.kas.mq.typedef.TokenQueue;

/**
 * An EXIT command
 * 
 * @author Pippo
 */
public class ExitCommand extends ACliCommand
{
  static public final String cCommandVerb = "EXIT";
  
  /**
   * The command arguments
   */
  private TokenQueue mCommandArgs;
  
  /**
   * Construct an {@link ExitCommand} passing the command arguments.
   * 
   * @param args The command arguments specified when command was entered
   */
  protected ExitCommand(TokenQueue args)
  {
    mCommandArgs = args;
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
      writeln("Excessive argument specified: " + mCommandArgs.poll());
      return false;
    }
    return true;
  }
  
  /**
   * Returns a replica of this {@link ExitCommand}.
   * 
   * @return a replica of this {@link ExitCommand}
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public ExitCommand replicate()
  {
    return new ExitCommand(mCommandArgs);
  }
}
