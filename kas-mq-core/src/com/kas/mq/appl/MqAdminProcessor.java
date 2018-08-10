package com.kas.mq.appl;

import java.util.ArrayDeque;
import java.util.Scanner;
import com.kas.infra.base.AKasObject;
import com.kas.mq.appl.cli.ACliCommand;
import com.kas.mq.appl.cli.CliCommandFactory;
import com.kas.mq.appl.cli.ICliCommand;
import com.kas.mq.typedef.TokenQueue;

/**
 * MQ administration CLI processor.
 * 
 * @author Pippo
 */
public class MqAdminProcessor extends AKasObject 
{
  /**
   * Running the processor.<br>
   * <br>
   * The main logic is quite simple - keep reading commands from STDIN until "stop" command
   * was issued. 
   * 
   * @param args arguments passed to {@code main} function
   */
  public void run()
  {
    writeln("KAS/Q Admin Command Processor started");
    writeln(" ");
    
    Scanner scanner = null;
    try
    {
      scanner = new Scanner(System.in);
      TokenQueue command = read(scanner);
      boolean stop = false;
      while (!stop)
      {
        stop = process(command);
        if (!stop)
        {
          command = read(scanner);
        }
      }
    }
    finally
    {
      if (scanner != null)
        scanner.close();
    }
    
    writeln(" ");
    writeln("KAS/Q Admin Command Processor ended");
  }
  
  /**
   * Process a command represented by a queue of tokens.<br>
   * <br>
   * According to the first element in the queue - the command verb - we determine which type of Command object
   * should be created and then we execute it.
   * 
   * @param cmdWords The queue containing the command tokens
   * @return {@code true} if an "exit" command was issued, {@code false} otherwise
   */
  private boolean process(TokenQueue cmdWords)
  {
    boolean stop = false;
    if (cmdWords.isEmpty() || cmdWords.peek().equals(""))
    {
      writeln(" ");
      return false;
    }
    
    String verb = cmdWords.poll();
    ICliCommand command = CliCommandFactory.newCommand(cmdWords);
    
    return command.run();
  }
  
  /**
   * Reading a command (one line) from STDIN and return it as a queue of tokens.
   * 
   * @param scanner The {@link Scanner} object associated with STDIN
   * 
   * @return a queue in which each element is a token from the read line
   */
  private TokenQueue read(Scanner scanner)
  {
    write("KAS/MQ Admin> ");
    String cmd = scanner.nextLine();
    String [] a = cmd.split(" ");
    TokenQueue q = new TokenQueue();
    for (String word : a)
      q.offer(word);
    return q;
  }
  
  /**
   * Writing a message to STDOUT.
   * 
   * @param message The message to print
   */
  private void write(String message)
  {
    System.out.println(message);
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
   * Returns a replica of this {@link MqAdminProcessor}.
   * 
   * @return a replica of this {@link MqAdminProcessor}
   * 
   * @throws RuntimeException always. Cannot replicate class
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public MqAdminProcessor replicate()
  {
    throw new RuntimeException("Cannot replicate objects of type " + this.getClass().getSimpleName());
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
    sb.append(pad).append(")\n");
    return sb.toString();
  }
  
}
