package com.kas.comm.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import com.kas.comm.IPacket;
import com.kas.comm.IMessenger;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.KasException;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;

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
  private ILogger mLogger;
  
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
   * Constructs a {@link Messenger} object using the specified socket, host and port.
   * 
   * @param host The remote host
   * @param port Remote host listening port
   * 
   * @throws IOException if I/O error occurs during streams creation
   */
  Messenger(String host, int port) throws IOException
  {
    this(new Socket(host, port));
  }
  
  /**
   * Constructs a {@link Messenger} object using the specified socket, host and port.
   * 
   * @param socket The socket that will serve this {@link Messenger}
   * 
   * @throws IOException if I/O error occurs during streams creation
   */
  Messenger(Socket socket) throws IOException
  {
    if (socket == null) throw new IOException("Null socket");
    
    mLogger = LoggerFactory.getLogger(this.getClass());
    
    mSocket = socket;
    mAddress = new NetworkAddress(socket);
    
    mOutputStream = new ObjectOutputStream(mSocket.getOutputStream());
    mInputStream = new ObjectInputStream(mSocket.getInputStream());
    mSocket.setSoTimeout(0);
  }
  
  /**
   * Get the messenger connectivity status
   * 
   * @return {@code true} if connected, {@code false} otherwise
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
   * @return The {@link NetworkAddress} that represents the remote host
   * 
   * @see com.kas.comm.IMessenger#getAddress()
   */
  public NetworkAddress getAddress()
  {
    return new NetworkAddress(mSocket);
  }
  
  /**
   * Sends a {@link IPacket} object.
   * 
   * @param message The packet to send
   * 
   * @throws IOException if an I/O error occurs
   * 
   * @see com.kas.comm.IMessenger#send(IPacket)
   */
  public void send(IPacket packet) throws IOException
  {
    mLogger.diag("Messenger::send() - IN");
    
    if (mOutputStream == null) throw new IOException("Null output stream; Messenger is probably not connected");
    
    try
    {
      PacketHeader header = packet.createHeader();
      header.serialize(mOutputStream);
      packet.serialize(mOutputStream);
    }
    catch (SocketException e)
    {
      mLogger.diag("Connection was reset: ", e);
      cleanup();
      throw e;
    }
    
    mLogger.diag("Messenger::send() - OUT");
  }

  /**
   * Receive a {@link IPacket} object.<br>
   * <br>
   * If a {@link IPacket} is not available, the call will block until one is.<br>
   * 
   * @return read packet
   * 
   * @throws IOException if an I/O error occurs
   * 
   * @see com.kas.comm.IMessenger#receive()
   * @see #receive(int)
   */
  public IPacket receive() throws IOException
  {
    return receive(0);
  }

  /**
   * Receive a {@link IPacket} object.<br>
   * <br>
   * If a {@link IPacket} is not available, wait for {@code timeout} milliseconds for one to be available.
   * 
   * @param timeout Milliseconds to wait for the {@link IPacket} before returning {@code null}. A value of {@code 0}
   * means there is no timeout.
   * @return the read packet or {@code null} if one is not available
   * 
   * @throws IOException if an I/O error occurs
   * 
   * @see com.kas.comm.IMessenger#receive(int)
   */
  public IPacket receive(int timeout) throws IOException
  {
    mLogger.diag("Messenger::receive() - IN");
    
    if (mInputStream == null) throw new IOException("Null input stream; Messenger is probably not connected");
    
    IPacket packet = null;
    try
    {
      mSocket.setSoTimeout(timeout);
      PacketHeader header = new PacketHeader(mInputStream);
      packet = header.read(mInputStream);
    }
    catch (SocketException e)
    {
      mLogger.diag("Connection was reset: ", e);
      cleanup();
      throw e;
    }
    catch (KasException e)
    {
      mLogger.warn("An error occurred while trying to read packet from input stream. Exception: ", e);
      cleanup();
    }
    
    mLogger.diag("Messenger::receive() - OUT");
    return packet;
  }
  
  /**
   * Sends a {@link IPacket} and wait indefinitely for a reply.
   * 
   * @param request A request packet
   * @return a response packet
   * 
   * @throws IOException if an I/O error occurs
   * 
   * @see com.kas.comm.IMessenger#send(IPacket)
   * @see com.kas.comm.IMessenger#receive()
   */
  public IPacket sendAndReceive(IPacket request) throws IOException
  {
    send(request);
    return receive();
  }

  /**
   * Sends a {@link IPacket} and wait for a reply.
   * 
   * @param request A request packet
   * @param timeout Milliseconds to wait for the reply
   * @return response packet or null if timeout expires
   * 
   * @throws IOException if an I/O error occurs
   * 
   * @see com.kas.comm.IMessenger#send(IPacket)
   * @see com.kas.comm.IMessenger#receive(int)
   * @see #receive(int)
   */
  public IPacket sendAndReceive(IPacket request, int timeout) throws IOException
  {
    send(request);
    return receive(timeout);
  }
  
  /**
   * Perform messenger's cleanup:<br>
   * <br>
   * Flush streams, close streams, close the socket etc.
   * 
   * @see com.kas.comm.IMessenger#cleanup()
   */
  public void cleanup()
  {
    mLogger.debug("Messenger::cleanup() - IN");
    
    if (!isConnected())
    {
      mLogger.debug("Messenger::cleanup() - Messenger not connected, nothing to cleanup");
    }
    else
    {
      mLogger.debug("Messenger::cleanup() - Flushing and closing streams, closing the socket...");
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
    
    mLogger.debug("Messenger::cleanup() - OUT");
  }
  
  /**
   * Return the string representation of this Messenger's remote host
   * 
   * @return the string representation of this Messenger's remote host
   */
  public String toString()
  {
    return mAddress.toString();
  }
  
  /**
   * Returns the {@link Messenger} detailed string representation.
   * 
   * @param level the required level padding
   * @return the object's printable string representation
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
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
