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
 * QUERY CONNECTION command
 * 
 * @author Pippo
 */
public class QueryConnCommand extends ACommand
{
  /**
   * Connections attributes
   */
  private String mId;
  
  /**
   * Construct the command and setting its verbs
   */
  QueryConnCommand()
  {
    mCommandVerbs.add("CONNECTION");
    mCommandVerbs.add("CONN");
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
        qprops.put(IMqConstants.cKasPropertyQueryConnId, mId);
      }
      catch (IllegalArgumentException e)
      {
        throw new IllegalArgumentException("ID does not designate a valid connection ID: [" + mId + ']');
      }
    }
    
    MqStringMessage result = conn.queryServer(EQueryType.QUERY_CONNECTION, qprops);
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
    sb.append("  CONNECTION").append('\n')
      .append("    ID(").append(mId).append(")\n");
    return sb.toString();
  }
}
