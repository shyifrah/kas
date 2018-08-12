package com.kas.mq.appl.cli;

import com.kas.infra.base.AKasObject;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.client.IClient;
import com.kas.mq.typedef.TokenDeque;

/**
 * A basic command
 * 
 * @author Pippo
 */
public abstract class ACliCommand extends AKasObject implements ICliCommand
{
  /**
   * The command arguments
   */
  protected TokenDeque mCommandArgs;
  
  /**
   * The client program that will actually execute the commands
   */
  protected IClient mClient;
  
  /**
   * Construct a {@link ACliCommand} specifying the command arguments and the client
   * 
   * @param cmdWords The command arguments
   * @param client The client
   */
  ACliCommand(TokenDeque args, IClient client)
  {
    mCommandArgs = args;
    mClient = client;
  }
  
  /**
   * Processing the command.
   * 
   * @return {@code true} upon successful processing and {@code false} otherwise for "Exit" command.
   * The other way around for all other commands.
   */
  public abstract boolean run();
  
  /**
   * Writing a message to STDOUT.
   * 
   * @param message The message to print
   */
  protected void write(String message)
  {
    System.out.print(message);
  }
  
  /**
   * Writing a message to STDOUT. The message will be followed by a Newline character.
   * 
   * @param message The message to print
   */
  protected void writeln(String message)
  {
    System.out.println(message);
  }
  
  /**
   * Get the object's detailed string representation
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n")
      .append(pad).append("  Client=").append(mClient).append("\n")
      .append(pad).append("  Arguments=(").append(StringUtils.asPrintableString(mCommandArgs, level+1)).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
