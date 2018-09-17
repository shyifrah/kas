package com.kas.mq.admin.commands;

import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import com.kas.infra.typedef.TokenDeque;
import com.kas.infra.utils.Validators;
import com.kas.mq.impl.MqMessageFactory;
import com.kas.mq.impl.MqTextMessage;
import com.kas.mq.impl.internal.MqClientImpl;

/**
 * A PUT command
 * 
 * @author Pippo
 */
public class PutCommand extends ACliCommand
{
  static public final Set<String> sCommandVerbs = new TreeSet<String>();
  static
  {
    sCommandVerbs.add("PUT");
  }
  
  /**
   * Construct a {@link PutCommand} passing the command arguments and the client object
   * that will perform actions on behalf of this command.
   * 
   * @param scanner A scanner to be used in case of further interaction is needed 
   * @param args The command arguments specified when command was entered
   * @param client The client that will perform the actual connection
   */
  protected PutCommand(Scanner scanner, TokenDeque args, MqClientImpl client)
  {
    super(scanner, args, client);
  }

  /**
   * Display help screen for this command.
   */
  public void help()
  {
    if (mCommandArgs.size() > 0)
    {
      writeln("Execssive command arguments are ignored for HELP PUT");
      writeln(" ");
      return;
    }
    
    writeln("Purpose: ");
    writeln(" ");
    writeln("     Put a text message into queue");
    writeln(" ");
    writeln("Format: ");
    writeln(" ");
    writeln("     >>--- PUT ---+--- queue ---+---+--- text ---+---><");
    writeln(" ");
    writeln("Description: ");
    writeln(" ");
    writeln("     The command will create a text message with a body holding the specified text,");
    writeln("     and then will put it into the specified queue.");
    writeln(" ");
    writeln("Examples:");
    writeln(" ");
    writeln("     Put the text \"shy\" as a message into queue TEMP1Q:");
    writeln("          KAS/MQ Admin> PUT TEMP1Q shy");
    writeln(" ");
  }
  
  /**
   * A put command.<br>
   * <br>
   * First token is the queue name.
   * All remaining tokens (at least one) are the message text.
   * 
   * @return {@code false} always because there is no way that this command will terminate the command processor.
   */
  public boolean run()
  {
    if (mCommandArgs.size() == 0)
    {
      writeln("Missing queue name");
      writeln(" ");
      return false;
    }
    
    String queue = mCommandArgs.poll().toUpperCase();
    if (!Validators.isQueueName(queue))
    {
      writeln("Invalid queue name \"" + queue + "\"");
      writeln(" ");
      return false;
    }
    
    if (mCommandArgs.size() == 0)
    {
      writeln("Missing message text");
      writeln(" ");
      return false;
    }
    
    StringBuilder sb = new StringBuilder();
    for (String token : mCommandArgs)
      sb.append(token).append(' ');
    String text = sb.toString().trim();
    MqTextMessage message = MqMessageFactory.createTextMessage(text);
    
    mClient.put(queue, message);
    writeln(mClient.getResponse());
    writeln(" ");
    return false;
  }
}
