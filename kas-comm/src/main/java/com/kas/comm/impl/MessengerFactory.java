package com.kas.comm.impl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A factory for creating {@link Messenger} objects
 * 
 * @author Pippo
 */
public class MessengerFactory
{
  /**
   * Create a {@link Messenger} object with an active socket.<br> 
   * This method is intended for use by server-side applications where
   * a new socket was created as a result of calling {@link ServerSocket#accept() accept()}
   * 
   * @param socket
   *   An active socket
   * @return
   *   a newly-created {@link Messenger} object
   * @throws IOException
   *   if an I/O error occurs
   */
  static public Messenger create(Socket socket) throws IOException
  {
    return new Messenger(socket);
  }
  
  /**
   * Create a {@link Messenger} object with an active socket.<br> 
   * This method is intended for use by server-side applications where
   * a new socket was created as a result of calling {@link ServerSocket#accept() accept()}
   * 
   * @param socket
   *   An active socket
   * @param timeout
   *   The socket's read timeout. This timeout enables SO_TIMEOUT option
   * @return
   *   a newly-created {@link Messenger} object
   * @throws IOException
   *   if an I/O error occurs
   */
  static public Messenger create(Socket socket, int timeout) throws IOException
  {
    return new Messenger(socket, timeout);
  }
  
  /**
   * Create a {@link Messenger} object with an active socket.<br> 
   * This method is intended for use by client-side applications where the socket
   * was not created yet, and the {@link Messenger} object will be the one to establish
   * the connection.
   * 
   * @param socket
   *   An active socket
   * @return
   *   a newly-created {@link Messenger} object
   * @throws IOException
   *   if an I/O error occurs
   */
  static public Messenger create(String host, int port) throws IOException
  {
    return new Messenger(host, port);
  }
}
