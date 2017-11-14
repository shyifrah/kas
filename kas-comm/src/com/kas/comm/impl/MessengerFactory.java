package com.kas.comm.impl;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import com.kas.comm.IPacketFactory;

public class MessengerFactory
{
  /***************************************************************************************************************
   * Create a {@code Messenger} object bound to the specified host and port.
   * 
   * This method is intended for use by client-side applications
   * 
   * @param host the remote host
   * @param port remote host listening port
   * @param factory the {@code IMessageFactory} which will be used to deserialize messages
   * 
   * @throws UnknownHostException
   * @throws IOException
   */
  public static Messenger create(String host, int port, IPacketFactory factory) throws UnknownHostException, IOException
  {
    return new Messenger(new Socket(host, port), host, port, factory);
  }
  
  /***************************************************************************************************************
   * Create a {@code Messenger} object with an active socket. 
   * 
   * This method is intended for use by server-side applications.
   * 
   * @param socket an active socket
   * @param factory the {@code IMessageFactory} which will be used to deserialize messages
   * 
   * @throws IOException
   */
  public static Messenger create(Socket socket, IPacketFactory factory) throws IOException
  {
    return new Messenger(socket, socket.getInetAddress().getHostName(), socket.getPort(), factory);
  }
}
