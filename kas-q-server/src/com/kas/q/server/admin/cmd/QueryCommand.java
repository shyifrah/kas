package com.kas.q.server.admin.cmd;

import javax.jms.JMSException;
import com.kas.q.ext.EDestinationType;
import com.kas.q.requests.QueryRequest;
import com.kas.q.server.admin.KasqAdminConnection;
import com.kas.q.server.typedef.CommandQueue;

public class QueryCommand extends ACommand
{
  /***************************************************************************************************************
   * Constructs a {@code QueryCommand} object, specifying the administrator connection object and the queue
   * representing the command arguments. The construction is simply passing the arguments to the super class.
   * 
   * @param conn the {@code KasqAdminCommand} which will be used to process the command
   * @param args the command arguments
   */
  public QueryCommand(KasqAdminConnection conn, CommandQueue args)
  {
    super(conn, args);
  }
  
  /***************************************************************************************************************
   * Running the Query command.
   * The use-cases that are handled (QUEUE can be replaced with TOPIC):
   *   "QUERY"                     > IllegalArgumentException
   *   "QUERY unknown_type"        > IllegalArgumentException
   *   "QUERY ALL ..."             > IllegalArgumentException
   *   "QUERY ALL"                 > execute
   *   "QUERY QUEUE"               > IllegalArgumentException
   *   "QUERY QUEUE ALL"           > execute
   *   "QUERY QUEUE name"          > IllegalArgumentException
   *   "QUERY QUEUE 'name' ..."    > IllegalArgumentException
   *   "QUERY QUEUE 'name'"        > execute
   */
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
  
  /***************************************************************************************************************
   * Command execution.
   * 
   * @param type the destination type
   */
  protected void execute(EDestinationType type)
  {
    execute(type, null);
  }
  
  /***************************************************************************************************************
   * Command execution.<br>
   * Creating a new {@code QueryRequest} object and call {@link KasqAdminConnection#query(QueryRequest)}.
   * 
   * @param type the destination type
   * @param name the destination name
   */
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
      writeln(output);
      writeln(" ");
      success = true;
    }
    
    if (!success)
    {
      writeln("Error occured while trying to execute query command");
      writeln(" ");
    }
  }
}
