package com.kas.mq.admin.commands;

import com.kas.infra.base.Properties;
import com.kas.infra.typedef.TokenDeque;
import com.kas.infra.utils.Validators;
import com.kas.mq.impl.MqContext;
import com.kas.mq.impl.IMqGlobals.EQueryType;
import com.kas.mq.impl.messages.MqStringMessage;
import com.kas.mq.internal.IMqConstants;

/**
 * A QUERY QUEUE command
 * 
 * @author Pippo
 */
public class QryQueueCommand extends ACliCommand
{
  /**
   * Construct a {@link QryQueueCommand} passing the command arguments and the client object
   * that will perform actions on behalf of this command.
   * 
   * @param args The command arguments specified when command was entered
   * @param client The client that will perform the actual connection
   */
  protected QryQueueCommand(TokenDeque args, MqContext client)
  {
    super(args, client);
  }

  /**
   * Display help screen for this command.
   * Not in use for QUERY QUEUE.
   */
  public void help()
  {
    writeln(" ");
  }
  
  /**
   * A query queue command.<br>
   * <br>
   * The command expects the "QUERY QUEUE" followed by a one or two arguments. The first argument
   * is the queue name / queue name prefix. This argument is mandatory.<br>
   * The second argument is either "ALL" or "BASIC". Omitting this argument is just like
   * specifying "BASIC".<br>
   * If more arguments are specified, the command will fail with an "excessive arguments" message 
   * 
   * @return {@code false} always because there is no way that this command will terminate the command processor.
   */
  public boolean run()
  {
    if (mCommandArgs.size() == 0)
    {
      writeln("Missing queue name/prefix");
      writeln(" ");
      return false;
    }
    
    Properties qprops = new Properties();
    
    String name = mCommandArgs.poll().toUpperCase();
    if (name.endsWith("*"))
    {
      name = name.substring(0, name.length()-1);
      qprops.setBoolProperty(IMqConstants.cKasPropertyQryqPrefix, true);
    }
    
    if ((name.length() > 0) && (!Validators.isQueueName(name)))
    {
      writeln("Invalid queue name \"" + name + "\"");
      writeln(" ");
      return false;
    }
    
    qprops.setStringProperty(IMqConstants.cKasPropertyQryqQueueName, name);
    
    boolean all = false;
    String opt = mCommandArgs.poll();
    if ((opt != null) && (opt.equalsIgnoreCase("ALL")))
      all = true;
    
    qprops.setBoolProperty(IMqConstants.cKasPropertyQryqAllData, all);
    
    if (mCommandArgs.size() > 0)
    {
      writeln("Excessive token \"" + mCommandArgs.poll() + "\"");
      writeln(" ");
      return false;
    }
    
    MqStringMessage result = mClient.queryServer(EQueryType.cQueryQueue, qprops);
    if (result != null)
      writeln(result.getBody());
    writeln(mClient.getResponse());
    writeln(" ");
    return false;
  }
}
