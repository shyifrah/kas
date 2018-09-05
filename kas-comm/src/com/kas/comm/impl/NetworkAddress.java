package com.kas.comm.impl;

import java.net.Socket;
import com.kas.infra.base.AKasObject;
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
   * Construct a {@link NetworkAddress} using the string
   * 
   * @param str A string in the format of {@code <host>:<port>}
   * 
   * @throws NullPointerException if {@code str} is {@code null}
   * @throws IllegalArgumentException if {@code str} contains invalid host/port values
   */
  public NetworkAddress(String str)
  {
    if (str == null)
      throw new NullPointerException("Cannot construct a NetworkAddress object from null string");
    String [] vals = str.split(":");
    if (vals.length != 2)
      throw new IllegalArgumentException("Invalid argument format: " + str);
    
    String host = vals[0];
    if ((!Validators.isHostName(host)) && (!Validators.isIpAddress(host)))
      throw new IllegalArgumentException("Invalid host name/IP address: " + host);
    
    String sport = vals[1];
    int port = -1;
    try
    {
      port = Integer.valueOf(sport.toUpperCase());
    }
    catch (NumberFormatException e)
    {
      throw new IllegalArgumentException("Invalid port number: " + sport);
    }
    
    if (!Validators.isPort(port))
      throw new IllegalArgumentException("Invalid port number: " + port);
   
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
