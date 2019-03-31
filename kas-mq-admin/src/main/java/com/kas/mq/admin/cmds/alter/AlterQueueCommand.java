package com.kas.mq.admin.cmds.alter;

import com.kas.infra.utils.ConsoleUtils;
import com.kas.mq.admin.ACommand;
import com.kas.mq.internal.EQueueDisp;
import com.kas.mq.internal.MqContextConnection;

/**
 * ALTER QUEUE command
 * 
 * @author Pippo
 */
public class AlterQueueCommand extends ACommand
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
  AlterQueueCommand()
  {
    mCommandVerbs.add("ALTER QUEUE");
    mCommandVerbs.add("ALTER Q");
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
  }
  
  /**
   * Display help screen for this command.
   */
  public void help()
  {
    ConsoleUtils.writelnGreen("Purpose: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Alter a queue entity.");
    ConsoleUtils.writeln("     You can alter a queue's name by using the NEWNAME attribute, the queue's DESCRIPTION, THRESHOLD or DISPOSITION.");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Format: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("       >>--ALTER|ALT|AL--+--QUEUE|Q--+--NAME(name)--+--------------------+--+---------------------+--+------------------------+--+--------------------------+--><");
    ConsoleUtils.writeln("                                                    |                    |  |                     |  |                        |  |                          |");
    ConsoleUtils.writeln("                                                    +--NEWNAME(newname)--+  +--DESCRIPTION(desc)--+  +--THRESHOLD(threshold)--+  +--DISPOSITION(TEMPORARY)--+");
    ConsoleUtils.writeln("                                                                                                                                 |                          |");
    ConsoleUtils.writeln("                                                                                                                                 +--DISPOSITION(PERMANENT)--+");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Where: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeRed("    name:       ");
    ConsoleUtils.writeln("Queue name.");
    ConsoleUtils.writeRed("    newname:    ");
    ConsoleUtils.writeln("The new name to assign to this queue.");
    ConsoleUtils.writeRed("    desc:       ");
    ConsoleUtils.writeln("Queue description.");
    ConsoleUtils.writeRed("    threshold:  ");
    ConsoleUtils.writeln("Queue threshold.");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Examples:");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Alter queue description:");
    ConsoleUtils.writeln("          KAS/MQ Admin> ALTER QUEUE NAME(APPQ) DESCRIPTION(Applicatino Queue)");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Alter queue threshold and description:");
    ConsoleUtils.writeln("          KAS/MQ Admin> ALTER QUEUE NAME(APPQ) DESCRIPTION(Applicatino Queue) THRESHOLD(1000)");
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
