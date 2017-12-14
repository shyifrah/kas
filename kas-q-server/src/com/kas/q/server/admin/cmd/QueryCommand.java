package com.kas.q.server.admin.cmd;

import java.util.Queue;
import javax.jms.JMSException;
import com.kas.q.ext.EDestinationType;
import com.kas.q.requests.QueryRequest;
import com.kas.q.server.admin.KasqAdminConnection;

public class QueryCommand extends ACommand
{
  public QueryCommand(KasqAdminConnection conn, Queue<String> args)
  {
    super(conn, args);
  }
  
  public void run()
  {
    String pDestType = mCommandArgs.poll();
    EDestinationType destType = null;
    if (pDestType == null)
    {
      throw new IllegalArgumentException("Destination type is missing");
    }
    
    destType = EDestinationType.fromString(pDestType);
    String pDestName = mCommandArgs.poll();
    
    if ((destType == EDestinationType.cAll) && (pDestName != null))
    {
      throw new IllegalArgumentException("Exessive token following ALL: " + pDestName);
    }
    
    if (destType == EDestinationType.cAll)
    {
      execute(destType);
    }
    else
    if (pDestName == null)
    {
      throw new IllegalArgumentException("Destination name is missing");
    }
    else
    if (pDestName.equalsIgnoreCase("all"))
    {
      String temp = mCommandArgs.poll();
      if (temp != null)
      {
        throw new IllegalArgumentException("Excessive token following ALL: " + temp);
      }
      execute(destType, null);
    }
    else
    if ((!pDestName.startsWith("'")) || (!pDestName.endsWith("'")) || (pDestName.length() <= 2))
    {
      throw new IllegalArgumentException("Invalid destination name: " + pDestName + ". Must be ALL or name enclosed with apostrpohes");
    }
    else
    {
      String destName = pDestName.substring(1, pDestName.length()-1);
      String temp = mCommandArgs.poll();
      if (temp != null)
      {
        throw new IllegalArgumentException("Excessive token following destination name: " + temp);
      }
      else
      {
        execute(destType, destName);
      }
    }
  }
  
  protected void execute(EDestinationType type)
  {
    execute(type, null);
  }
  
  protected void execute(EDestinationType type, String name)
  {
    QueryRequest request = null;
    boolean success = false;
    try
    {
      request = new QueryRequest(type, name);
    }
    catch (JMSException e) {}
    
    if (request != null)
    {
      String output = mConnection.query(request);
      if (output != null)
      {
        writeln(output);
        writeln(" ");
        success = true;
      }
    }
    
    if (!success)
    {
      writeln("Error occured while trying to execute query command");
      writeln(" ");
    }
  }
  
  public String toPrintableString(int level)
  {
    return null;
  }
}
