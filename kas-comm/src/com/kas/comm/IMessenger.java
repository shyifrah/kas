package com.kas.comm;

import java.io.IOException;
import com.kas.infra.base.IObject;

public interface IMessenger extends IObject
{
  /***************************************************************************************************************
   * Perform cleanup: flush stream, close them, close the socket etc.
   * 
   */
  public abstract void cleanup();
  
  /***************************************************************************************************************
   * Sends a {@code IPacket} object.
   * 
   * @param message the {@code IPacket} to send
   * 
   * @throws IOException
   */
  public abstract void send(IPacket packet) throws IOException;
  
  /***************************************************************************************************************
   * Receive a {@code IPacket} object.
   * If a packet is not available, the call will block until a packet is available.
   * 
   * @return read packet
   * 
   * @throws IOException
   */
  public abstract IPacket receive() throws IOException;
  
  /***************************************************************************************************************
   * Receive a {@code IPacket} object.
   * If a packet is not available, wait for {@code timeout} milliseconds for one to be available
   * 
   * @param timeout milliseconds to wait for the reply
   * 
   * @return the read packet or {@code null} if one is not available
   * 
   * @throws IOException
   */
  public abstract IPacket receive(int timeout) throws IOException;
  
  /***************************************************************************************************************
   * Sends a {@code IPacket} and wait indefinitely for a reply.
   * 
   * @param request a request packet
   * 
   * @return response packet
   * 
   * @throws IOException
   */
  public abstract IPacket sendAndReceive(IPacket request) throws IOException;
  
  /***************************************************************************************************************
   * Sends a {@code IPacket} and wait for a reply.
   * 
   * @param request a request packet
   * @param timeout milliseconds to wait for the reply
   * 
   * @return response packet or null if timeout expires
   * 
   * @throws IOException
   */
  public abstract IPacket sendAndReceive(IPacket request, int timeout) throws IOException;
}
