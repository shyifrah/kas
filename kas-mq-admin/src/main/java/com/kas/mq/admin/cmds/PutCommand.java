package com.kas.mq.admin.cmds;

import com.kas.infra.utils.ConsoleUtils;
import com.kas.infra.utils.Validators;
import com.kas.mq.admin.ACommand;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.impl.messages.MqMessageFactory;
import com.kas.mq.internal.MqContextConnection;

/**
 * PUT command
 * 
 * @author Pippo
 */
public class PutCommand extends ACommand
{
  /**
   * Put attributes
   */
  private String mQueue;
  private String mText;
  private int mCount;
  
  /**
   * Construct the command and setting its verbs
   */
  PutCommand()
  {
    mCommandVerbs.add("PUT");
    mCommandVerbs.add("P");
  }
  
  /**
   * Setting data members
   */
  protected void setup()
  {
    mQueue = getString("QUEUE", null);
    mText = getString("TEXT", "");
    mCount = getInteger("COUNT", 1);
  }
  
  /**
   * Execute the command using the specified {@link MqContextConnection}
   * 
   * @param conn
   *   The {@link MqContextConnection} that will be used to execute the command
   */
  public void exec(MqContextConnection conn)
  {
    if (!Validators.isQueueName(mQueue))
      throw new IllegalArgumentException("QUEUE was not specified or invalid: [" + mQueue + ']');
    if (mCount <= 0)
      throw new IllegalArgumentException("Invalid COUNT: [" + mCount + ']');
    
    for (int i = 0; i < mCount; ++i)
    {
      
      IMqMessage msg = MqMessageFactory.createStringMessage(mText);
      conn.put(mQueue, msg);
      ConsoleUtils.writeln(conn.getResponse());
    }
  }
  
  /**
   * Display help screen for this command.
   */
  public void help()
  {
    ConsoleUtils.writelnGreen("Purpose: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Put text messages into queue.");
    ConsoleUtils.writeln("     The QUEUE argument identifies the queue to which messages are written.");
    ConsoleUtils.writeln("     If TEXT is specified, the command will write messages containing that text, otherwise, the command will have no text.");
    ConsoleUtils.writeln("     COUNT is the number of messages to write into the queue. All messages will have the same specified TEXT.");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Format: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("                                  +--TEXT()------+  +--COUNT(1)------+");
    ConsoleUtils.writeln("                                  |              |  |                |");
    ConsoleUtils.writeln("       >>--PUT|P--+--QUEUE(name)--+--------------+--+----------------+--><");
    ConsoleUtils.writeln("                                  |              |  |                |");
    ConsoleUtils.writeln("                                  +--TEXT(text)--+  +--COUNT(count)--+");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Where: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeRed("    name:       ");
    ConsoleUtils.writeln("Queue name.");
    ConsoleUtils.writeRed("    text:       ");
    ConsoleUtils.writeln("The message body.");
    ConsoleUtils.writeRed("    count:      ");
    ConsoleUtils.writeln("The number of messages to write to the queue.");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Examples:");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Write a single message, with no text, to queue APPQ:");
    ConsoleUtils.writeln("          KAS/MQ Admin> PUT QUEUE(APPQ)");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Write 10 messages to queue QUEUE1:");
    ConsoleUtils.writeln("          KAS/MQ Admin> PUT QUEUE(QUEUE1) COUNT(10) TEXT(HELLO)");
    ConsoleUtils.writeln(" ");
  }
  
  /**
   * Get the command text
   * 
   * @return
   *   the command text
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("PUT").append('\n')
      .append("  QUEUE(").append(mQueue).append(")\n")
      .append("  TEXT(").append(mText).append(")\n")
      .append("  COUNT(").append(mCount).append(")\n");
    return sb.toString();
  }
}
