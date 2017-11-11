package com.kas.comm.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.net.SocketException;
import com.kas.comm.IMessage;
import com.kas.comm.IMessenger;
import com.kas.infra.base.KasObject;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;

public class Messenger extends KasObject implements IMessenger
{
  /***************************************************************************************************************
   * 
   */
  protected ILogger  mLogger;
  protected Socket   mSocket;
  protected ObjectOutputStream mOutputStream;
  protected ObjectInputStream  mInputStream;
  protected boolean  mConnected;
  protected String   mHost;
  protected int      mPort;
  
  /***************************************************************************************************************
   * Constructs a {@code Messenger} object using the specified socket, host and port.
   * 
   * @param socket the socket that will serve this {@code Messenger} object
   * @param host the remote host
   * @param port remote host listening port
   * 
   * @throws IOException
   */
  Messenger(Socket socket, String host, int port) throws IOException
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mHost = host;
    mPort = port;
    mSocket = socket;
    mConnected = true;
    mOutputStream = new ObjectOutputStream(mSocket.getOutputStream());
    mInputStream = new ObjectInputStream(mSocket.getInputStream());
  }
  
  /***************************************************************************************************************
   * Do cleanup before destroying this object
   * 
   */
  public void cleanup()
  {
    mLogger.debug("Messenger::cleanup() - IN");
    
    try
    {
      mOutputStream.flush();
      mOutputStream.close();
      mInputStream.close();
      mSocket.close();
      mConnected = false;
    }
    catch (IOException e) {}
    
    mLogger.debug("Messenger::cleanup() - OUT");
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
  public void send(IMessage message) throws IOException
  {
    MessageSerializer.serialize(mOutputStream, message);
  }

  /***************************************************************************************************************
   * 
   */
  public IMessage receive() throws StreamCorruptedException, SocketException
  {
    mSocket.setSoTimeout(0);
    return MessageSerializer.deserialize(mInputStream);
  }

  /***************************************************************************************************************
   * 
   */
  public IMessage receive(int timeout) throws StreamCorruptedException, SocketException
  {
    IMessage message = null;
    mSocket.setSoTimeout(timeout);
    try
    {
      message = MessageSerializer.deserialize(mInputStream);
    }
    catch (Throwable e) {}
    
    return message;
  }

  /***************************************************************************************************************
   * 
   */
  public IMessage sendAndReceive(IMessage request) throws IOException, StreamCorruptedException
  {
    send(request);
    return receive();
  }

  /***************************************************************************************************************
   *  
   */
  public IMessage sendAndReceive(IMessage request, int timeout) throws IOException, StreamCorruptedException
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
