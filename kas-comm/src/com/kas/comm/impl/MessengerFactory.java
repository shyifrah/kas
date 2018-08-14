package com.kas.comm.impl;

import java.io.IOException;
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
   * <br>
   * This method is intended for use by server-side applications.
   * 
   * @param socket An active socket
   * @return a newly-created {@link Messenger} object
   * 
   * @throws IOException if an I/O error occurs
   */
  static public Messenger create(Socket socket) throws IOException
  {
    return new Messenger(socket, socket.getInetAddress().getHostName(), socket.getPort());
  }
}
