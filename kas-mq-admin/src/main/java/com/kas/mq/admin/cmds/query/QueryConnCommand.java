package com.kas.mq.admin.cmds.query;

import com.kas.infra.base.UniqueId;
import com.kas.infra.utils.ConsoleUtils;
import com.kas.mq.admin.ACommand;
import com.kas.mq.impl.messages.MqStringMessage;
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
   * @param conn
   *   The {@link MqContextConnection} that will be used to execute the command
   */
  public void exec(MqContextConnection conn)
  {
    if (!mId.equals("ALL"))
    {
      try
      {
        UniqueId.fromString(mId);
      }
      catch (IllegalArgumentException e)
      {
        throw new IllegalArgumentException("ID does not designate a valid connection ID: [" + mId + ']');
      }
    }
    
    MqStringMessage result = conn.queryConn(mId);
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
    ConsoleUtils.writeln("     Query a connection entity.");
    ConsoleUtils.writeln("     This command will display information on a connection identified by ID.");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Format: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("                                        +--ID(ALL)--+");
    ConsoleUtils.writeln("                                        |           |");
    ConsoleUtils.writeln("       >>--QUERY|Q--+--CONNECTION|CONN--+-----------+--><");
    ConsoleUtils.writeln("                                        |           |");
    ConsoleUtils.writeln("                                        +--ID(id)---+");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Where: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeRed("    id:         ");
    ConsoleUtils.writeln("Connection ID or ALL.");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Examples:");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Query all connections:");
    ConsoleUtils.writeln("          KAS/MQ Admin> QUERY CONN");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Query connection with ID 01234567-0123-1234-2345-0123456789ab:");
    ConsoleUtils.writeln("          KAS/MQ Admin> Q CONNECTION ID(01234567-0123-1234-2345-0123456789ab)");
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
