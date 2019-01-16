package com.kas.mq.console.cmds.query;

import com.kas.infra.base.Properties;
import com.kas.infra.utils.ConsoleUtils;
import com.kas.infra.utils.Validators;
import com.kas.mq.console.ACommand;
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
   * @param conn The {@link MqContextConnection} that will be used to execute the command
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
   * Get the command text
   * 
   * @return the command text
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("QUERY QUEUE").append('\n')
      .append(" NAME(").append(mName).append(")\n")
      .append(" ALLDATA(").append(mAllData).append(")\n");
    return sb.toString();
  }
}
