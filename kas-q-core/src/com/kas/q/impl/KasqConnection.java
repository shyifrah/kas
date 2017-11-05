package com.kas.q.impl;

import java.io.IOException;
import java.net.UnknownHostException;
import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;
import com.kas.infra.base.KasObject;
import com.kas.q.ext.IDestination;
import com.kas.q.ext.IMessage;
import com.kas.q.ext.impl.MessageSerializer;
import com.kas.q.ext.impl.Messenger;

public class KasqConnection extends KasObject implements Connection
{
  Messenger mMessenger = null;
  boolean   mStarted   = false;
  String    mClientId  = null;
  
  /***************************************************************************************************************
   * Constructs a Connection object to the specified host/port combination, using the default user identity
   * 
   * @param host hostname or IP-address of the KasQ server
   * @param port port number on which the KasQ server listens for new connections
   * 
   * @throws UnknownHostException
   * @throws IOException
   */
  KasqConnection(String host, int port) throws UnknownHostException, IOException
  {
    mMessenger = Messenger.Factory.create(host, port);
  }
  
  /***************************************************************************************************************
   * Constructs a Connection object to the specified host/port combination, using the specified user identity
   * 
   * @param host hostname or IP-address of the KasQ server
   * @param port port number on which the KasQ server listens for new connections
   * @param userName the caller's user name
   * @param password the caller's password
   * 
   * @throws UnknownHostException
   * @throws IOException
   */
  KasqConnection(String host, int port, String userName, String password) throws UnknownHostException, IOException
  {
    mMessenger = Messenger.Factory.create(host, port, userName, password);
  }
  
  public void start()
  {
    mStarted = true;
  }

  public void stop()
  {
    mStarted = false;
  }
  
  public Session createSession() throws JMSException
  {
    return new KasqSession(this);
  }

  public Session createSession(boolean transacted, int acknowledgeMode) throws JMSException
  {
    return new KasqSession(this, transacted, acknowledgeMode);
  }

  public Session createSession(int sessionMode) throws JMSException
  {
    return new KasqSession(this, sessionMode);
  }

  public String getClientID() throws JMSException
  {
    return mClientId;
  }

  public void setClientID(String clientID) throws JMSException
  {
    mClientId = clientID;
  }

  public ConnectionMetaData getMetaData() throws JMSException
  {
    throw new JMSException("Unsupported method: Connection.getMetaData()");
  }

  public ExceptionListener getExceptionListener() throws JMSException
  {
    throw new JMSException("Unsupported method: Connection.getExceptionListener()");
  }

  public void setExceptionListener(ExceptionListener listener) throws JMSException
  {
    throw new JMSException("Unsupported method: Connection.setExceptionListener(ExceptionListener)");
  }

  public void close() throws JMSException
  {
    throw new JMSException("Unsupported method: Connection.close()");
  }

  public ConnectionConsumer createConnectionConsumer(Destination destination, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException
  {
    throw new JMSException("Unsupported method: Connection.createConnectionConsumer(destination, String, ServerSessionPool, int)");
  }

  public ConnectionConsumer createSharedConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException
  {
    throw new JMSException("Unsupported method: Connection.createSharedConnectionConsumer(Topic, String, String, ServerSessionPool, int)");
  }

  public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException
  {
    throw new JMSException("Unsupported method: Connection.createDurableConnectionConsumer(Topic, String, String, ServerSessionPool, int)");
  }

  public ConnectionConsumer createSharedDurableConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException
  {
    throw new JMSException("Unsupported method: Connection.createSharedDurableConnectionConsumer(Topic, String, String, ServerSessionPool, int)");
  }
  
  /***************************************************************************************************************
   * Sends a {@code IMessage}.
   * 
   * Since a {@code Connection} object must support concurrent use, we must synchronize the use
   * of the Messenger.
   * 
   * @param message the message to be sent
   * 
   * @throws IOException 
   */
  synchronized void send(IMessage message) throws IOException
  {
    MessageSerializer.serialize(mMessenger.getOutputStream(), message);
  }
  
  /***************************************************************************************************************
   * Receives a {@code IMessage}.
   * 
   * Since a {@code Connection} object must support concurrent use, we must synchronize the use
   * of the Messenger.
   * 
   * @param message the message to be sent
   * 
   * @throws IOException 
   * @throws ClassNotFoundException 
   */
  synchronized IMessage recv(IDestination destination) throws IOException, ClassNotFoundException
  {
    if (mStarted)
    {
      // send a message to the KasqServer requesting to listen on messages for the Destination
      // call a recv() on the messenger to wait for the arrival of a message
      // note: this means that it is possible the recv() call will block, meaning preventing the Connection
      //       from serving other consumers/producers at the same time...
      //       the more "correct" way is that each consumer/producer has its own messenger.
    }
    return null;
  }
  
  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    
    sb.append(name()).append("(\n")
      .append(pad).append("  Started=").append(mStarted).append("\n")
      .append(pad).append("  ClientId=").append(mClientId).append("\n")
      .append(pad).append("  Messenger=(").append(mMessenger.toPrintableString(level + 2)).append(")\n")
      .append(pad).append(")");
    
    return sb.toString();
  }
}
