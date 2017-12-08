package com.kas.q.server.admin;

import com.kas.q.server.KasqAdmin;

public class HelpProcessor implements Runnable
{
  private String [] mArgs;
  
  public HelpProcessor(String [] args)
  {
    mArgs = args;
  }
  
  public void run()
  {
    if (mArgs.length == 1)
    {
      help();
    }
    else
    if (KasqAdmin.cVerbExit.equalsIgnoreCase(mArgs[1]))
    {
      helpExit();
    }
    else
    if (KasqAdmin.cVerbDefine.equalsIgnoreCase(mArgs[1]))
    {
      helpDefine();
    }
    else if (KasqAdmin.cVerbDelete.equalsIgnoreCase(mArgs[1]))
    {
      helpDelete();
    }
    else if (KasqAdmin.cVerbQuery.equalsIgnoreCase(mArgs[1]))
    {
      helpQuery();
    }
    else if (KasqAdmin.cVerbHelp.equalsIgnoreCase(mArgs[1]))
    {
      helpHelp();
    }
    else
    {
      write("Unknown command verb: [" + mArgs[1] + "]");
      help();
    }
  }
  
  private void help()
  {
    write("KasqAdmin help:");
    
    write(" ");
    helpDefine();
    write(" ");
    write(" ");
    helpQuery();
    write(" ");
    write(" ");
    helpDelete();
    write(" ");
    write(" ");
    helpHelp();
    write(" ");
    write(" ");
    helpExit();
    write(" ");
  }
  
  private void helpDefine()
  {
    write("   >>--- DEFINE ---+--- QUEUE ---+--- name ---><");
    write("                   |             |");
    write("                   +--- TOPIC ---+");
  }
  
  private void helpQuery()
  {
    write("   >>--- QUERY ---+--- QUEUE ---+---+--- name ---+---+----------+---+---><");
    write("                  |             |   |            |   |          |   |");
    write("                  +--- TOPIC ---+   +--- ALL ----+   +--- Pn ---+   |");
    write("                  |                                                 |");
    write("                  +--- ALL -----------------------------------------+");
  }
  
  private void helpDelete()
  {
    write("   >>--- DELETE ---+--- QUEUE ---+--- name ---><");
    write("                   |             |");
    write("                   +--- TOPIC ---+");
  }
  
  private void helpHelp()
  {
    write("   >>--- HELP ---><");
  }
  
  private void helpExit()
  {
    write("   >>--- EXIT ---><");
  }
  
  private void write(String message)
  {
    System.out.println(message);
  }
}
