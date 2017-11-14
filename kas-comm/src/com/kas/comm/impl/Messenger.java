package com.kas.comm.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.net.SocketException;
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
   * Get the {@code ObjectOutputStream} that wraps the socket's OutputStream
   * 
   * @return the {@code ObjectOutputStream}
   */
  public ObjectOutputStream getOutputStream()
  {
    return mOutputStream;
  }
  
  /***************************************************************************************************************
   * Get the {@code ObjectInputStream} that wraps the socket's InputStream
   * 
   * @return the {@code ObjectInputStream}
   */
  public ObjectInputStream getInputStream()
  {
    return mInputStream;
  }
  
  /***************************************************************************************************************
   * Get the {@code Messenger}'s status.
   * 
   * @return the {@code Messenger}'s status
   */
  public boolean isConnected()
  {
    return mConnected;
  }
  
  /***************************************************************************************************************
   * 
   */
  public void send(IPacket packet) throws IOException
  {
    PacketHeader header = packet.createHeader();
    header.serialize(mOutputStream);
    packet.serialize(mOutputStream);
  }

  /***************************************************************************************************************
   * 
   */
  public IPacket receive() throws StreamCorruptedException, SocketException
  {
    mSocket.setSoTimeout(0);
    IPacket packet = mPacketFactory.createFromStream(mInputStream);
    return packet;
  }

  /***************************************************************************************************************
   * 
   */
  public IPacket receive(int timeout) throws StreamCorruptedException, SocketException
  {
    IPacket packet = null;
    mSocket.setSoTimeout(timeout);
    try
    {
      packet= mPacketFactory.createFromStream(mInputStream);
    }
    catch (Throwable e) {}
    
    return packet;
  }

  /***************************************************************************************************************
   * 
   */
  public IPacket sendAndReceive(IPacket request) throws IOException, StreamCorruptedException
  {
    send(request);
    return receive();
  }

  /***************************************************************************************************************
   *  
   */
  public IPacket sendAndReceive(IPacket request, int timeout) throws IOException, StreamCorruptedException
  {
    send(request);
    return receive(timeout);
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
      .append(pad).append(")\n");
    
    return sb.toString();
  }
}
