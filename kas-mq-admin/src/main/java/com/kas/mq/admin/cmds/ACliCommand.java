package com.kas.mq.admin.cmds;

import com.kas.infra.base.AKasObject;
import com.kas.infra.typedef.TokenDeque;
import com.kas.infra.utils.ConsoleUtils;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.internal.MqContextConnection;

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
  protected MqContextConnection mClient;
  
  /**
   * Construct a {@link ACliCommand} specifying the command arguments and the client
   * 
   * @param cmdWords The command arguments
   * @param client The client
   */
  protected ACliCommand(TokenDeque args, MqContextConnection client)
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
   * Writing a message to STDOUT. The message will be followed by a Newline character.
   * 
   * @param message The message to print
   */
  protected void writeln(String message)
  {
    System.out.println(message);
  }
  
  /**
   * Writing a message to STDOUT highlighted in GREEN. The message will be followed by a Newline character.
   * 
   * @param message The message to print
   */
  protected void writelnGreen(String message)
  {
    System.out.println(ConsoleUtils.GREEN + message + ConsoleUtils.RESET);
  }
  
  /**
   * Reading a command (a single line) from STDIN and return it as a queue of tokens.<br>
   * The read command is clear-text.
   * 
   * @param prompt The prompt message to print to the user
   * @return a {@link TokenDeque} containing command's tokens 
   */
  protected TokenDeque readClear(String prompt)
  {
    String input = ConsoleUtils.readClearText(prompt);
    return new TokenDeque(input);
  }
  
  /**
   * Reading a command (a single line) from STDIN and return it as a queue of tokens.<br>
   * The read command is masked text.
   * 
   * @param prompt The prompt message to print to the user
   * @return a {@link TokenDeque} containing command's tokens 
   */
  protected TokenDeque readMasked(String prompt)
  {
    String input = ConsoleUtils.readMaskedText(prompt);
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
      .append(pad).append("  Connection=").append(mClient).append("\n")
      .append(pad).append("  Arguments=(").append(StringUtils.asPrintableString(mCommandArgs, level+1)).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
