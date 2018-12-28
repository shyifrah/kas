package com.kas.mq.console.cmds;

import com.kas.infra.utils.ConsoleUtils;
import com.kas.infra.utils.Validators;
import com.kas.mq.console.ACommand;
import com.kas.mq.internal.MqContextConnection;

/**
 * CONNECT HOST command
 * 
 * @author Pippo
 */
public class ConnectHostCommand extends ACommand
{
  /**
   * Connect attributes
   */
  private String mHost;
  private int mPort;
  private String mUser;
  private String mPassword;
  
  /**
   * Construct the command
   * 
   * @param verb The command verb
   * @param args The argument string
   */
  public ConnectHostCommand(String verb, String args)
  {
    super(verb, args);
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
   * Verify mandatory arguments
   */
  protected void verify()
  {
    if (!Validators.isHostName(mHost))
      throw new IllegalArgumentException("HOST was not specified or invalid host name: [" + mHost + ']');
    if (!Validators.isPort(mPort))
      throw new IllegalArgumentException("Invalid port number: " + mPort);
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
      sb.append(" USER(").append(mHost).append(")\n");
    if (mPassword != null)
      sb.append(" PASSWORD(").append(mPassword).append(")\n");
    
    return sb.toString();
  }
}
