package com.kas.comm;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.net.SocketException;
import com.kas.infra.base.IObject;

public interface IMessenger extends IObject
{
  /***************************************************************************************************************
   * Perform cleanup: flush stream, close them, close the socket etc.
   * 
   */
  public abstract void cleanup();
  
  /***************************************************************************************************************
   * Sends a {@code IMessage} object.
   * 
   * @param message the {@code IMessage} to send
   * 
   * @throws IOException
   */
  public abstract void send(IMessage message) throws IOException;
  
  /***************************************************************************************************************
   * Receive a {@code IMessage} object.
   * If a message is not available, the call will block until a message is available.
   * 
   * @return read message
   * 
   * @throws StreamCorruptedException
   * @throws SocketException
   */
  public abstract IMessage receive() throws StreamCorruptedException, SocketException;
  
  /***************************************************************************************************************
   * Receive a {@code IMessage} object.
   * If a message is not available, wait for {@code timeout} milliseconds for one to be available
   * 
   * @param timeout milliseconds to wait for the reply
   * 
   * @return the read message or {@code null} if one is not available
   * 
   * @throws StreamCorruptedException
   * @throws SocketException
   */
  public abstract IMessage receive(int timeout) throws StreamCorruptedException, SocketException;
  
  /***************************************************************************************************************
   * Sends a {@code IMessage} and wait indefinitely for a reply.
   * 
   * @param request a request message
   * 
   * @return response message
   * 
   * @throws IOException
   * @throws StreamCorruptedException
   * @throws SocketException
   */
  public abstract IMessage sendAndReceive(IMessage request) throws IOException, StreamCorruptedException, SocketException;
  
  /***************************************************************************************************************
   * Sends a {@code IMessage} and wait for a reply.
   * 
   * @param request a request message
   * @param timeout milliseconds to wait for the reply
   * 
   * @return response message or null if timeout expires
   * 
   * @throws IOException
   * @throws StreamCorruptedException
   * @throws SocketException
   */
  public abstract IMessage sendAndReceive(IMessage request, int timeout) throws IOException, StreamCorruptedException, SocketException;
}
