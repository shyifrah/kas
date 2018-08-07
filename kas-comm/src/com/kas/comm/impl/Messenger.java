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
  static private ILogger sLogger = LoggerFactory.getLogger(Messenger.class);
  
  /**
   * Socket used to transfer packets back and forth
   */
  protected Socket mSocket;
  
  /**
   * Output stream for writing objects
   */
  protected ObjectOutputStream mOutputStream;
  
  /**
   * Input stream for reading objects
   */
  protected ObjectInputStream  mInputStream;
  
  /**
   * Packet factory for packets creation
   */
  protected IPacketFactory mPacketFactory;
  
  /**
   * Host and port
   */
  protected String   mHost;
  protected int      mPort;
  
  /**
   * Constructs a {@link Messenger} object using the specified socket, host and port.
   * 
   * @param socket The socket that will serve this {@link Messenger}
   * @param host The remote host
   * @param port Remote host listening port
   * @param factory The packet factory used to deserialize packets
   * 
   * @throws IOException if I/O error occurs during streams creation
   */
  Messenger(Socket socket, String host, int port, IPacketFactory factory) throws IOException
  {
    mHost = host;
    mPort = port;
    mSocket = socket;
    mPacketFactory = factory;
    mOutputStream = new ObjectOutputStream(mSocket.getOutputStream());
    mInputStream = new ObjectInputStream(mSocket.getInputStream());
    mSocket.setSoTimeout(0);
  }
  
  /**
   * Messenger cleanup<br>
   * <br>
   * Flushing, closing streams and socket
   * 
   * @see com.kas.comm.IMessenger#cleanup()
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
    }
    catch (IOException e) {}
    
    sLogger.debug("Messenger::cleanup() - OUT");
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
    sLogger.debug("Messenger::send() - IN");
    
    PacketHeader header = packet.createHeader();
    header.serialize(mOutputStream);
    packet.serialize(mOutputStream);
    
    sLogger.debug("Messenger::send() - OUT");
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
    sLogger.debug("Messenger::receive() - IN");
    
    IPacket packet = null;
    mSocket.setSoTimeout(timeout);
    packet = mPacketFactory.createFromStream(mInputStream);
    
    sLogger.debug("Messenger::receive() - OUT");
    return packet;
  }
  
  /**
   * Sends a {@link IPacket} and wait indefinitely for a reply.
   * 
   * @param request A request packet
   * 
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
   * 
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
   * Shutdown the Messenger's Input side.
   * This is achieved by simply closing the socket's input stream.
   */
  public void shutdownInput()
  {
    try
    {
      mInputStream.close();
    }
    catch (Throwable e) {}
  }
  
  /**
   * Return the string representation of this Messenger's remote host
   * 
   * @return the string representation of this Messenger's remote host
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
  
  /**
   * Get a replica of this {@link Messenger}.<br>
   * <br>
   * Note that the socket in the replica object is the same one as in use by this {@link Messenger}
   * 
   * @return a replica of this {@link Messenger} or {@code null} if an exception was thrown
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public Messenger replicate()
  {
    Messenger messenger = null;
  
    try
    {
      messenger = new Messenger(mSocket, mHost, mPort, mPacketFactory);
    }
    catch (IOException e) {}
    
    return messenger;
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
    StringBuffer sb = new StringBuffer();
    sb.append(name()).append("(\n")
      .append(pad).append("  Host=").append(mHost).append("\n")
      .append(pad).append("  Port=").append(mPort).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
