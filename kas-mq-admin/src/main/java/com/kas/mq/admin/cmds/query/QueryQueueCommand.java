package com.kas.mq.admin.cmds.query;

import com.kas.infra.base.Properties;
import com.kas.infra.utils.ConsoleUtils;
import com.kas.infra.utils.Validators;
import com.kas.mq.admin.ACommand;
import com.kas.mq.impl.EQueryType;
import com.kas.mq.impl.messages.MqStringMessage;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.internal.MqContextConnection;

/**
 * QUERY QUEUE command
 * 
 * @author Pippo
 */
public class QueryQueueCommand extends ACommand
{
  /**
   * Queue attributes
   */
  private String mName;
  private boolean mAllData;
  
  /**
   * Construct the command and setting its verbs
   */
  QueryQueueCommand()
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
    if (mName != null) mName = mName.toUpperCase();
    mAllData = getBoolean("ALLDATA", false);
  }
  
  /**
   * Execute the command using the specified {@link MqContextConnection}
   * 
   * @param conn
   *   The {@link MqContextConnection} that will be used to execute the command
   */
  public void exec(MqContextConnection conn)
  {
    Properties qprops = new Properties();
    qprops.setBoolProperty(IMqConstants.cKasPropertyQueryFormatOutput, true);
    if (mName.endsWith("*"))
    {
      mName = mName.substring(0, mName.length()-1);
      qprops.setBoolProperty(IMqConstants.cKasPropertyQueryPrefix, true);
    }
    
    if ((mName.length() > 0) && (!Validators.isQueueName(mName)))
      throw new IllegalArgumentException("NAME was not specified or invalid: [" + mName + ']');
    
    qprops.setStringProperty(IMqConstants.cKasPropertyQueryQueueName, mName);
    qprops.setBoolProperty(IMqConstants.cKasPropertyQueryAllData, mAllData);
    
    MqStringMessage result = conn.queryServer(EQueryType.QUERY_QUEUE, qprops);
    if (result != null)
      ConsoleUtils.writeln("%s", result.getBody());
    ConsoleUtils.writeln("%s", conn.getResponse());
  }
  
  /**
   * Display help screen for this command.
   */
  public void help()
  {
    ConsoleUtils.writelnGreen("Purpose: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Query a queue entity.");
    ConsoleUtils.writeln("     This command will display information on the queue identified by NAME.");
    ConsoleUtils.writeln("     The level of details is controlled by the ALLDATA attribute.");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Format: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("                                               +--ALLDATA(FALSE)--+");
    ConsoleUtils.writeln("                                               |                  |");
    ConsoleUtils.writeln("       >>--QUERY|Q--+--QUEUE|Q--+--NAME(name)--+------------------+--><");
    ConsoleUtils.writeln("                                               |                  |");
    ConsoleUtils.writeln("                                               +--ALLDATA(TRUE)---+");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Where: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeRed("    name:       ");
    ConsoleUtils.writeln("Queue name.");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Examples:");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Query basic information on queue APPQ1:");
    ConsoleUtils.writeln("          KAS/MQ Admin> QUERY QUEUE NAME(APPQ1)");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Query extended information on queue APPQ2:");
    ConsoleUtils.writeln("          KAS/MQ Admin> Q Q NAME(APPQ2) ALLDATA(TRUE)");
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
      .append("    ALLDATA(").append(mAllData).append(")\n");
    return sb.toString();
  }
}
