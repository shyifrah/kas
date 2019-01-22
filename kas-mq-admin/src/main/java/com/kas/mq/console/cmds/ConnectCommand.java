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
        throw new IllegalArgumentException("HOST was not specified or invalid: [" + mHost + ']');
      if (!Validators.isPort(mPort))
        throw new IllegalArgumentException("Invalid PORT: [" + mPort + ']');
      
      conn.connect(mHost, mPort);
      if (!conn.isConnected())
      {
        ConsoleUtils.writeln("%s", conn.getResponse());
        return;
      }
      
      if (mUser == null)
        mUser = ConsoleUtils.readClearText("Enter user name: ");
      
      if (!Validators.isUserName(mUser))
        throw new IllegalArgumentException("USER was not specified or invalid: [" + mUser + ']');
      
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
   * Print HELP screen for the specified command.
   */
  public void help()
  {
    ConsoleUtils.writelnGreen("Purpose: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Connect to a host - IP address or host name - on a specific port.");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Format: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("                                         +--PORT(14560)--+");
    ConsoleUtils.writeln("                                         |               |");
    ConsoleUtils.writeln("     >>--CONNECT|CONN--+--HOST(host)--+--+---------------+--+--------------+--+----------------------+--><");
    ConsoleUtils.writeln("                       |              |  |               |  |              |  |                      |");
    ConsoleUtils.writeln("                       +--HOST(ip)----+  +--PORT(port)---+  +--USER(user)--+  +--PASSWORD(password)--+");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Description: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Connect to the specified host or IP address on the specified port number, or 14560 if no port is specified.");
    ConsoleUtils.writeln("     If user/password are not specified, the user will be propmpted for its user name and password.");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Where: ");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeRed("    host/ip:  ");
    ConsoleUtils.writeln("The host name or IP address of the KAS/MQ server");
    ConsoleUtils.writeRed("    port:     ");
    ConsoleUtils.writeln("The port number on which the KAS/MQ server listens");
    ConsoleUtils.writeRed("    user:     ");
    ConsoleUtils.writeln("The user name to be used for authentication");
    ConsoleUtils.writeRed("    password: ");
    ConsoleUtils.writeln("The user's password");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writelnGreen("Examples:");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Connect to host name TLVHOSTA on default port number:");
    ConsoleUtils.writeln("          KAS/MQ Admin> CONNECT HOST(TLVHOSTA)");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Connect with user ROOT to host with IP address of 101.23.2.2 on default port number:");
    ConsoleUtils.writeln("          KAS/MQ Admin> CONN HOST(101.23.2.2) USER(root)");
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln("     Connect to host name LONDON1 on port number 24560");
    ConsoleUtils.writeln("          KAS/MQ Admin> CONNECT HOST(LONDON1) PORT(24560)");
    ConsoleUtils.writeln(" ");
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
      .append("  HOST(").append(mHost).append(")\n")
      .append("  PORT(").append(mPort).append(")\n");
    if (mUser != null)
      sb.append("  USER(").append(mUser).append(")\n");
    if (mPassword != null)
      sb.append("  PASSWORD(").append(mPassword).append(")\n");
    return sb.toString();
  }
}
