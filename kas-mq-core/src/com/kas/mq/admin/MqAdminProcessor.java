package com.kas.mq.admin;

import java.util.Scanner;
import com.kas.infra.base.AKasObject;
import com.kas.mq.client.IClient;
import com.kas.mq.client.MqClientImpl;
import com.kas.mq.internal.TokenDeque;

/**
 * MQ administration CLI processor.
 * 
 * @author Pippo
 */
public class MqAdminProcessor extends AKasObject
{
  private IClient mClientImpl = new MqClientImpl(); 
  
  /**
   * Running the processor.<br>
   * <br>
   * The main logic is quite simple - keep reading commands from STDIN until "exit" command was issued.
   */
  public void run()
  {
    writeln("KAS/MQ Admin Command Processor started");
    writeln(" ");
    
    Scanner scanner = null;
    try
    {
      scanner = new Scanner(System.in);
      TokenDeque command = read(scanner);
      boolean stop = false;
      while (!stop)
      {
        stop = process(scanner, command);
        if (!stop)
        {
          command = read(scanner);
        }
      }
    }
    catch (Throwable e)
    {
      writeln(" ");
      writeln("Exception caught: ");
      e.printStackTrace();
    }
    finally
    {
      if (scanner != null)
        scanner.close();
    }
    
    writeln(" ");
    writeln("KAS/MQ Admin Command Processor ended");
  }
  
  /**
   * Process a command represented by a queue of tokens.<br>
   * <br>
   * According to the first element in the queue - the command verb - we determine which type of Command object
   * should be created and then we execute it.
   * 
   * @param cmdWords The queue containing the command tokens
   * @param scanner The scanner, in case further interaction with the user is needed
   * @return {@code true} if an "exit" command was issued, {@code false} otherwise
   */
  private boolean process(Scanner scanner, TokenDeque cmdWords)
  {
    if (cmdWords.isEmpty() || cmdWords.peek().equals(""))
    {
      writeln(" ");
      return false;
    }
    
    String verb = cmdWords.peek();
    ICliCommand command = CliCommandFactory.newCommand(scanner, cmdWords, mClientImpl);
    if (command == null)
    {
      writeln("Unknown command verb: \"" + verb + "\". Type HELP to see available commands");
      writeln(" ");
      return false;
    }
    
    return command.run();
  }
  
  /**
   * Reading a command (one line) from STDIN and return it as a queue of tokens.
   * 
   * @param scanner The {@link Scanner} object associated with STDIN
   * 
   * @return a queue in which each element is a token from the read line
   */
  private TokenDeque read(Scanner scanner)
  {
    write("KAS/MQ Admin> ");
    String cmd = scanner.nextLine();
    return new TokenDeque(cmd);
  }
  
  /**
   * Writing a message to STDOUT.
   * 
   * @param message The message to print
   */
  private void write(String message)
  {
    System.out.print(message);
  }
  
  /**
   * Writing a message to STDOUT. The message will be followed by a Newline character.
   * 
   * @param message The message to print
   */
  private void writeln(String message)
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
    sb.append(name()).append("(\n");
    sb.append(pad).append("  ClientImpl=(").append(mClientImpl.toString()).append(")\n");
    sb.append(pad).append(")\n");
    return sb.toString();
  }
}
