package com.kas.mq.admin.cmds.term;

import com.kas.infra.base.UniqueId;
import com.kas.infra.utils.ConsoleUtils;
import com.kas.mq.admin.ACommand;
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
   * @param conn
   *   The {@link MqContextConnection} that will be used to execute the command
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
   * Display help screen for this command.
   */
  public void help()
  {
    ConsoleUtils.writelnGreen("Purpose: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Terminate a connection.");
    ConsoleUtils.writeln("     This command will terminate a connection identified with ID.");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Format: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("       >>--TERMINATE|TERM|TRM--+--CONNECTION|CONN--+--ID(id)--+--><");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Where: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeRed("    id:         ");
    ConsoleUtils.writeln("Connection ID.");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Examples:");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Terminate connection with ID 01234567-0123-1234-2345-0123456789ab:");
    ConsoleUtils.writeln("          KAS/MQ Admin> TERMINATE CONNECTION ID(01234567-0123-1234-2345-0123456789ab)");
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
    sb.append("  CONNECTION").append('\n')
      .append("    ID(").append(mId).append(")\n");
    return sb.toString();
  }
}
