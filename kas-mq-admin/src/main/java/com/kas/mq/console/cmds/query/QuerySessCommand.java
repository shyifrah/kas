package com.kas.mq.console.cmds.query;

import com.kas.infra.base.Properties;
import com.kas.infra.base.UniqueId;
import com.kas.infra.utils.ConsoleUtils;
import com.kas.mq.console.ACommand;
import com.kas.mq.impl.EQueryType;
import com.kas.mq.impl.messages.MqStringMessage;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.internal.MqContextConnection;

/**
 * QUERY SESSION command
 * 
 * @author Pippo
 */
public class QuerySessCommand extends ACommand
{
  /**
   * Session attributes
   */
  private String mId;
  
  /**
   * Construct the command and setting its verbs
   */
  QuerySessCommand()
  {
    mCommandVerbs.add("SESSION");
    mCommandVerbs.add("SESS");
  }
  
  /**
   * Setting data members
   */
  protected void setup()
  {
    mId = getString("ID", "ALL");
    mId = mId.toUpperCase();
  }
  
  /**
   * Execute the command using the specified {@link MqContextConnection}
   * 
   * @param conn The {@link MqContextConnection} that will be used to execute the command
   */
  public void exec(MqContextConnection conn)
  {
    Properties qprops = new Properties();
    if (!mId.equals("ALL"))
    {
      try
      {
        UniqueId.fromString(mId);
        qprops.put(IMqConstants.cKasPropertyQuerySessId, mId);
      }
      catch (IllegalArgumentException e)
      {
        throw new IllegalArgumentException("ID does not designate a valid session ID: [" + mId + ']');
      }
    }
    
    MqStringMessage result = conn.queryServer(EQueryType.QUERY_SESSION, qprops);
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
    sb.append("  SESSION").append('\n')
      .append("    ID(").append(mId).append(")\n");
    return sb.toString();
  }
}