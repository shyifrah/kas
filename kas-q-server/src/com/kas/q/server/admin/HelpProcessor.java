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
      helpExit(true);
    }
    else
    if (KasqAdmin.cVerbDefine.equalsIgnoreCase(mArgs[1]))
    {
      helpDefine(true);
    }
    else if (KasqAdmin.cVerbDelete.equalsIgnoreCase(mArgs[1]))
    {
      helpDelete(true);
    }
    else if (KasqAdmin.cVerbQuery.equalsIgnoreCase(mArgs[1]))
    {
      helpQuery(true);
    }
    else if (KasqAdmin.cVerbHelp.equalsIgnoreCase(mArgs[1]))
    {
      helpHelp(true);
    }
    else
    {
      writeln("Unknown command verb: [" + mArgs[1] + "]");
      writeln("Type \"help\" to display the help screen");
      writeln(" ");
    }
  }
  
  private void help()
  {
    writeln("KasqAdmin help:");
    writeln(" ");
    helpDefine(false);
    writeln(" ");
    helpQuery(false);
    writeln(" ");
    helpDelete(false);
    writeln(" ");
    helpHelp(false);
    writeln(" ");
    helpExit(false);
  }
  
  private void helpDefine(boolean extensive)
  {
    writeln("   Command syntax:");
    writeln(" ");
    writeln("      >>--- DEFINE ---+--- QUEUE ---+--- name ---><");
    writeln("                      |             |");
    writeln("                      +--- TOPIC ---+");
    writeln(" ");
    if (extensive)
    {
      writeln("   Where:");
      writeln(" ");
      writeln("      name.........: The name of the destination to be defined.");
      writeln(" ");
      writeln("   Purpose:");
      writeln(" ");
      writeln("      Define a destination.");
      writeln(" ");
      writeln("      First token should be the destination type - QUEUE or TOPIC.");
      writeln("      The second token is the name of the destination to be defined.");
      writeln(" ");
    }
  }
  
  private void helpQuery(boolean extensive)
  {
    writeln("   Command syntax:");
    writeln(" ");
    writeln("      >>--- QUERY ---+--- QUEUE ---+---+--- name ---+---+----------+---+---><");
    writeln("                     |             |   |            |   |          |   |");
    writeln("                     +--- TOPIC ---+   +--- ALL ----+   +--- Pn ---+   |");
    writeln("                     |                                                 |");
    writeln("                     +--- ALL -----------------------------------------+");
    writeln(" ");
    if (extensive)
    {
      writeln("   Where:");
      writeln(" ");
      writeln("      name.........: The name of the destination to be queried.");
      writeln("      Pn...........: The priority sub-queue to be queried, where 'n' is a value between 0 to 9.");
      writeln(" ");
      writeln("   Purpose:");
      writeln(" ");
      writeln("      Query information on a specific destination or the entire repository.");
      writeln(" ");
      writeln("      First token should be the destination type - QUEUE or TOPIC - or ALL, if information should be queried");
      writeln("      on the entire KAS/Q repository.");
      writeln(" ");
      writeln("      If ALL is specified, no other tokens can be specified.");
      writeln("      If destination type is specified, the next token can be one of two:");
      writeln("        ALL  - if information should be queried on all destinations of the specified type.");
      writeln("        name - if information should be queried for the specific destination.");
      writeln(" ");
      writeln("      The last optional token is the Pn, which simply requests information to be limitted for the specific");
      writeln("      priority queue.");
      writeln(" ");
    }
  }
  
  private void helpDelete(boolean extensive)
  {
    writeln("   Command syntax:");
    writeln(" ");
    writeln("      >>--- DELETE ---+--- QUEUE ---+--- name ---><");
    writeln("                      |             |");
    writeln("                      +--- TOPIC ---+");
    writeln(" ");
    if (extensive)
    {
      writeln("   Where:");
      writeln(" ");
      writeln("      name.........: The name of the destination to be deleted.");
      writeln(" ");
      writeln("   Purpose:");
      writeln(" ");
      writeln("      Delete a destination.");
      writeln(" ");
      writeln("      First token should be the destination type - QUEUE or TOPIC.");
      writeln("      The second token is the name of the destination to be deleted.");
      writeln(" ");
    }
  }
  
  private void helpHelp(boolean extensive)
  {
    writeln("   Command syntax:");
    writeln(" ");
    writeln("      >>--- HELP ---+------------+---><");
    writeln("                    |            |");
    writeln("                    +--- verb ---+");
    writeln(" ");
    if (extensive)
    {
      writeln("   Where:");
      writeln(" ");
      writeln("      verb.........: The command verb for which you seek help for.");
      writeln(" ");
      writeln("   Purpose:");
      writeln(" ");
      writeln("      Display help screen.");
      writeln(" ");
      writeln("      If a command verb is specified, then help will be displayed for the specific command verb.");
      writeln(" ");
    }
  }
  
  private void helpExit(boolean extensive)
  {
    writeln("   Command syntax:");
    writeln(" ");
    writeln("      >>--- EXIT ---><");
    writeln(" ");
    if (extensive)
    {
      writeln("   Purpose:");
      writeln(" ");
      writeln("      Terminates the KAS/Q Admin Command Processor.");
      writeln(" ");
    }
  }
  
  private static void writeln(String message)
  {
    System.out.println(message);
  }
}
