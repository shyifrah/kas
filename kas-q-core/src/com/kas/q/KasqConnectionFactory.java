package com.kas.q;

import java.util.ArrayList;
import java.util.List;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import com.kas.infra.base.AKasObject;
import com.kas.infra.utils.StringUtils;

public class KasqConnectionFactory extends AKasObject implements ConnectionFactory
{
  protected String mHost;
  protected int    mPort;
  
  protected List<KasqConnection> mConnList;
  
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
    
    mConnList = new ArrayList<KasqConnection>();
  }
  
  /***************************************************************************************************************
   * 
   */
  public Connection createConnection() throws JMSException
  {
    KasqConnection conn = new KasqConnection(mHost, mPort);
    synchronized (mConnList)
    {
      mConnList.add(conn);
    }
    return conn;
  }

  /***************************************************************************************************************
   * 
   */
  public Connection createConnection(String userName, String password) throws JMSException
  {
    KasqConnection conn = new KasqConnection(mHost, mPort, userName, password);
    synchronized (mConnList)
    {
      mConnList.add(conn);
    }
    return conn;
  }
  
  /***************************************************************************************************************
   * 
   */
  public JMSContext createContext()
  {
    // TODO: Unsupported method: ConnectionFactory.createContext()
    return null;
  }

  /***************************************************************************************************************
   * 
   */
  public JMSContext createContext(String userName, String password)
  {
    // TODO: Unsupported method: ConnectionFactory.createContext(String, String)
    return null;
  }

  /***************************************************************************************************************
   * 
   */
  public JMSContext createContext(String userName, String password, int sessionMode)
  {
    // TODO: Unsupported method: ConnectionFactory.createContext(String, String, int)
    return null;
  }

  /***************************************************************************************************************
   * 
   */
  public JMSContext createContext(int sessionMode)
  {
    // TODO: Unsupported method: ConnectionFactory.createContext(int)
    return null;
  }
  
  /***************************************************************************************************************
   * 
   */
  public void shutdown()
  {
    for (KasqConnection conn : mConnList)
    {
      try
      {
        conn.close();
      }
      catch (JMSException e) {}
    }
  }
  
  /***************************************************************************************************************
   * 
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    sb.append(pad).append("  Host=").append(mHost).append("\n")
      .append(pad).append("  Port=").append(mPort).append("\n")
      .append(pad).append("  Connections=(")
      .append(StringUtils.asPrintableString(mConnList, level+2))
      .append(pad).append("  )");
    return sb.toString();
  }
}
