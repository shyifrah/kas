package com.kas.comm.impl;

import java.net.Socket;
import com.kas.infra.base.AKasObject;

public class NetworkAddress extends AKasObject
{
  private String mHost;
  private int mPort;
  
  public NetworkAddress(Socket socket)
  {
    this(socket.getInetAddress().getHostName(), socket.getPort());
  }
  
  public NetworkAddress(String host, int port)
  {
    mHost = host;
    mPort = port;
  }
  
  public String getHost()
  {
    return mHost;
  }
  
  public int getPort()
  {
    return mPort;
  }
  
  public String toString()
  {
    StringBuilder sb = new StringBuilder()
      .append(mHost).append(':').append(mPort);
    return sb.toString();
  }
  
  public String toPrintableString(int level)
  {
    return toString();
  }
}
