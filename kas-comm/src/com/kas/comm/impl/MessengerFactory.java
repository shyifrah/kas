package com.kas.comm.impl;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import com.kas.comm.IPacketFactory;

/**
 * A factory for creating {@link Messenger} objects
 * 
 * @author Pippo
 */
public class MessengerFactory
{
  /**
   * Create a {@link Messenger} object bound to the specified host and port.<br>
   * <br>
   * This method is intended for use by client-side applications
   * 
   * @param host The remote host
   * @param port Remote host listening port
   * @param factory The {@link IMessageFactory} which will be used to deserialize messages
   * @return a newly-created {@link Messenger} object
   * 
   * @throws UnknownHostException if failed to create {@link Socket}
   * @throws IOException if an I/O error occurs
   */
  static public Messenger create(String host, int port, IPacketFactory factory) throws UnknownHostException, IOException
  {
    return new Messenger(new Socket(host, port), host, port, factory);
  }
  
  /**
   * Create a {@link Messenger} object with an active socket.<br> 
   * <br>
   * This method is intended for use by server-side applications.
   * 
   * @param socket An active socket
   * @param factory The {@link IMessageFactory} which will be used to deserialize messages
   * @return a newly-created {@link Messenger} object
   * 
   * @throws IOException if an I/O error occurs
   */
  static public Messenger create(Socket socket, IPacketFactory factory) throws IOException
  {
    return new Messenger(socket, socket.getInetAddress().getHostName(), socket.getPort(), factory);
  }
}
