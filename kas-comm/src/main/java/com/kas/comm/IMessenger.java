package com.kas.comm;

import java.io.IOException;
import com.kas.comm.impl.NetworkAddress;
import com.kas.infra.base.IObject;

/**
 * A messenger is the object responsible for sending and receiving {@link IPacket packets} over a socket.
 * 
 * @author Pippo
 */
public interface IMessenger extends IObject
{
  /**
   * Get the messenger connectivity status
   * 
   * @return
   *   {@code true} if connected, {@code false} otherwise
   */
  public abstract boolean isConnected();
  
  /**
   * Get the messenger remote address
   * 
   * @return
   *   the {@link NetworkAddress} that represents the remote host
   */
  public abstract NetworkAddress getAddress();
  
  /**
   * Sends a {@link IPacket} object.
   * 
   * @param message
   *   The packet to send
   * @throws IOException
   *   if an I/O error occurs
   */
  public abstract void send(IPacket packet) throws IOException;
  
  /**
   * Receive a {@link IPacket} object.<br>
   * If a {@link IPacket} is not available, the call will block until one is.
   * 
   * @return
   *   read packet
   * @throws IOException
   *   if an I/O error occurs
   */
  public abstract IPacket receive() throws IOException;
  
  /**
   * Sends a {@link IPacket} and wait indefinitely for a reply.
   * 
   * @param request
   *   A request packet
   * @return
   *   a response packet
   * @throws IOException
   *   if an I/O error occurs
   */
  public abstract IPacket sendAndReceive(IPacket request) throws IOException;
  
  /**
   * Perform messenger's cleanup:<br>
   * Flush streams, close streams, close the socket etc.
   */
  public abstract void cleanup();
}
