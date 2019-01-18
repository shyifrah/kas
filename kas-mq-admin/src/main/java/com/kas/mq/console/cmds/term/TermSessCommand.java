package com.kas.mq.console.cmds.term;

import com.kas.infra.base.UniqueId;
import com.kas.infra.utils.ConsoleUtils;
import com.kas.mq.console.ACommand;
import com.kas.mq.internal.MqContextConnection;

/**
 * TERMINATE SESSION command
 * 
 * @author Pippo
 */
public class TermSessCommand extends ACommand
{
  /**
   * Session attributes
   */
  private String mId;
  
  /**
   * Construct the command and setting its verbs
   */
  TermSessCommand()
  {
    mCommandVerbs.add("SESSION");
    mCommandVerbs.add("SESS");
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
    
    conn.termSess(uuid);
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
