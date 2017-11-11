package com.kas.q;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import com.kas.infra.base.KasObject;

public class KasqConnectionFactory extends KasObject implements ConnectionFactory
{
  protected String mHost;
  protected int    mPort;
  
  /***************************************************************************************************************
   * Constructs a {@code KasqConnectionFactory} object to the specified host/port
   * 
   * @param host hostname or IP-address of the KasQ server
   * @param port port number on which the KasQ server listens for new connections
   */
  public KasqConnectionFactory(String host, int port)
  {
    mHost = host;
    mPort = port;
  }
  
  /***************************************************************************************************************
   * 
   */
  public Connection createConnection() throws JMSException
  {
    return new KasqConnection(mHost, mPort);
  }

  /***************************************************************************************************************
   * 
   */
  public Connection createConnection(String userName, String password) throws JMSException
  {
    return new KasqConnection(mHost, mPort, userName, password);
  }
  
  /***************************************************************************************************************
   * 
   */
  public JMSContext createContext()
  {
    return null;
  }

  public JMSContext createContext(String userName, String password)
  {
    // Unsupported method: ConnectionFactory.createContext(String, String)
    return null;
  }

  /***************************************************************************************************************
   * 
   */
  public JMSContext createContext(String userName, String password, int sessionMode)
  {
    // Unsupported method: ConnectionFactory.createContext(String, String, int)
    return null;
  }

  /***************************************************************************************************************
   * 
   */
  public JMSContext createContext(int sessionMode)
  {
    // Unsupported method: ConnectionFactory.createContext(int)
    return null;
  }
  
  /***************************************************************************************************************
   * 
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    sb.append(pad).append("  Host=").append(mHost).append("\n")
      .append(pad).append("  Port=").append(mPort).append("\n");
    return sb.toString();
  }
}
