package com.kas.mq.appl.cli;

import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import com.kas.mq.client.IClient;
import com.kas.mq.impl.MqMessageFactory;
import com.kas.mq.impl.MqTextMessage;
import com.kas.mq.internal.TokenDeque;

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
  protected PutCommand(Scanner scanner, TokenDeque args, IClient client)
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
    }
    
    writeln("Purpose: ");
    writeln(" ");
    writeln("     Put a text message into a previously opened queue");
    writeln(" ");
    writeln("Format: ");
    writeln(" ");
    writeln("     >>--- PUT ---+--- text ---+---><");
    writeln(" ");
    writeln("Description: ");
    writeln(" ");
    writeln("     Put a text message into a queue the was previously opened via the OPEN command.");
    writeln(" ");
    writeln("Examples:");
    writeln(" ");
    writeln("     Put the text \"shy\" as a message into the previously opened queue:");
    writeln("          KAS/MQ Admin> PUT shy");
    writeln(" ");
  }
  
  /**
   * A put command.<br>
   * <br>
   * For more than a single argument, the command will fail with excessive arguments message.
   * For only the command verb, the command will fail with a missing argument message.
   * If no queue was previously opened, the command will fail with a message stating there's no
   * open queue.
   * 
   * @return {@code false} always because there is no way that this command will terminate the command processor.
   */
  public boolean run()
  {
    if (mCommandArgs.size() == 0)
    {
      writeln("Put failed. Missing text to put");
      writeln(" ");
      return false;
    }
    
    StringBuilder sb = new StringBuilder();
    for (String token : mCommandArgs)
      sb.append(token).append(' ');
    
    String text = sb.toString().trim();
    
    MqTextMessage message = MqMessageFactory.createTextMessage(text);
    mClient.put(message);
    writeln(mClient.getResponse());
    writeln(" ");
    return false;
  }
}
