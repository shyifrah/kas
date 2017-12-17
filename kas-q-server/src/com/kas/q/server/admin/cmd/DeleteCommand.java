package com.kas.q.server.admin.cmd;

import com.kas.q.server.admin.KasqAdminConnection;
import com.kas.q.server.typedef.CommandQueue;

public class DeleteCommand extends ACommand
{
  //private KasqAdminConnection mConnection;
  //private Queue<String>       mCommandArgs;
  
  public DeleteCommand(KasqAdminConnection conn, CommandQueue args)
  {
    super(conn, args);
  }
  
  public void run()
  {
    
  }
}
