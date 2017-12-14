package com.kas.q.server.admin.cmd;

import java.util.Queue;
import com.kas.infra.base.AKasObject;
import com.kas.q.server.admin.KasqAdminConnection;

public abstract class ACommand extends AKasObject implements Runnable
{
  protected KasqAdminConnection mConnection;
  protected Queue<String>       mCommandArgs;
  
  protected ACommand(KasqAdminConnection conn, Queue<String> args)
  {
    mConnection  = conn;
    mCommandArgs = args;
  }
  
  public abstract void run();
  
  public abstract String toPrintableString(int level);
  
  protected static void writeln(String message)
  {
    System.out.println(message);
  }
}
