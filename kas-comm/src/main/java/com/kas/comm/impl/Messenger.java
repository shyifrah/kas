package com.kas.comm.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kas.comm.IPacket;
import com.kas.comm.IMessenger;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.IObject;
import com.kas.infra.base.KasException;

/**
 * A messenger object is the basic implementation of the {@link IMessenger} 
 * 
 * @author Pippo
 */
public class Messenger extends AKasObject implements IMessenger
{
  /**
   * Logger
   */
  private Logger mLogger;
  
  /**
   * Socket used to transfer packets back and forth
   */
  protected Socket mSocket;
  
  /**
   * Output and Input streams for writing and reading objects
   */
  protected ObjectOutputStream mOutputStream;
  protected ObjectInputStream  mInputStream;
  
  /**
   * Host and port
   */
  protected NetworkAddress mAddress;
  
  /**
   * Constructs a {@link Messenger} object using the specified host and port.
   * 
   * @param host
   *   The remote host
   * @param port
   *   Remote host listening port
   * @throws IOException
   *   if I/O error occurs during streams creation
   */
  Messenger(String host, int port) throws IOException
  {
    this(new Socket(host, port));
  }
  
  /**
   * Constructs a {@link Messenger} object using the specified host, port and timeout.
   * 
   * @param host
   *   The remote host
   * @param port
   *   Remote host listening port
   * @param timeout
   *   The socket read timeout
   * @throws IOException
   *   if I/O error occurs during streams creation
   */
  Messenger(String host, int port, int timeout) throws IOException
  {
    this(new Socket(host, port), timeout);
  }
  
  /**
   * Constructs a {@link Messenger} object using the specified socket.
   * 
   * @param socket
   *   The socket that will serve this {@link Messenger}
   * @throws IOException
   *   if I/O error occurs during streams creation
   */
  Messenger(Socket socket) throws IOException
  {
    this(socket, 0);
  }
  
  /**
   * Constructs a {@link Messenger} object using the specified socket and timeout.
   * 
   * @param socket
   *   The socket that will serve this {@link Messenger}
   * @param timeout
   *   The socket read timeout
   * @throws IOException
   *   if I/O error occurs during streams creation
   */
  Messenger(Socket socket, int timeout) throws IOException
  {
    if (socket == null) throw new IOException("Null socket");
    
    mLogger = LogManager.getLogger(getClass());
    
    mSocket = socket;
    mAddress = new NetworkAddress(socket);
    
    mOutputStream = new ObjectOutputStream(mSocket.getOutputStream());
    mInputStream = new ObjectInputStream(mSocket.getInputStream());
    mSocket.setSoTimeout(timeout);
  }
  
  /**
   * Get the messenger connectivity status
   * 
   * @return
   *   {@code true} if connected, {@code false} otherwise
   * 
   * @see com.kas.comm.IMessenger#isConnected()
   * @see java.net.Socket#isConnected()
   */
  public boolean isConnected()
  {
    return mSocket == null ? false : mSocket.isConnected() && !mSocket.isClosed();
  }
  
  /**
   * Get the messenger remote address
   * 
   * @return
   *   the {@link NetworkAddress} that represents the remote host
   */
  public NetworkAddress getAddress()
  {
    return new NetworkAddress(mSocket);
  }
  
  /**
   * Sends a {@link IPacket} object.
   * 
   * @param message
   *   The packet to send
   * @throws IOException
   *   if an I/O error occurs
   * 
   * @see com.kas.comm.IMessenger#send(IPacket)
   */
  public void send(IPacket packet) throws IOException
  {
    mLogger.trace("Messenger::send() - IN");
    
    if (mOutputStream == null) throw new IOException("Null output stream; Messenger is probably not connected");
    
    try
    {
      PacketHeader header = packet.createHeader();
      header.serialize(mOutputStream);
      packet.serialize(mOutputStream);
    }
    catch (SocketException e)
    {
      mLogger.trace("Connection was reset: ", e);
      cleanup();
      throw e;
    }
    
    mLogger.trace("Messenger::send() - OUT");
  }

  /**
   * Receive a {@link IPacket} object.<br>
   * If a {@link IPacket} is not available, the call will block until one is.<br>
   * 
   * @return
   *   read packet
   * @throws IOException
   *   if an I/O error occurs
   * 
   * @see #receive(int)
   */
  public IPacket receive() throws IOException
  {
    mLogger.trace("Messenger::receive() - IN");
    
    if (mInputStream == null) throw new IOException("Null input stream; Messenger is probably not connected");
    
    IPacket packet = null;
    try
    {
      PacketHeader header = new PacketHeader(mInputStream);
      packet = header.read(mInputStream);
    }
    catch (SocketException e)
    {
      mLogger.trace("Connection was reset: ", e);
      cleanup();
      throw e;
    }
    catch (KasException e)
    {
      mLogger.warn("An error occurred while trying to read packet from input stream. Exception: ", e);
      cleanup();
    }
    
    mLogger.trace("Messenger::receive() - OUT");
    return packet;
  }
  
  /**
   * Sends a {@link IPacket} and wait indefinitely for a reply.
   * 
   * @param request
   *   A request packet
   * @return
   *   a response packet
   * 
   * @throws IOException
   *   if an I/O error occurs
   */
  public IPacket sendAndReceive(IPacket request) throws IOException
  {
    send(request);
    return receive();
  }
  
  /**
   * Perform messenger's cleanup:<br>
   * Flush streams, close streams, close the socket etc.
   */
  public void cleanup()
  {
    mLogger.trace("Messenger::cleanup() - IN");
    
    if (!isConnected())
    {
      mLogger.trace("Messenger::cleanup() - Messenger not connected, nothing to cleanup");
    }
    else
    {
      mLogger.trace("Messenger::cleanup() - Flushing and closing streams, closing the socket...");
      try
      {
        mOutputStream.flush();
      }
      catch (IOException e) {}
      
      try
      {
        mOutputStream.close();
      }
      catch (IOException e) {}
      
      try
      {
        mInputStream.close();
      }
      catch (IOException e) {}
      
      try
      {
        mSocket.close();
      }
      catch (IOException e) {}
      
      mOutputStream = null;
      mInputStream = null;
      mSocket = null;
    }
    
    mLogger.trace("Messenger::cleanup() - OUT");
  }
  
  /**
   * Return the string representation.
   * 
   * @return
   *   the string representation
   */
  public String toString()
  {
    return mAddress.toString();
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
    String pad = pad(level);
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n")
      .append(pad).append("  Host=").append(mAddress.getHost()).append("\n")
      .append(pad).append("  Port=").append(mAddress.getPort()).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
