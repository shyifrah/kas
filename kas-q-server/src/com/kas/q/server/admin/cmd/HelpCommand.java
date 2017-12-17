package com.kas.q.server.admin.cmd;

import com.kas.q.server.admin.KasqAdmin;
import com.kas.q.server.admin.KasqAdminConnection;
import com.kas.q.server.typedef.CommandQueue;

public class HelpCommand extends ACommand
{
  public HelpCommand(KasqAdminConnection conn, CommandQueue args)
  {
    super(conn, args);
  }
  
 public void run()
  {
    if (mCommandArgs.isEmpty())
    {
      help();
    }
    else
    {
      String helpVerb = mCommandArgs.poll();
      String temp     = mCommandArgs.poll();
      if (temp != null)
      {
        writeln("Invalid HELP command. Excessive token encountered: [" + temp + "]");
        writeln("Type \"HELP\" to display the help screen");
        writeln(" ");
      }
      else
      if (KasqAdmin.cVerbExit.equalsIgnoreCase(helpVerb))
      {
        helpExit(true);
      }
      else
      if (KasqAdmin.cVerbDefine.equalsIgnoreCase(helpVerb))
      {
        helpDefine(true);
      }
      else if (KasqAdmin.cVerbDelete.equalsIgnoreCase(helpVerb))
      {
        helpDelete(true);
      }
      else if (KasqAdmin.cVerbQuery.equalsIgnoreCase(helpVerb))
      {
        helpQuery(true);
      }
      else if (KasqAdmin.cVerbHelp.equalsIgnoreCase(helpVerb))
      {
        helpHelp(true);
      }
      else
      {
        writeln("Unknown command verb: [" + helpVerb + "]");
        writeln("Type \"HELP\" to display the help screen");
        writeln(" ");
      }
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
    writeln("      >>--- DEFINE ---+--- QUEUE ---+--- 'name' ---><");
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
    writeln("      >>--- QUERY ---+--- QUEUE ---+---+-- 'name' --+---+---><");
    writeln("                     |             |   |            |   |");
    writeln("                     +--- TOPIC ---+   +--- ALL ----+   |");
    writeln("                     |                                  |");
    writeln("                     +--- ALL --------------------------+");
    writeln(" ");
    if (extensive)
    {
      writeln("   Where:");
      writeln(" ");
      writeln("      name.........: The name of the destination to be queried.");
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
    }
  }
  
  private void helpDelete(boolean extensive)
  {
    writeln("   Command syntax:");
    writeln(" ");
    writeln("      >>--- DELETE ---+--- QUEUE ---+--- 'name' ---><");
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
}
