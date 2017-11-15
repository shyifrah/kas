package com.kas.q;

import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;

public class KasqQueueConnectionFactory extends KasqConnectionFactory implements QueueConnectionFactory
{
  /***************************************************************************************************************
   * Constructs a {@code KasqQueueConnectionFactory} object to the specified host/port
   * 
   * @param host hostname or IP-address of the KasQ server
   * @param port port number on which the KasQ server listens for new connections
   */
  public KasqQueueConnectionFactory(String host, int port)
  {
    super(host, port);
  }
  
  /***************************************************************************************************************
   * 
   */
  public QueueConnection createQueueConnection() throws JMSException
  {
    try
    {
      return new KasqQueueConnection(mHost, mPort);
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught ", "KasqQueueConnection(" + mHost + ", " + mPort + ")", e);
    }
  }

  /***************************************************************************************************************
   * 
   */
  public QueueConnection createQueueConnection(String userName, String password) throws JMSException
  {
    try
    {
      return new KasqQueueConnection(mHost, mPort, userName, password);
    }
    catch (Exception e)
    {
      throw new JMSRuntimeException("Exception caught ", "KasqQueueConnection(" + mHost + ", " + mPort + ", " + userName + ", " + password + ")", e);
    }
  }
  
  /***************************************************************************************************************
   * 
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    sb.append(name()).append("(\n")
      .append(super.toPrintableString(level))
      .append(pad).append(")");
    return sb.toString();
  }
}
