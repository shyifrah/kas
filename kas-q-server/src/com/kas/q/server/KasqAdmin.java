package com.kas.q.server;

import java.util.Scanner;
import com.kas.q.server.admin.DefineProcessor;
import com.kas.q.server.admin.DeleteProcessor;
import com.kas.q.server.admin.HelpProcessor;
import com.kas.q.server.admin.QueryProcessor;

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
    KasqAdmin admin = new KasqAdmin();
    admin.run(args);
  }
  
  private void run(String [] args)
  {
    write("KAS/Q Admin Command Processor started");
    write(" ");
    
    if (args.length == 0)
    {
      // interactive mode
      interactive();
    }
    else
    {
      process(args);
    }
    
    write(" ");
    write("KAS/Q Admin Command Processor ended");
  }
  
  private void interactive()
  {
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
  }
  
  private boolean process(String [] cmdWords)
  {
    boolean stop = false;
    String cmd = cmdWords[0];
    if (cVerbExit.equalsIgnoreCase(cmd))
    {
      stop = true;
    }
    else
    if (cVerbDefine.equalsIgnoreCase(cmd))
    {
      DefineProcessor processor = new DefineProcessor(cmdWords);
      processor.run();
    }
    else
    if (cVerbDelete.equalsIgnoreCase(cmd))
    {
      DeleteProcessor processor = new DeleteProcessor(cmdWords);
      processor.run();
    }
    else
    if (cVerbQuery.equalsIgnoreCase(cmd))
    {
      QueryProcessor processor = new QueryProcessor(cmdWords);
      processor.run();
    }
    else
    if (cVerbHelp.equalsIgnoreCase(cmd))
    {
      HelpProcessor processor = new HelpProcessor(cmdWords);
      processor.run();
    }
    else
    {
      write("Unknown command verb: [" + cmd + "]");
    }
    return stop;
  }
  
  private String read(Scanner scanner)
  {
    return scanner.nextLine();
  }
  
  private void write(String message)
  {
    System.out.println(message);
  }
}
