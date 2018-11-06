package com.kas.mq.impl;

import java.net.Socket;
import com.kas.infra.base.IObject;
import com.kas.infra.base.UniqueId;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.MqConnection;

public interface IMqConnection extends IObject
{
  /**
   * Connect client to the KAS/MQ server.<br>
   * <br>
   * If the {@link MqConnection} is already connected, it will be disconnected first.
   * 
   * @param host The host name or IP address (uppercased)
   * @param port The port number
   */
  public abstract void connect(String host, int port);

  /**
   * Disconnecting from the remote KAS/MQ server.
   * <br>
   * First we verify the client is actually connected, otherwise there's no point in disconnecting.<br>
   * Note we allocate a new {@link Socket} following the call to {@link Socket#close() close()} because
   * a closed socket cannot be reused.
   */
  public abstract void disconnect();
  
  /**
   * Get the connection status.<br>
   * <br>
   * A connected socket is one that is marked as connected AND not closed.
   * 
   * @return {@code true} if socket is connected and not closed, {@code false} otherwise
   * 
   * @see java.net.Socket#isConnected()
   */
  public abstract boolean isConnected();
  
  /**
   * login to KAS/MQ server.
   * 
   * @param user The user's name
   * @param pwd The user's password
   * @return {@code true} if {@code password} matches the user's password
   */
  public abstract boolean login(String user, String pwd);
  
  /**
   * Get a message from queue.
   * 
   * @param queue The target queue name
   * @param timeout The number of milliseconds to wait until a message available
   * @param interval The number in milliseconds the thread execution is suspended between each polling operation
   * @return the {@link IMqMessage} object or {@code null} if a message is unavailable
   */
  public abstract IMqMessage get(String queue, long timeout, long interval);
  
  /**
   * Put a message into the specified queue.
   * 
   * @param queue The target queue name
   * @param message The message to be put
   * @return the {@link IMqMessage} returned by the messenger
   */
  public abstract IMqMessage put(String queue, IMqMessage message);
  
  /**
   * Get the connection ID
   * 
   * @return the connection ID
   */
  public abstract UniqueId getConnectionId();
  
  /**
   * Get last response from last {@link IClient} call.
   * 
   * @return the last message the {@link IClient} issued for a call.
   */
  public abstract String getResponse();
  
  /**
   * Set response from last {@link IClient} call.
   * 
   * @param response The response from last {@link IClient} call
   */
  public abstract void setResponse(String response);  
}
