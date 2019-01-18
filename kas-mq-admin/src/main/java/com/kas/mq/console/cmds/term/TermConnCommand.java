package com.kas.mq.console.cmds.term;

import com.kas.infra.base.UniqueId;
import com.kas.infra.utils.ConsoleUtils;
import com.kas.mq.console.ACommand;
import com.kas.mq.internal.MqContextConnection;

/**
 * TERMINATE CONNECTION command
 * 
 * @author Pippo
 */
public class TermConnCommand extends ACommand
{
  /**
   * Connections attributes
   */
  private String mId;
  
  /**
   * Construct the command and setting its verbs
   */
  TermConnCommand()
  {
    mCommandVerbs.add("CONNECTION");
    mCommandVerbs.add("CONN");
  }
  
  /**
   * Setting data members
   */
  protected void setup()
  {
    mId = getString("ID", null);
    if (mId != null) mId = mId.toUpperCase();
  }
  
  /**
   * Execute the command using the specified {@link MqContextConnection}
   * 
   * @param conn The {@link MqContextConnection} that will be used to execute the command
   */
  public void exec(MqContextConnection conn)
  {
    UniqueId uuid = null;
    try
    {
      uuid = UniqueId.fromString(mId);
    }
    catch (Throwable e) {}
    
    if (uuid == null)
      throw new IllegalArgumentException("ID was not specified or invalid: [" + mId + ']');
    
    conn.termConn(uuid);
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
