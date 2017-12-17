package com.kas.q.server.admin;

import java.util.Scanner;
import javax.jms.JMSException;
import com.kas.config.MainConfiguration;
import com.kas.infra.base.threads.ThreadPool;
import com.kas.q.server.admin.cmd.DefineCommand;
import com.kas.q.server.admin.cmd.DeleteCommand;
import com.kas.q.server.admin.cmd.HelpCommand;
import com.kas.q.server.admin.cmd.QueryCommand;
import com.kas.q.server.typedef.CommandQueue;

public class KasqAdmin
{
  public static final String cVerbExit   = "exit";
  public static final String cVerbDefine = "define";
  public static final String cVerbDelete = "delete";
  public static final String cVerbQuery  = "query";
  public static final String cVerbHelp   = "help";
  
  /***************************************************************************************************************
   * Main function.
   * 
   * @param args arguments passed to the main function 
   */
  public static void main(String [] args)
  {
    if (args.length != 2)
    {
      writeln("Invalid number of arguments");
      writeln("Usage: java -cp <...> " + KasqAdmin.class.getName() + " <hostname> <port>");
    }
    else
    {
      String host = args[0];
      int    port = -1;
      try
      {
        port = Integer.valueOf(args[1]);
      }
      catch (Throwable e) {}
      
      MainConfiguration config = MainConfiguration.getInstance();
      config.init();
      
      KasqAdmin admin = null;
      try
      {
        admin = new KasqAdmin(host, port);
      }
      catch (JMSException e)
      {
        writeln(e.getMessage());
      }
      if (admin != null) admin.run(args);
      
      config.term();
      ThreadPool.shutdownNow();
    }
  }
  
  private KasqAdminConnection mConnection;
  
  private KasqAdmin(String host, int port) throws JMSException
  {
    mConnection = new KasqAdminConnection(host, port);
  }

  /***************************************************************************************************************
   * {@code KasqAdmin} execution method
   * 
   * @param args arguments passed to {@code main} function
   */
  private void run(String [] args)
  {
    writeln("KAS/Q Admin Command Processor started");
    writeln(" ");
    
    Scanner scanner = null;
    try
    {
      scanner = new Scanner(System.in);
      CommandQueue command = read(scanner);
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
  
  /***************************************************************************************************************
   * Process a command represented by a queue of tokens.<br>
   * According to the first element in the queue - the command verb - we determine which type of Command object
   * should be created and then we execute it.
   * 
   * @param cmdWords the queue containing the command tokens
   * 
   * @return true if processing ended successfully, false otherwise
   */
  private boolean process(CommandQueue cmdWords)
  {
    boolean stop = false;
    if (cmdWords.isEmpty() || cmdWords.peek().equals(""))
    {
      writeln(" ");
    }
    else
    {
      String verb = cmdWords.poll();
      if (cVerbExit.equalsIgnoreCase(verb))
      {
        stop = true;
      }
      else
      if (cVerbDefine.equalsIgnoreCase(verb))
      {
        DefineCommand command = new DefineCommand(mConnection, cmdWords);
        command.run();
      }
      else
      if (cVerbDelete.equalsIgnoreCase(verb))
      {
        DeleteCommand command = new DeleteCommand(mConnection, cmdWords);
        command.run();
      }
      else
      if (cVerbQuery.equalsIgnoreCase(verb))
      {
        try
        {
          QueryCommand command = new QueryCommand(mConnection, cmdWords);
          command.run();
        }
        catch (IllegalArgumentException e)
        {
          writeln("Error occurred while running QUERY command: " + e.getMessage());
        }
      }
      else
      if (cVerbHelp.equalsIgnoreCase(verb))
      {
        HelpCommand command = new HelpCommand(mConnection, cmdWords);
        command.run();
      }
      else
      {
        writeln("Unknown command verb: [" + verb + "]");
        writeln(" ");
      }
    }
    
    return stop;
  }
  
  /***************************************************************************************************************
   * Reading a command (one line) from STDIN and return it as a queue of tokens.
   * 
   * @param scanner the Scanner object associated with STDIN
   * 
   * @return a queue in which each element is a token from the read line
   */
  private static CommandQueue read(Scanner scanner)
  {
    write("KAS/Q> ");
    String cmd = scanner.nextLine();
    String [] a = cmd.split(" ");
    CommandQueue q = new CommandQueue();
    for (String word : a)
      q.offer(word);
    return q;
  }
  
  /***************************************************************************************************************
   * Writing a message to STDOUT.
   * 
   * @param message the message to print
   */
  private static void write(String message)
  {
    System.out.print(message);
  }
  
  /***************************************************************************************************************
   * Writing a message to STDOUT. The message will be followed by a Newline character.
   * 
   * @param message the message to print
   */
  private static void writeln(String message)
  {
    System.out.println(message);
  }
}
