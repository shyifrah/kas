package com.kas.q.server.admin.cmd;

import com.kas.infra.base.AKasObject;
import com.kas.q.server.admin.KasqAdminConnection;
import com.kas.q.server.typedef.CommandQueue;

public abstract class ACommand extends AKasObject implements Runnable
{
  protected KasqAdminConnection mConnection;
  protected CommandQueue        mCommandArgs;
  
  /***************************************************************************************************************
   * Constructs a {@code ACommand} object, specifying the administrator connection object and the queue
   * representing the command arguments. No threads are involved in execution of this object. It implements
   * the {@code Runnable} interface just for the sake of the {@link #run()} method.
   * 
   * @param conn the {@code KasqAdminCommand} which will be used to process the command
   * @param args the command arguments
   */
  protected ACommand(KasqAdminConnection conn, CommandQueue args)
  {
    mConnection  = conn;
    mCommandArgs = args;
  }
  
  /***************************************************************************************************************
   * Running the command.
   */
  public abstract void run();
  
  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    sb.append(name()).append("(\n")
      .append(pad).append("  Connection=(").append(mConnection.toPrintableString(level+1)).append(")\n")
      .append(pad).append("  CommandArgs=(").append(mCommandArgs.toPrintableString(level+1)).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
  
  /***************************************************************************************************************
   * Writing a message to STDOUT. The message will be followed by a Newline character.
   * 
   * @param message the message to print
   */
  protected static void writeln(String message)
  {
    System.out.println(message);
  }
}
