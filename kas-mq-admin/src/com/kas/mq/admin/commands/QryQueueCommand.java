package com.kas.mq.admcons.commands;

import java.util.Map;
import java.util.Scanner;
import com.kas.infra.base.Properties;
import com.kas.infra.typedef.TokenDeque;
import com.kas.infra.utils.Validators;
import com.kas.mq.impl.internal.MqClientImpl;

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
   * @param scanner A scanner to be used in case of further interaction is needed 
   * @param args The command arguments specified when command was entered
   * @param client The client that will perform the actual connection
   */
  protected QryQueueCommand(Scanner scanner, TokenDeque args, MqClientImpl client)
  {
    super(scanner, args, client);
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
   * A Define command.<br>
   * <br>
   * For only the "DEFINE" verb, the command will fail with a missing queue name message.
   * For more than a single argument, the command will fail with excessive arguments message.
   * 
   * @return {@code false} always because there is no way that this command will terminate the command processor.
   */
  public boolean run()
  {
    if (mCommandArgs.size() == 0)
    {
      writeln("Missing regular expression");
      writeln(" ");
      return false;
    }
    
    String name = mCommandArgs.poll().toUpperCase();
    boolean prefix = false;
    if (name.endsWith("*"))
    {
      name = name.substring(0, name.length()-1);
      prefix = true;
    }
    
    if ((name.length() > 0) && (!Validators.isQueueName(name)))
    {
      writeln("Invalid queue name \"" + name + "\"");
      writeln(" ");
      return false;
    }
    
    boolean all = false;
    String opt = mCommandArgs.poll();
    if ((opt != null) && (opt.equalsIgnoreCase("ALL")))
      all = true;
    
    if (mCommandArgs.size() > 0)
    {
      writeln("Excessive token \"" + mCommandArgs.poll() + "\"");
      writeln(" ");
      return false;
    }
    
    Properties props = mClient.queryQueue(name, prefix, all);
    output(props, all);
    return false;
  }
  
  /**
   * Print the output of the query followed by the client's response
   * 
   * @param props The properties object containing the response from the KAS/MQ server
   * @param all When {@code true}, query included all data about queues, {@code false} otherwise
   */
  private void output(Properties props, boolean all)
  {
    if (props != null)
    {
      for (Map.Entry<Object, Object> entry : props.entrySet())
      {
        String val = (String)entry.getValue();
        writeln(val);
        writeln(" ");
      }
    }
    
    writeln(mClient.getResponse());
    writeln(" ");
  }
}