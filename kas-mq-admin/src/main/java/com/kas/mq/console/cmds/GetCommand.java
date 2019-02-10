package com.kas.mq.console.cmds;

import com.kas.comm.serializer.EClassId;
import com.kas.infra.utils.ConsoleUtils;
import com.kas.infra.utils.Validators;
import com.kas.mq.console.ACommand;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.impl.messages.MqStringMessage;
import com.kas.mq.internal.MqContextConnection;

/**
 * GET command
 * 
 * @author Pippo
 */
public class GetCommand extends ACommand
{
  /**
   * Get attributes
   */
  private String mQueue;
  private long mTimeout;
  private long mInterval;
  private int mCount;
  
  /**
   * Construct the command and setting its verbs
   */
  GetCommand()
  {
    mCommandVerbs.add("GET");
    mCommandVerbs.add("G");
  }
  
  /**
   * Setting data members
   */
  protected void setup()
  {
    mQueue = getString("QUEUE", null);
    mTimeout = getLong("TIMEOUT", 0L);
    mInterval = getLong("INTERVAL", 1000L);
    mCount = getInteger("COUNT", 1);
  }
  
  /**
   * Execute the command using the specified {@link MqContextConnection}
   * 
   * @param conn The {@link MqContextConnection} that will be used to execute the command
   */
  public void exec(MqContextConnection conn)
  {
    if (!Validators.isQueueName(mQueue))
      throw new IllegalArgumentException("QUEUE was not specified or invalid: [" + mQueue + ']');
    if (mTimeout < 0)
      throw new IllegalArgumentException("Invalid TIMEOUT: [" + mTimeout + ']');
    if (mInterval <= 0)
      throw new IllegalArgumentException("Invalid INTERVAL: [" + mInterval + ']');
    if (mCount <= 0)
      throw new IllegalArgumentException("Invalid COUNT: [" + mCount + ']');
    
    for (int i = 0; i < mCount; ++i)
    {
      IMqMessage imsg = conn.get(mQueue, mTimeout, mInterval);
      ConsoleUtils.writeln(conn.getResponse());
      if ((imsg != null) && (imsg.createHeader().getClassId() == EClassId.cClassMqStringMessage))
      {
        MqStringMessage msg = (MqStringMessage)imsg; 
        ConsoleUtils.writeln(msg.getBody());
      }
    }
  }
  
  /**
   * Display help screen for this command.
   */
  public void help()
  {
    ConsoleUtils.writelnGreen("Purpose: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Get messages from queue.");
    ConsoleUtils.writeln("     The QUEUE argument identifies the queue from which messages are read.");
    ConsoleUtils.writeln("     If TIMEOUT is specified, the command will wait for this amount of milliseconds until the operation is aborted.");
    ConsoleUtils.writeln("     The command works in a polling methodology. INTERVAL specifies the amount of time to wait between polling operations.");
    ConsoleUtils.writeln("     COUNT is the number of messages to retrieve from the queue.");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Format: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("                                  +--TIMEOUT(0)--------+  +--INTERVAL(1000)------+  +--COUNT(1)------+");
    ConsoleUtils.writeln("                                  |                    |  |                      |  |                |");
    ConsoleUtils.writeln("       >>--GET|G--+--QUEUE(name)--+--------------------+--+----------------------+--+----------------+--><");
    ConsoleUtils.writeln("                                  |                    |  |                      |  |                |");
    ConsoleUtils.writeln("                                  +--TIMEOUT(timeout)--+  +--INTERVAL(interval)--+  +--COUNT(count)--+");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Where: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeRed("    name:       ");
    ConsoleUtils.writeln("Queue name.");
    ConsoleUtils.writeRed("    timeout:    ");
    ConsoleUtils.writeln("The amount of time, in Milliseconds, to wait for a message to be available.");
    ConsoleUtils.writeRed("    interval:   ");
    ConsoleUtils.writeln("The amount of time, in Milliseconds, to wait between each poll operation.");
    ConsoleUtils.writeRed("    count:      ");
    ConsoleUtils.writeln("The number of messages to read from the queue.");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Examples:");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Read a single message from queue APPQ:");
    ConsoleUtils.writeln("          KAS/MQ Admin> GET QUEUE(APPQ)");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Wait for 10 seconds for a message to be available in queue QUEUE1:");
    ConsoleUtils.writeln("          KAS/MQ Admin> GET QUEUE(QUEUE1) TIMEOUT(10000)");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Read 3 messages from queue TELAVIV:");
    ConsoleUtils.writeln("          KAS/MQ Admin> GET QUEUE(TELAVIV) COUNT(3)");
    ConsoleUtils.writeln(" ");
  }
  
  /**
   * Get the command text
   * 
   * @return the command text
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("GET").append('\n')
      .append("  QUEUE(").append(mQueue).append(")\n")
      .append("  TIMEOUT(").append(mTimeout).append(")\n")
      .append("  INTERVAL(").append(mInterval).append(")\n")
      .append("  COUNT(").append(mCount).append(")\n");
    return sb.toString();
  }
}
