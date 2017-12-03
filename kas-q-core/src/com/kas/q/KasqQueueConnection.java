package com.kas.q;

import javax.jms.ConnectionConsumer;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.jms.ServerSessionPool;
import javax.jms.Session;

public class KasqQueueConnection extends KasqConnection implements QueueConnection
{
  /***************************************************************************************************************
   * Constructs a {@code KasQueueConnection} object to the specified host/port combination, using the default user identity
   * 
   * @param host hostname or IP-address of the KasQ server
   * @param port port number on which the KasQ server listens for new connections
   * 
   * @throws JMSException 
   */
  KasqQueueConnection(String host, int port) throws JMSException
  {
    super(host, port);
  }
  
  /***************************************************************************************************************
   * Constructs a {@code KasQueueConnection} object to the specified host/port combination, using the specified user identity
   * 
   * @param host hostname or IP-address of the KasQ server
   * @param port port number on which the KasQ server listens for new connections
   * @param userName the caller's user name
   * @param password the caller's password
   * 
   * @throws JMSException 
   */
  KasqQueueConnection(String host, int port, String userName, String password) throws JMSException
  {
    super(host, port, userName, password);
  }

  /***************************************************************************************************************
   *  
   */
  public Session createSession() throws JMSException
  {
    KasqSession sess = new KasqSession(this);
    synchronized (mSessions)
    {
      mSessions.add(sess);
    }
    return sess;
  }

  /***************************************************************************************************************
   *  
   */
  public QueueSession createQueueSession(boolean transacted, int acknowledgeMode) throws JMSException
  {
    KasqQueueSession sess = new KasqQueueSession(this, transacted, acknowledgeMode);
    synchronized (mSessions)
    {
      mSessions.add(sess);
    }
    return sess;
  }

  /***************************************************************************************************************
   *  
   */
  public ConnectionConsumer createConnectionConsumer(Queue queue, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException
  {
    // TODO: implement
    throw new JMSException("Unsupported method: QueueConnection.createConnectionConsumer(Queue, String, ServerSessionPool, int)");
  }
  
  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    return super.toPrintableString(level);
  }
}
