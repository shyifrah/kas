package com.kas.mq.admcons.commands;

import java.util.Scanner;
import com.kas.infra.base.AKasObject;
import com.kas.infra.types.TokenDeque;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.client.IClient;

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
   * A scanner in order to read user's input
   */
  protected Scanner mScanner;
  
  /**
   * Construct a {@link ACliCommand} specifying the command arguments and the client
   * 
   * @param cmdWords The command arguments
   * @param client The client
   */
  ACliCommand(Scanner scanner, TokenDeque args, IClient client)
  {
    mScanner = scanner;
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
   * Reading a command (one line) from STDIN and return it as a queue of tokens.
   * 
   * @return a queue in which each element is a token from the read line
   */
  protected TokenDeque read(String message)
  {
    write(message);
    String input = mScanner.nextLine();
    return new TokenDeque(input);
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
