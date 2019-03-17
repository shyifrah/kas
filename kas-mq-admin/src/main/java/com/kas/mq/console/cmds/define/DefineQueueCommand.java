package com.kas.mq.console.cmds.define;

import com.kas.infra.utils.ConsoleUtils;
import com.kas.infra.utils.Validators;
import com.kas.mq.console.ACommand;
import com.kas.mq.internal.EQueueDisp;
import com.kas.mq.internal.MqContextConnection;

/**
 * DEFINE QUEUE command
 * 
 * @author Pippo
 */
public class DefineQueueCommand extends ACommand
{
  /**
   * Queue attributes
   */
  private String mName;
  private String mDescription;
  private Integer mThreshold;
  private EQueueDisp mDisposition;
  
  /**
   * Construct the command and setting its verbs
   */
  DefineQueueCommand()
  {
    mCommandVerbs.add("QUEUE");
    mCommandVerbs.add("Q");
  }
  
  /**
   * Setting data members
   */
  protected void setup()
  {
    mName = getString("NAME", null);
    mDescription = getString("DESCRIPTION", "");
    mThreshold = getInteger("THRESHOLD", 1000);
    mDisposition = getEnum("DISPOSITION", EQueueDisp.class, EQueueDisp.TEMPORARY);
  }
  
  /**
   * Execute the command using the specified {@link MqContextConnection}
   * 
   * @param conn
   *   The {@link MqContextConnection} that will be used to execute the command
   */
  public void exec(MqContextConnection conn)
  {
    if (!Validators.isQueueName(mName))
      throw new IllegalArgumentException("NAME was not specified or invalid: [" + mName + ']');
    if (!Validators.isThreshold(mThreshold))
      throw new IllegalArgumentException("Invalid THRESHOLD: [" + mThreshold + "]; Value must be in range 1-100,000");
    if (!Validators.isQueueDesc(mDescription))
      throw new IllegalArgumentException("Invalid DESCRIPTION: [" + mDescription + "]; Value cannot exceed 256 characters");
    
    conn.defineQueue(mName, mDescription, mThreshold, mDisposition);
    ConsoleUtils.writeln("%s", conn.getResponse());
  }
  
  /**
   * Display help screen for this command.
   */
  public void help()
  {
    ConsoleUtils.writelnGreen("Purpose: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Define a queue entity.");
    ConsoleUtils.writeln("     The queue is identified by a NAME, which must be unique across the local queue manager, but not in the cluster.");
    ConsoleUtils.writeln("     The queue's DESCRIPTION is a free text and is entirely ignored.");
    ConsoleUtils.writeln("     The queue will hold a maximum amount of messages as specified in the THRESHOLD.");
    ConsoleUtils.writeln("     The queue's DISPOSITION determines the nature of the queue contents. A TEMPORARY queue is one that is deleted");
    ConsoleUtils.writeln("     upon server restart, whilst PERMANENT means the queue contents will be backed up to the local file system");
    ConsoleUtils.writeln("     before the server is shut down.");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Format: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("                                                  +--DESCRIPTION()------+  +--THRESHOLD(1000)-------+  +--DISPOSITION(TEMPORARY)--+");
    ConsoleUtils.writeln("                                                  |                     |  |                        |  |                          |");
    ConsoleUtils.writeln("       >>--DEFINE|DEF--+--QUEUE|Q--+--NAME(name)--+---------------------+--+------------------------+--+--------------------------+--+><");
    ConsoleUtils.writeln("                                                  |                     |  |                        |  |                          |");
    ConsoleUtils.writeln("                                                  +--DESCRIPTION(desc)--+  +--THRESHOLD(threshold)--+  +--DISPOSITION(PERMANENT)--+");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Where: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeRed("    name:       ");
    ConsoleUtils.writeln("Queue name.");
    ConsoleUtils.writeRed("    desc:       ");
    ConsoleUtils.writeln("Queue description.");
    ConsoleUtils.writeRed("    threshold:  ");
    ConsoleUtils.writeln("Queue threshold. The maximum number of messages this queue can accomodate.");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Examples:");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Define temporary queue named QUEUE1A with default values:");
    ConsoleUtils.writeln("          KAS/MQ Admin> DEFINE QUEUE NAME(APPQ1) DESCRIPTION(Application queue #1)");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Define temporary queue named LONDONQ with threshold of 10,000 messages:");
    ConsoleUtils.writeln("          KAS/MQ Admin> DEFINE QUEUE NAME(LONDONQ) THRESHOLD(10000) DISPOSITION(TEMPORARY)");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Define permanent queue named TELAVIV1 with threshold of 100 messages:");
    ConsoleUtils.writeln("          KAS/MQ Admin> DEFINE QUEUE NAME(TELAVIV1) THRESHOLD(100) DISPOSITION(PERMANENT)");
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
    sb.append("  QUEUE").append('\n')
      .append("    NAME(").append(mName).append(")\n")
      .append("    DESCRIPTION(").append(mDescription).append(")\n")
      .append("    THRESHOLD(").append(mThreshold).append(")\n")
      .append("    DISPOSITION(").append(mDisposition).append(")\n");
    return sb.toString();
  }
}
