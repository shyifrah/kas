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
 * QUERY GROUP command
 * 
 * @author Pippo
 */
public class QueryGroupCommand extends ACommand
{
  /**
   * Group attributes
   */
  private String mName;
  
  /**
   * Construct the command and setting its verbs
   */
  QueryGroupCommand()
  {
    mCommandVerbs.add("QUERY GROUP");
    mCommandVerbs.add("QUERY GRP");
    mCommandVerbs.add("QUERY G");
  }
  
  /**
   * Setting data members
   */
  protected void setup()
  {
    mName = getString("NAME", null);
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
    if (mName.endsWith("*"))
    {
      mName = mName.substring(0, mName.length()-1);
      qprops.setBoolProperty(IMqConstants.cKasPropertyQueryPrefix, true);
    }
    
    if ((mName.length() > 0) && (!Validators.isUserName(mName)))
      throw new IllegalArgumentException("NAME was not specified or invalid: [" + mName + ']');
    
    qprops.setStringProperty(IMqConstants.cKasPropertyQueryGroupName, mName);
    
    MqStringMessage result = conn.queryServer(EQueryType.QUERY_GROUP, qprops);
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
    ConsoleUtils.writeln("     Query a group entity.");
    ConsoleUtils.writeln("     This command will display information on the group identified by NAME.");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Format: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("       >>--QUERY|Q--+--GROUP|GRP|G--+--NAME(name)--><");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Where: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeRed("    name:       ");
    ConsoleUtils.writeln("Group name.");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Examples:");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Query group STARKS:");
    ConsoleUtils.writeln("          KAS/MQ Admin> QUERY GROUP NAME(STARKS)");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Query all groups whose name is prefixed with G01:");
    ConsoleUtils.writeln("          KAS/MQ Admin> Q GRP NAME(G01*)");
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
    sb.append("  GROUP").append('\n')
      .append("    NAME(").append(mName).append(")\n");
    return sb.toString();
  }
}
