package com.kas.comm.impl;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class MessengerFactory
{
  /***************************************************************************************************************
   * Create a {@code Messenger} object bound to the specified host and port.
   * 
   * This method is intended for use by client-side applications
   * 
   * @param host the remote host
   * @param port remote host listening port
   * 
   * @throws UnknownHostException
   * @throws IOException
   */
  public static Messenger create(String host, int port) throws UnknownHostException, IOException
  {
    return new Messenger(new Socket(host, port), host, port);
  }
  
  /***************************************************************************************************************
   * Create a {@code Messenger} object with an active socket. 
   * 
   * This method is intended for use by server-side applications.
   * 
   * @param socket an active socket
   * 
   * @throws IOException
   */
  public static Messenger create(Socket socket) throws IOException
  {
    return new Messenger(socket, socket.getInetAddress().getHostName(), socket.getPort());
  }
}
