package com.kas.mq.console.cmds;

import com.kas.infra.utils.ConsoleUtils;
import com.kas.infra.utils.Validators;
import com.kas.mq.console.ACommand;
import com.kas.mq.internal.MqContextConnection;

/**
 * CONNECT command
 * 
 * @author Pippo
 */
public class ConnectCommand extends ACommand
{
  /**
   * Connect attributes
   */
  private String mHost;
  private int mPort;
  private String mUser;
  private String mPassword;
  
  /**
   * Construct the command and setting its verbs
   */
  ConnectCommand()
  {
    mCommandVerbs.add("CONNECT");
    mCommandVerbs.add("CONN");
  }
  
  /**
   * Setting data members
   */
  protected void setup()
  {
    mHost = getString("HOST", null);
    mPort = getInteger("PORT", 14560);
    mUser = getString("USER", null);
    mPassword = getString("PASSWORD", null);
  }
  
  /**
   * Execute the command using the specified {@link MqContextConnection}
   * 
   * @param conn The {@link MqContextConnection} that will be used to execute the command
   */
  public void exec(MqContextConnection conn)
  {
    try
    {
      if (!Validators.isHostName(mHost))
        throw new IllegalArgumentException("HOST was not specified or invalid host name: [" + mHost + ']');
      if (!Validators.isPort(mPort))
        throw new IllegalArgumentException("PORT was not specified or invalid port: [" + mPort + ']');
      
      conn.connect(mHost, mPort);
      if (!conn.isConnected())
      {
        ConsoleUtils.writeln("%s", conn.getResponse());
        return;
      }
      
      if (mUser == null)
        mUser = ConsoleUtils.readClearText("Enter user name: ");
      
      if (!Validators.isUserName(mUser))
        throw new IllegalArgumentException("USER was not specified or invalid user name: [" + mUser + ']');
      
      if (mPassword == null)
        mPassword = ConsoleUtils.readMaskedText("Enter password: ");
      
      conn.login(mUser, mPassword);
      ConsoleUtils.writeln("%s", conn.getResponse());
    }
    catch (Throwable e)
    {
      if (conn.isConnected())
        conn.disconnect();
      throw e;
    }
  }
  
  /**
   * Get the command text
   * 
   * @return the command text
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("CONNECT").append('\n')
      .append(" HOST(").append(mHost).append(")\n")
      .append(" PORT(").append(mPort).append(")\n");
    
    if (mUser != null)
      sb.append(" USER(").append(mUser).append(")\n");
    if (mPassword != null)
      sb.append(" PASSWORD(").append(mPassword).append(")\n");
    
    return sb.toString();
  }
}
