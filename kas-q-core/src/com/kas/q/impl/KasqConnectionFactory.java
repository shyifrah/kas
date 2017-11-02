package com.kas.q.impl;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
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
  
  public Connection createConnection() throws JMSException
  {
    try
    {
      return new KasqConnection(mHost, mPort);
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "Failed to create Connection", e);
    }
  }

  public Connection createConnection(String userName, String password) throws JMSException
  {
    try
    {
      return new KasqConnection(mHost, mPort, userName, password);
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught", "Failed to create Connection", e);
    }
  }
  
  public JMSContext createContext()
  {
    return null;
  }

  public JMSContext createContext(String userName, String password)
  {
    // Unsupported method: ConnectionFactory.createContext(String, String)
    return null;
  }

  public JMSContext createContext(String userName, String password, int sessionMode)
  {
    // Unsupported method: ConnectionFactory.createContext(String, String, int)
    return null;
  }

  public JMSContext createContext(int sessionMode)
  {
    // Unsupported method: ConnectionFactory.createContext(int)
    return null;
  }
  
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    
    sb.append(name()).append("(\n")
      .append(pad).append("  Host=").append(mHost).append("\n")
      .append(pad).append("  Port=").append(mPort).append("\n")
      .append(pad).append(")");
    
    return sb.toString();
  }
}
