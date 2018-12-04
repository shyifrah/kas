package com.kas.mq.impl.messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.kas.comm.impl.PacketHeader;
import com.kas.comm.serializer.Deserializer;
import com.kas.comm.serializer.EClassId;
import com.kas.infra.base.KasException;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.internal.ABaseBytesMessage;

/**
 * A KAS/MQ message with a stream of primitives payload.<br>
 * <br>
 * The message body is a byte []
 * 
 * @author Pippo
 */
public final class MqStreamMessage extends ABaseBytesMessage
{
  static
  {
    Deserializer.getInstance().register(MqStreamMessage.class, EClassId.cClassMqStreamMessage);
  }
  
  private transient ByteArrayInputStream  mBais = null;
  private transient ObjectInputStream     mOis  = null;
  private transient ByteArrayOutputStream mBaos = null;
  private transient ObjectOutputStream    mOos  = null;
  private transient boolean mReadOnly = false;
  
  /**
   * Construct a default bytes message object
   */
  MqStreamMessage()
  {
    super();
    mBody = null;
    setWriteOnly();
  }
  
  /**
   * Constructs a {@link MqStreamMessage} object from {@link ObjectInputStream}
   * 
   * @param istream The {@link ObjectInputStream}
   * 
   * @throws IOException if I/O error occurs
   */
  public MqStreamMessage(ObjectInputStream istream) throws IOException
  {
    super(istream);
    setReadOnly();
  }
  
  /**
   * Serialize the {@link MqStreamMessage} to the specified {@link ObjectOutputStream}
   * 
   * @param ostream The {@link ObjectOutputStream} to which the message will be serialized
   * 
   * @throws IOException if an I/O error occurs
   * 
   * @see com.kas.infra.base.ISerializable#serialize(ObjectOutputStream)
   */
  public void serialize(ObjectOutputStream ostream) throws IOException
  {
    setReadOnly();
    super.serialize(ostream);
  }
  
  /**
   * Return the message's state
   * 
   * @return {@code true} if message can only be read from, {@code false} if the message
   * can only be written to.
   */
  private boolean isReadOnly()
  {
    return mReadOnly;
  }
  
  /**
   * Set the message's state to read only
   */
  private void setReadOnly()
  {
    try
    {
      mOos.flush();
      mBaos.flush();
      mOos.close();
      mBaos.close();
      mBody = mBaos.toByteArray();
    }
    catch (Throwable e) {}
    
    mBais = new ByteArrayInputStream(mBody);
    try
    {
      mOis = new ObjectInputStream(mBais);
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
    mReadOnly = true;
  }
  
  /**
   * Set the message's state to write only
   */
  private void setWriteOnly()
  {
    try
    {
      mOis.close();
      mBais.close();
    }
    catch (Throwable e) {}
    
    mBaos = new ByteArrayOutputStream();
    try
    {
      mOos = new ObjectOutputStream(mBaos);
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
    mReadOnly = false;
  }
  
  /**
   * Reads a boolean from the stream message.
   * 
   * @return read boolean value from the stream
   * @throws KasException if Message is not in read only mode or an IOException was thrown
   */
  public boolean readBoolean() throws KasException
  {
    boolean result;
    if (!isReadOnly()) throw new KasException("Message not readable");
    
    try
    {
      result = mOis.readBoolean();
    }
    catch (IOException e)
    {
      throw new KasException(e);
    }
    return result;
  }
  
  /**
   * Reads a byte value from the stream message.
   * 
   * @return read byte value from the stream
   * @throws KasException if Message is not in read only mode or an IOException was thrown
   */
  public byte readByte() throws KasException
  {
    byte result;
    if (!isReadOnly()) throw new KasException("Message not readable");
    
    try
    {
      result = mOis.readByte();
    }
    catch (IOException e)
    {
      throw new KasException(e);
    }
    return result;
  }
  
  /**
   * Reads a byte array field from the stream message into the specified byte[] object (the read buffer).
   * 
   * @param value The read buffer
   * @return the number of bytes read
   * @throws KasException if Message is not in read only mode or an IOException was thrown
   */
  public int readBytes(byte [] value) throws KasException
  {
    int result;
    if (!isReadOnly()) throw new KasException("Message not readable");
    
    try
    {
      result = mOis.read(value);
    }
    catch (IOException e)
    {
      throw new KasException(e);
    }
    return result;
  }
  
  /**
   * Reads a Unicode character value from the stream message.
   * 
   * @return read char value from the stream
   * @throws KasException if Message is not in read only mode or an IOException was thrown
   */
  public char readChar() throws KasException
  {
    char result;
    if (!isReadOnly()) throw new KasException("Message not readable");
    
    try
    {
      result = mOis.readChar();
    }
    catch (IOException e)
    {
      throw new KasException(e);
    }
    return result;
  }
  
  /**
   * Reads a double from the stream message.
   * 
   * @return read double value from the stream
   * @throws KasException if Message is not in read only mode or an IOException was thrown
   */
  public double readDouble() throws KasException
  {
    double result;
    if (!isReadOnly()) throw new KasException("Message not readable");
    
    try
    {
      result = mOis.readDouble();
    }
    catch (IOException e)
    {
      throw new KasException(e);
    }
    return result;
  }
  
  /**
   * Reads a float from the stream message.
   * 
   * @return read float value from the stream
   * @throws KasException if Message is not in read only mode or an IOException was thrown
   */
  public float readFloat() throws KasException
  {
    float result;
    if (!isReadOnly()) throw new KasException("Message not readable");
    
    try
    {
      result = mOis.readFloat();
    }
    catch (IOException e)
    {
      throw new KasException(e);
    }
    return result;
  }
  
  /**
   * Reads a 32-bit integer from the stream message.
   * 
   * @return read integer value from the stream
   * @throws KasException if Message is not in read only mode or an IOException was thrown
   */
  public int readInt() throws KasException
  {
    int result;
    if (!isReadOnly()) throw new KasException("Message not readable");
    
    try
    {
      result = mOis.readInt();
    }
    catch (IOException e)
    {
      throw new KasException(e);
    }
    return result;
  }
  
  /**
   * Reads a 64-bit integer from the stream message.
   * 
   * @return read long value from the stream
   * @throws KasException if Message is not in read only mode or an IOException was thrown
   */
  public long readLong() throws KasException
  {
    long result;
    if (!isReadOnly()) throw new KasException("Message not readable");
    
    try
    {
      result = mOis.readLong();
    }
    catch (IOException e)
    {
      throw new KasException(e);
    }
    return result;
  }
  
  /**
   * Reads an object from the stream message.
   * 
   * @return read Object value from the stream
   * @throws KasException if Message is not in read only mode or an IOException was thrown
   */
  public Object readObject() throws KasException
  {
    Object result;
    if (!isReadOnly()) throw new KasException("Message not readable");
    
    try
    {
      result = mOis.readObject();
    }
    catch (IOException | ClassNotFoundException e)
    {
      throw new KasException(e);
    }
    return result;
  }
  
  /**
   * Reads a 16-bit integer from the stream message.
   * 
   * @return read short value from the stream
   * @throws KasException if Message is not in read only mode or an IOException was thrown
   */
  public short readShort() throws KasException
  {
    short result;
    if (!isReadOnly()) throw new KasException("Message not readable");
    
    try
    {
      result = mOis.readShort();
    }
    catch (IOException e)
    {
      throw new KasException(e);
    }
    return result;
  }
  
  /**
   * Reads a String from the stream message.
   * 
   * @return read String value from the stream
   * @throws KasException if Message is not in read only mode or an IOException was thrown
   */
  public String readString() throws KasException
  {
    String result;
    if (!isReadOnly()) throw new KasException("Message not readable");
    
    try
    {
      result = (String)mOis.readObject();
    }
    catch (IOException | ClassCastException | ClassNotFoundException e)
    {
      throw new KasException(e);
    }
    return result;
  }
  
  /**
   * Writes a boolean to the stream message.
   * 
   * @param value The boolean value to write
   * @throws KasException if message is in read only mode or an IOException was thrown
   */
  public void writeBoolean(boolean value) throws KasException
  {
    if (isReadOnly()) throw new KasException("Message not writeable");
    
    try
    {
      mOos.writeBoolean(value);
    }
    catch (IOException | ClassCastException e)
    {
      throw new KasException(e);
    }
  }
  
  /**
   * Writes a byte to the stream message.
   * 
   * @param value The byte value to write
   * @throws KasException if message is in read only mode or an IOException was thrown
   */
  public void writeByte(byte value) throws KasException
  {
    if (isReadOnly()) throw new KasException("Message not writeable");
    
    try
    {
      mOos.writeByte(value);
    }
    catch (IOException | ClassCastException e)
    {
      throw new KasException(e);
    }
  }
  
  /**
   * Writes a byte array field to the stream message.
   * 
   * @param value The byte [] value to write
   * @throws KasException if message is in read only mode or an IOException was thrown
   */
  public void writeBytes(byte[] value) throws KasException
  {
    if (isReadOnly()) throw new KasException("Message not writeable");
    
    try
    {
      mOos.write(value);
    }
    catch (IOException | ClassCastException e)
    {
      throw new KasException(e);
    }
  }
  
  /**
   * Writes a portion of a byte array as a byte array field to the stream message.
   * 
   * @param value The byte array that contains the portion to write
   * @param offset The offset on which the portion starts 
   * @param length The length of the portion
   * @throws KasException if message is in read only mode or an IOException was thrown
   */
  public void writeBytes(byte[] value, int offset, int length) throws KasException
  {
    if (isReadOnly()) throw new KasException("Message not writeable");
    
    try
    {
      mOos.write(value, offset, length);
    }
    catch (IOException | ClassCastException e)
    {
      throw new KasException(e);
    }
  }
  
  /**
   * Writes a char to the stream message.
   * 
   * @param value The char value to write
   * @throws KasException if message is in read only mode or an IOException was thrown
   */
  public void writeChar(char value) throws KasException
  {
    if (isReadOnly()) throw new KasException("Message not writeable");
    
    try
    {
      mOos.writeChar(value);
    }
    catch (IOException | ClassCastException e)
    {
      throw new KasException(e);
    }
  }
  
  /**
   * Writes a double to the stream message.
   * 
   * @param value The double value to write
   * @throws KasException if message is in read only mode or an IOException was thrown
   */
  public void writeDouble(double value) throws KasException
  {
    if (isReadOnly()) throw new KasException("Message not writeable");
    
    try
    {
      mOos.writeDouble(value);
    }
    catch (IOException | ClassCastException e)
    {
      throw new KasException(e);
    }
  }
  
  /**
   * Writes a float to the stream message.
   * 
   * @param value The float value to write
   * @throws KasException if message is in read only mode or an IOException was thrown
   */
  public void writeFloat(float value) throws KasException
  {
    if (isReadOnly()) throw new KasException("Message not writeable");
    
    try
    {
      mOos.writeFloat(value);
    }
    catch (IOException | ClassCastException e)
    {
      throw new KasException(e);
    }
  }
  
  /**
   * Writes an int to the stream message.
   * 
   * @param value The integer value to write
   * @throws KasException if message is in read only mode or an IOException was thrown
   */
  public void writeInt(int value) throws KasException
  {
    if (isReadOnly()) throw new KasException("Message not writeable");
    
    try
    {
      mOos.writeInt(value);
    }
    catch (IOException | ClassCastException e)
    {
      throw new KasException(e);
    }
  }
  
  /**
   * Writes a long to the stream message.
   * 
   * @param value The long value to write
   * @throws KasException if message is in read only mode or an IOException was thrown
   */
  public void writeLong(long value) throws KasException
  {
    if (isReadOnly()) throw new KasException("Message not writeable");
    
    try
    {
      mOos.writeLong(value);
    }
    catch (IOException | ClassCastException e)
    {
      throw new KasException(e);
    }
  }
  
  /**
   * Writes an object to the stream message.
   * 
   * @param value The Object value to write
   * @throws KasException if message is in read only mode or an IOException was thrown
   */
  public void writeObject(Object value) throws KasException
  {
    if (isReadOnly()) throw new KasException("Message not writeable");
    
    try
    {
      mOos.writeObject(value);
    }
    catch (IOException | ClassCastException e)
    {
      throw new KasException(e);
    }
  }
  
  /**
   * Writes a short to the stream message.
   * 
   * @param value The short value to write
   * @throws KasException if message is in read only mode or an IOException was thrown
   */
  public void writeShort(short value) throws KasException
  {
    if (isReadOnly()) throw new KasException("Message not writeable");
    
    try
    {
      mOos.writeShort(value);
    }
    catch (IOException | ClassCastException e)
    {
      throw new KasException(e);
    }
  }
  
  /**
   * Writes a String to the stream message.
   * 
   * @param value The String value to write
   * @throws KasException if message is in read only mode or an IOException was thrown
   */
  public void writeString(String value) throws KasException
  {
    if (isReadOnly()) throw new KasException("Message not writeable");
    
    try
    {
      mOos.writeObject(value);
    }
    catch (IOException | ClassCastException e)
    {
      throw new KasException(e);
    }
  }
  
  /**
   * Create the {@link PacketHeader} describing this {@link MqStreamMessage}
   * 
   * @return the packet header
   */
  public PacketHeader createHeader()
  {
    return new PacketHeader(EClassId.cClassMqStreamMessage);
  }
  
  /**
   * Get the object's detailed string representation
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n")
      .append(super.toPrintableString(level))
      .append(pad).append("  Body=(").append(StringUtils.asString(mBody)).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
