package com.kas.q.server.admin.cmd;

import java.util.Queue;
import com.kas.q.server.admin.KasqAdminConnection;

public class DefineCommand extends ACommand
{
  //private KasqAdminConnection mConnection;
  //private Queue<String>       mCommandArgs;
  
  public DefineCommand(KasqAdminConnection conn, Queue<String> args)
  {
    super(conn, args);
  }
  
  public void run()
  {
    
  }
  
  public String toPrintableString(int level)
  {
    return null;
  }
}
