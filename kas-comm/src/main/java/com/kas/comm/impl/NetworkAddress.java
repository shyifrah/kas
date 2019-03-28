package com.kas.comm.impl;

import java.net.Socket;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.IObject;
import com.kas.infra.utils.Validators;

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
   * @param socket
   *   The socket from which to extract data
   */
  public NetworkAddress(Socket socket)
  {
    this(socket.getInetAddress().getHostName(), socket.getPort());
  }
  
  /**
   * Construct a {@link NetworkAddress} using the host and port
   * 
   * @param host
   *   Host name or IP address
   * @param port
   *   The port number
   * @throws IllegalArgumentException
   *   if {@code host} doesn't designate a valid host name nor a valid IP address,
   *   or if {@code port} doesn't designate a valid port number
   */
  public NetworkAddress(String host, int port)
  {
    if ((!Validators.isHostName(host))  && (!Validators.isIpAddress(host)))
      throw new IllegalArgumentException("Invalid host name: " + host);
    if (!Validators.isPort(port))
      throw new IllegalArgumentException("Invalid port number: " + port);
    
    mHost = host;
    mPort = port;
  }
  
  /**
   * Get the host name or IP address
   * 
   * @return
   *   the host name or IP address
   */
  public String getHost()
  {
    return mHost;
  }
  
  /**
   * Get the port number
   * 
   * @return
   *   the port number
   */
  public int getPort()
  {
    return mPort;
  }
  
  /**
   * Get the string representation
   * 
   * @return
   *   the string representation
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder()
      .append(mHost).append(':').append(mPort);
    return sb.toString();
  }
  
  /**
   * Returns the {@link IObject} string representation.
   * 
   * @param level
   *   The required padding level
   * @return
   *   the string representation with the specified level of padding
   */
  public String toPrintableString(int level)
  {
    return toString();
  }
}
