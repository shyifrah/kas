package com.kas.comm;

import java.io.IOException;
import com.kas.infra.base.IObject;

/**
 * A messenger is the object responsible for sending and receiving {@link IPacket packets} over a socket.
 * 
 * @author Pippo
 */
public interface IMessenger extends IObject
{
  /**
   * Perform messenger's cleanup:<br>
   * <br>
   * Flush streams, close streams, close the socket etc.
   */
  public abstract void cleanup();
  
  /**
   * Sends a {@link IPacket} object.
   * 
   * @param message The packet to send
   * 
   * @throws IOException if an I/O error occurs
   */
  public abstract void send(IPacket packet) throws IOException;
  
  /**
   * Receive a {@link IPacket} object.<br>
   * <br>
   * If a {@link IPacket} is not available, the call will block until one is.
   * 
   * @return read packet
   * 
   * @throws IOException if an I/O error occurs
   */
  public abstract IPacket receive() throws IOException;
  
  /**
   * Receive a {@link IPacket} object.<br>
   * <br>
   * If a {@link IPacket} is not available, wait for {@code timeout} milliseconds for one to be available.
   * 
   * @param timeout Milliseconds to wait for the {@link IPacket} before returning {@code null}
   * 
   * @return the read packet or {@code null} if one is not available
   * 
   * @throws IOException if an I/O error occurs
   */
  public abstract IPacket receive(int timeout) throws IOException;
  
  /**
   * Sends a {@link IPacket} and wait indefinitely for a reply.
   * 
   * @param request A request packet
   * 
   * @return a response packet
   * 
   * @throws IOException if an I/O error occurs
   */
  public abstract IPacket sendAndReceive(IPacket request) throws IOException;
  
  /**
   * Sends a {@link IPacket} and wait for a reply.
   * 
   * @param request A request packet
   * @param timeout Milliseconds to wait for the reply
   * 
   * @return response packet or null if timeout expires
   * 
   * @throws IOException if an I/O error occurs
   */
  public abstract IPacket sendAndReceive(IPacket request, int timeout) throws IOException;
  
  /**
   * Shutdown the Messenger's Input side.
   * This is achieved by simply closing the socket's input stream.
   */
  public abstract void shutdownInput();
}
