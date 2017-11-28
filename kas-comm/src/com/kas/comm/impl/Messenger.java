package com.kas.comm.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import com.kas.comm.IPacket;
import com.kas.comm.IPacketFactory;
import com.kas.comm.IMessenger;
import com.kas.infra.base.AKasObject;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;

public class Messenger extends AKasObject implements IMessenger
{
  /***************************************************************************************************************
   * 
   */
  private static ILogger sLogger = LoggerFactory.getLogger(Messenger.class);
  
  /***************************************************************************************************************
   * 
   */
  protected Socket   mSocket;
  protected ObjectOutputStream mOutputStream;
  protected ObjectInputStream  mInputStream;
  
  protected IPacketFactory    mPacketFactory;
  
  protected boolean  mConnected;
  protected String   mHost;
  protected int      mPort;
  
  /***************************************************************************************************************
   * Constructs a {@code Messenger} object using the specified socket, host and port.
   * 
   * @param socket the socket that will serve this {@code Messenger} object
   * @param host the remote host
   * @param port remote host listening port
   * @param factory the packet factory used to deserialize packets 
   * 
   * @throws IOException
   */
  Messenger(Socket socket, String host, int port, IPacketFactory factory) throws IOException
  {
    mHost = host;
    mPort = port;
    mSocket = socket;
    mConnected = true;
    mPacketFactory = factory;
    mOutputStream = new ObjectOutputStream(mSocket.getOutputStream());
    mInputStream = new ObjectInputStream(mSocket.getInputStream());
    mSocket.setSoTimeout(0);
  }
  
  /***************************************************************************************************************
   * 
   */
  public void cleanup()
  {
    sLogger.debug("Messenger::cleanup() - IN");
    
    try
    {
      mOutputStream.flush();
      mOutputStream.close();
      mInputStream.close();
      mSocket.close();
      mConnected = false;
    }
    catch (IOException e) {}
    
    sLogger.debug("Messenger::cleanup() - OUT");
  }
  
  /***************************************************************************************************************
   * 
   */
  public void send(IPacket packet) throws IOException
  {
    sLogger.debug("Messenger::send() - IN");
    
    verifyConnected();
    
    PacketHeader header = packet.createHeader();
    header.serialize(mOutputStream);
    packet.serialize(mOutputStream);
    
    sLogger.debug("Messenger::send() - OUT");
  }

  /***************************************************************************************************************
   * 
   */
  public IPacket receive() throws IOException
  {
    return receive(0);
  }

  /***************************************************************************************************************
   * 
   */
  public IPacket receive(int timeout) throws IOException
  {
    sLogger.debug("Messenger::receive() - IN");
    
    verifyConnected();
    
    IPacket packet = null;
    mSocket.setSoTimeout(timeout);
    packet = mPacketFactory.createFromStream(mInputStream);
    
    sLogger.debug("Messenger::receive() - OUT");
    return packet;
  }
  
  /***************************************************************************************************************
   * 
   */
  public IPacket sendAndReceive(IPacket request) throws IOException
  {
    send(request);
    return receive();
  }

  /***************************************************************************************************************
   *  
   */
  public IPacket sendAndReceive(IPacket request, int timeout) throws IOException
  {
    send(request);
    return receive(timeout);
  }
  
  public void shutdownInput() throws IOException
  {
    try
    {
      mInputStream.close();
    }
    catch (Throwable e)
    {
      e.printStackTrace();
    }
  }
  
  /***************************************************************************************************************
   * Verify the messenger's state
   * 
   * @throws IllegalStateException if the messenger is not connected
   */
  private void verifyConnected() throws IllegalStateException
  {
    if (!mConnected)
      throw new IllegalStateException("Messenger not connected");
  }
  
  /***************************************************************************************************************
   * 
   */
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append(name())
      .append("(Address=[")
      .append(mHost)
      .append(':')
      .append(mPort)
      .append("])");
    return sb.toString();
  }
  
  /***************************************************************************************************************
   * 
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    sb.append(name()).append("(\n")
      .append(pad).append("  Host=").append(mHost).append("\n")
      .append(pad).append("  Port=").append(mPort).append("\n")
      .append(pad).append("  Connected=").append(mConnected).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
