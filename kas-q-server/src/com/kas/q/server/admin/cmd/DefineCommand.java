package com.kas.q.server.admin.cmd;

import com.kas.q.server.admin.KasqAdminConnection;
import com.kas.q.server.typedef.CommandQueue;

public class DefineCommand extends ACommand
{
  //private KasqAdminConnection mConnection;
  //private Queue<String>       mCommandArgs;
  
  public DefineCommand(KasqAdminConnection conn, CommandQueue args)
  {
    super(conn, args);
  }
  
  public void run()
  {
    
  }
}
