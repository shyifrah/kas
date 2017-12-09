package com.kas.q.server;

import java.util.Scanner;
import com.kas.q.ext.KasqClient;
import com.kas.q.server.admin.DefineProcessor;
import com.kas.q.server.admin.DeleteProcessor;
import com.kas.q.server.admin.HelpProcessor;
import com.kas.q.server.admin.QueryProcessor;

public class KasqAdmin extends KasqClient
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
      
      KasqAdmin admin = new KasqAdmin(host, port);
      
      admin.init();
      admin.run(args);
      admin.term();
    }
  }
  
  private KasqAdmin(String host, int port)
  {
    super(host, port);
  }

  private void run(String [] args)
  {
    writeln("KAS/Q Admin Command Processor started");
    writeln(" ");
    
    Scanner scanner = null;
    try
    {
      scanner = new Scanner(System.in);
      String cmdText = read(scanner);
      String [] cmdWords = cmdText.split(" ");
      boolean stop = false;
      while (!stop)
      {
        stop = process(cmdWords);
        if (!stop)
        {
          cmdText = read(scanner);
          cmdWords = cmdText.split(" ");
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
  
  private boolean process(String [] cmdWords)
  {
    boolean stop = false;
    if ((cmdWords.length == 0) || ("".equalsIgnoreCase(cmdWords[0])))
    {
      writeln(" ");
    }
    else
    if (cVerbExit.equalsIgnoreCase(cmdWords[0]))
    {
      stop = true;
    }
    else
    if (cVerbDefine.equalsIgnoreCase(cmdWords[0]))
    {
      DefineProcessor processor = new DefineProcessor(this, cmdWords);
      processor.run();
    }
    else
    if (cVerbDelete.equalsIgnoreCase(cmdWords[0]))
    {
      DeleteProcessor processor = new DeleteProcessor(this, cmdWords);
      processor.run();
    }
    else
    if (cVerbQuery.equalsIgnoreCase(cmdWords[0]))
    {
      QueryProcessor processor = new QueryProcessor(this, cmdWords);
      processor.run();
    }
    else
    if (cVerbHelp.equalsIgnoreCase(cmdWords[0]))
    {
      HelpProcessor processor = new HelpProcessor(cmdWords);
      processor.run();
    }
    else
    {
      writeln("Unknown command verb: [" + cmdWords[0] + "]");
      writeln(" ");
    }
    return stop;
  }
  
  private static String read(Scanner scanner)
  {
    write("KAS/Q> ");
    return scanner.nextLine();
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
