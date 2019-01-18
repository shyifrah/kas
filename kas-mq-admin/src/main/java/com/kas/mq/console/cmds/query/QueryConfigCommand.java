package com.kas.mq.console.cmds.query;

import com.kas.infra.base.Properties;
import com.kas.infra.utils.ConsoleUtils;
import com.kas.mq.console.ACommand;
import com.kas.mq.impl.EQueryConfigType;
import com.kas.mq.impl.EQueryType;
import com.kas.mq.impl.messages.MqStringMessage;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.internal.MqContextConnection;

/**
 * QUERY CONFIG command
 * 
 * @author Pippo
 */
public class QueryConfigCommand extends ACommand
{
  /**
   * Config attributes
   */
  private EQueryConfigType mType;
  
  /**
   * Construct the command and setting its verbs
   */
  QueryConfigCommand()
  {
    mCommandVerbs.add("CONFIGURATION");
    mCommandVerbs.add("CONFIG");
    mCommandVerbs.add("CONF");
    mCommandVerbs.add("CFG");
  }
  
  /**
   * Setting data members
   */
  protected void setup()
  {
    mType = getEnum("TYPE", EQueryConfigType.class, EQueryConfigType.ALL);
  }
  
  /**
   * Execute the command using the specified {@link MqContextConnection}
   * 
   * @param conn The {@link MqContextConnection} that will be used to execute the command
   */
  public void exec(MqContextConnection conn)
  {
    Properties qprops = new Properties();
    qprops.setStringProperty(IMqConstants.cKasPropertyQueryConfigType, mType.name());
    
    MqStringMessage result = conn.queryServer(EQueryType.QUERY_CONFIG, qprops);
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
    sb.append("  CONFIGURATION").append('\n')
      .append("    TYPE(").append(mType.name()).append(")\n");
    return sb.toString();
  }
}
