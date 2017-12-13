package com.kas.q.server.admin;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Scanner;
import javax.jms.JMSException;
import com.kas.config.MainConfiguration;
import com.kas.infra.base.threads.ThreadPool;
import com.kas.q.server.admin.cmd.DefineCommand;
import com.kas.q.server.admin.cmd.DeleteCommand;
import com.kas.q.server.admin.cmd.HelpCommand;
import com.kas.q.server.admin.cmd.QueryCommand;

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

  private void run(String [] args)
  {
    writeln("KAS/Q Admin Command Processor started");
    writeln(" ");
    
    Scanner scanner = null;
    try
    {
      scanner = new Scanner(System.in);
      Queue<String> command = read(scanner);
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
  
  private boolean process(Queue<String> cmdWords)
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
        DefineCommand processor = new DefineCommand(mConnection, cmdWords);
        processor.run();
      }
      else
      if (cVerbDelete.equalsIgnoreCase(verb))
      {
        DeleteCommand processor = new DeleteCommand(mConnection, cmdWords);
        processor.run();
      }
      else
      if (cVerbQuery.equalsIgnoreCase(verb))
      {
        QueryCommand command = new QueryCommand(mConnection, cmdWords);
        command.run();
      }
      else
      if (cVerbHelp.equalsIgnoreCase(verb))
      {
        HelpCommand command = new HelpCommand(cmdWords);
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
  
  
  private static Queue<String> read(Scanner scanner)
  {
    write("KAS/Q> ");
    String cmd = scanner.nextLine();
    String [] a = cmd.split(" ");
    Queue<String> q = new ArrayDeque<String>();
    for (String word : a)
      q.offer(word);
    return q;
  }
  
  private static void write(String message)
  {
    System.out.print(message);
  }
  
  private static void writeln(String message)
  {
    System.out.println(message);
  }
}
