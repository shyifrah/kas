package com.kas.mq.console.cmds.delete;

import com.kas.infra.utils.ConsoleUtils;
import com.kas.infra.utils.Validators;
import com.kas.mq.console.ACommand;
import com.kas.mq.internal.MqContextConnection;

/**
 * DELETE QUEUE command
 * 
 * @author Pippo
 */
public class DeleteQueueCommand extends ACommand
{
  /**
   * Queue attributes
   */
  private String mName;
  private Boolean mForce;
  
  /**
   * Construct the command and setting its verbs
   */
  DeleteQueueCommand()
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
    mForce = getBoolean("FORCE", false);
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
    
    conn.deleteQueue(mName, mForce);
    ConsoleUtils.writeln("%s", conn.getResponse());
  }
  
  /**
   * Display help screen for this command.
   */
  public void help()
  {
    ConsoleUtils.writelnGreen("Purpose: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Delete a queue entity.");
    ConsoleUtils.writeln("     The NAME argument identifies the queue to be deleted. If FORCE(false) is specified or defaulted,");
    ConsoleUtils.writeln("     the delete operation will fail in case the queue is not empty.");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Format: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("                                                  +--FORCE(FALSE)--+");
    ConsoleUtils.writeln("                                                  |                |");
    ConsoleUtils.writeln("       >>--DELETE|DEL--+--QUEUE|Q--+--NAME(name)--+----------------+--><");
    ConsoleUtils.writeln("                                                  |                |");
    ConsoleUtils.writeln("                                                  +--FORCE(TRUE)---+");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Where: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeRed("    name:       ");
    ConsoleUtils.writeln("User name.");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Examples:");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Delete queue named APPQ:");
    ConsoleUtils.writeln("          KAS/MQ Admin> DELETE Q NAME(APPQ)");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Forcely delete queue named TEMP1:");
    ConsoleUtils.writeln("          KAS/MQ Admin> DELETE Q NAME(APPQ) FORCE(true)");
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
      .append("    FORCE(").append(mForce).append(")\n");
    return sb.toString();
  }
}
