package com.kas.comm.impl;

import java.net.Socket;
import com.kas.infra.base.AKasObject;

/**
 * An object used to simplify data extraction from {@link Socket} object
 * 
 * @author Pippo
 */
public class NetworkAddress extends AKasObject
{
  /**
   * Host name
   */
  private String mHost;
  
  /**
   * Port number
   */
  private int mPort;
  
  /**
   * Construct a {@link NetworkAddress} using a {@link Socket}
   * 
   * @param socket The socket from which to extract data
   */
  public NetworkAddress(Socket socket)
  {
    this(socket.getInetAddress().getHostName(), socket.getPort());
  }
  
  /**
   * Construct a {@link NetworkAddress} using the host and port
   * 
   * @param host Host name or IP address
   * @param port The port number
   */
  public NetworkAddress(String host, int port)
  {
    mHost = host;
    mPort = port;
  }
  
  /**
   * Get the host name or IP address
   * 
   * @return the host name or IP address
   */
  public String getHost()
  {
    return mHost;
  }
  
  /**
   * Get the port number
   * 
   * @return the port number
   */
  public int getPort()
  {
    return mPort;
  }
  
  /**
   * Get a string representation of the network address
   * 
   * @return string representation of the network address
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder()
      .append(mHost).append(':').append(mPort);
    return sb.toString();
  }
  
  /**
   * Get a detailed string representation of the network address.<br>
   * <br>
   * For {@link NetworkAddress} this is the same output as the {@link #toString()} method.
   * 
   * @return string representation of the network address
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   * @see #toString()
   */
  public String toPrintableString(int level)
  {
    return toString();
  }
}
