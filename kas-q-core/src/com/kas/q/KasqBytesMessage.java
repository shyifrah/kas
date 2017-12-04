package com.kas.q;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MessageFormatException;
import com.kas.q.ext.EMessageType;
import com.kas.q.ext.EReadWriteMode;

public class KasqBytesMessage extends KasqMessage implements BytesMessage
{
  private byte [] mBody;
  private ByteArrayOutputStream mOutputArray  = null;
  private ByteArrayInputStream  mInputArray   = null;
  private ObjectOutputStream    mOutputStream = null;
  private ObjectInputStream     mInputStream  = null;
  
  /***************************************************************************************************************
   * Constructs a default {@code KasqBytesMessage} object
   * 
   * @throws JMSException if an IOException is thrown
   */
  public KasqBytesMessage() throws JMSException
  {
    super();
    mBody = null;
    mBodyMode = EReadWriteMode.cWriteOnly;
    
    try
    {
      mOutputArray  = new ByteArrayOutputStream();
      mOutputStream = new ObjectOutputStream(mOutputArray);
    }
    catch (IOException e)
    {
      throw new JMSException("IOException caught");
    }
  }
  
  /***************************************************************************************************************
   * Constructs a {@code KasqBytesMessage} object from {@code ObjectInputStream}
   * 
   * @param istream the {@code ObjectInputStream} from which the message will be deserialized
   * 
   * @throws IOException 
   */
  public KasqBytesMessage(ObjectInputStream istream) throws IOException
  {
    super(istream);
    try
    {
      mBody = (byte [])istream.readObject();
    }
    catch (IOException e)
    {
      throw e;
    }
    catch (Throwable e)
    {
      throw new IOException(e);
    }
  }
  
  /***************************************************************************************************************
   * 
   */
  public EMessageType getType()
  {
    return EMessageType.cKasqMessageBytes;
  }
  
  /***************************************************************************************************************
   *  
   */
  public boolean readBoolean() throws JMSException
  {
    internalAssertBodyReadable();
    boolean result;
    try
    {
      result = mInputStream.readBoolean();
    }
    catch (Throwable e)
    {
      throw new JMSException("readBoolean() failed", e.getMessage());
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void writeBoolean(boolean value) throws JMSException
  {
    internalAssertBodyWriteable();
    try
    {
      mOutputStream.writeBoolean(value);
    }
    catch (Throwable e)
    {
      throw new JMSException("writeBoolean() failed", e.getMessage());
    }
  }

  /***************************************************************************************************************
   *  
   */
  public byte readByte() throws JMSException
  {
    internalAssertBodyReadable();
    byte result;
    try
    {
      result = mInputStream.readByte();
    }
    catch (Throwable e)
    {
      throw new JMSException("readByte() failed", e.getMessage());
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void writeByte(byte value) throws JMSException
  {
    internalAssertBodyWriteable();
    try
    {
      mOutputStream.writeByte(value);
    }
    catch (Throwable e)
    {
      throw new JMSException("writeByte() failed", e.getMessage());
    }
  }

  /***************************************************************************************************************
   *  
   */
  public int readBytes(byte[] value) throws JMSException
  {
    internalAssertBodyReadable();
    int result;
    try
    {
      result = mInputStream.read(value);
    }
    catch (Throwable e)
    {
      throw new JMSException("readBytes() failed", e.getMessage());
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void writeBytes(byte[] value) throws JMSException
  {
    internalAssertBodyWriteable();
    try
    {
      mOutputStream.write(value);
    }
    catch (Throwable e)
    {
      throw new JMSException("writeBytes() failed", e.getMessage());
    }
  }

  /***************************************************************************************************************
   *  
   */
  public int readBytes(byte[] value, int length) throws JMSException
  {
    internalAssertBodyReadable();
    int result;
    try
    {
      result = mInputStream.read(value, 0, length);
    }
    catch (Throwable e)
    {
      throw new JMSException("readBytes() failed", e.getMessage());
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void writeBytes(byte[] value, int offset, int length) throws JMSException
  {
    internalAssertBodyWriteable();
    try
    {
      mOutputStream.write(value, offset, length);
    }
    catch (Throwable e)
    {
      throw new JMSException("writeBytes() failed", e.getMessage());
    }
  }

  /***************************************************************************************************************
   *  
   */
  public char readChar() throws JMSException
  {
    internalAssertBodyReadable();
    char result;
    try
    {
      result = mInputStream.readChar();
    }
    catch (Throwable e)
    {
      throw new JMSException("readChar() failed", e.getMessage());
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void writeChar(char value) throws JMSException
  {
    internalAssertBodyWriteable();
    try
    {
      mOutputStream.writeChar(value);
    }
    catch (Throwable e)
    {
      throw new JMSException("writeChar() failed", e.getMessage());
    }
  }

  /***************************************************************************************************************
   *  
   */
  public double readDouble() throws JMSException
  {
    internalAssertBodyReadable();
    double result;
    try
    {
      result = mInputStream.readDouble();
    }
    catch (Throwable e)
    {
      throw new JMSException("readDouble() failed", e.getMessage());
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void writeDouble(double value) throws JMSException
  {
    internalAssertBodyWriteable();
    try
    {
      mOutputStream.writeDouble(value);
    }
    catch (Throwable e)
    {
      throw new JMSException("writeDouble() failed", e.getMessage());
    }
  }

  /***************************************************************************************************************
   *  
   */
  public float readFloat() throws JMSException
  {
    internalAssertBodyReadable();
    float result;
    try
    {
      result = mInputStream.readFloat();
    }
    catch (Throwable e)
    {
      throw new JMSException("readFloat() failed", e.getMessage());
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void writeFloat(float value) throws JMSException
  {
    internalAssertBodyWriteable();
    try
    {
      mOutputStream.writeFloat(value);
    }
    catch (Throwable e)
    {
      throw new JMSException("writeFloat() failed", e.getMessage());
    }
  }

  /***************************************************************************************************************
   *  
   */
  public int readInt() throws JMSException
  {
    internalAssertBodyReadable();
    int result;
    try
    {
      result = mInputStream.readInt();
    }
    catch (Throwable e)
    {
      throw new JMSException("readInt() failed", e.getMessage());
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void writeInt(int value) throws JMSException
  {
    internalAssertBodyWriteable();
    try
    {
      mOutputStream.writeInt(value);
    }
    catch (Throwable e)
    {
      throw new JMSException("writeInt() failed", e.getMessage());
    }
  }

  /***************************************************************************************************************
   *  
   */
  public long readLong() throws JMSException
  {
    internalAssertBodyReadable();
    long result;
    try
    {
      result = mInputStream.readLong();
    }
    catch (Throwable e)
    {
      throw new JMSException("readLong() failed", e.getMessage());
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void writeLong(long value) throws JMSException
  {
    internalAssertBodyWriteable();
    try
    {
      mOutputStream.writeLong(value);
    }
    catch (Throwable e)
    {
      throw new JMSException("writeLong() failed", e.getMessage());
    }
  }

  /***************************************************************************************************************
   *  
   */
  public short readShort() throws JMSException
  {
    internalAssertBodyReadable();
    short result;
    try
    {
      result = mInputStream.readShort();
    }
    catch (Throwable e)
    {
      throw new JMSException("readShort() failed", e.getMessage());
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void writeShort(short value) throws JMSException
  {
    internalAssertBodyWriteable();
    try
    {
      mOutputStream.writeShort(value);
    }
    catch (Throwable e)
    {
      throw new JMSException("writeShort() failed", e.getMessage());
    }
  }

  /***************************************************************************************************************
   *  
   */
  public String readUTF() throws JMSException
  {
    internalAssertBodyReadable();
    String result;
    try
    {
      result = mInputStream.readUTF();
    }
    catch (Throwable e)
    {
      throw new JMSException("readUTF() failed", e.getMessage());
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void writeUTF(String value) throws JMSException
  {
    internalAssertBodyWriteable();
    try
    {
      mOutputStream.writeUTF(value);
    }
    catch (Throwable e)
    {
      throw new JMSException("writeUTF() failed", e.getMessage());
    }
  }
  
  /***************************************************************************************************************
   *  
   */
  public int readUnsignedByte() throws JMSException
  {
    internalAssertBodyReadable();
    int result;
    try
    {
      result = mInputStream.readUnsignedByte();
    }
    catch (Throwable e)
    {
      throw new JMSException("readUnsignedByte() failed", e.getMessage());
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public int readUnsignedShort() throws JMSException
  {
    internalAssertBodyReadable();
    int result;
    try
    {
      result = mInputStream.readUnsignedShort();
    }
    catch (Throwable e)
    {
      throw new JMSException("readUnsignedShort() failed", e.getMessage());
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void writeObject(Object value) throws JMSException
  {
    internalAssertBodyWriteable();
    try
    {
      mOutputStream.writeObject(value);
    }
    catch (Throwable e)
    {
      throw new JMSException("writeObject() failed", e.getMessage());
    }
  }

  /***************************************************************************************************************
   *  
   */
  public void reset() throws JMSException
  {
    try
    {
      mOutputStream.flush();
      mOutputStream.close();
      mOutputArray.flush();
      mOutputArray.close();
      
      mBody = mOutputArray.toByteArray();
      
      mInputArray = new ByteArrayInputStream(mBody);
      mInputStream = new ObjectInputStream(mInputArray);
    }
    catch (Throwable e)
    {
      throw new JMSException("reset() failed", e.getMessage());
    }
  }

  /***************************************************************************************************************
   *  
   */
  public long getBodyLength() throws JMSException
  {
    internalAssertBodyReadable();
    return mBody.length;
  }

  /***************************************************************************************************************
   *  
   */
  public void serialize(ObjectOutputStream ostream)
  {
    super.serialize(ostream);
    try
    {
      reset();
      ostream.writeObject(mBody);
      ostream.reset();
    }
    catch (Throwable e)
    {
      throw new RuntimeException(e);
    }
  }
  
  /***************************************************************************************************************
   *  
   */
  public void clearBody() throws JMSException
  {
    try
    {
      mOutputStream.flush();
      mOutputStream.close();
      mOutputArray.flush();
      mOutputArray.close();
      
      mOutputArray = new ByteArrayOutputStream();
      mOutputStream = new ObjectOutputStream(mOutputArray);
      
      mBody = null;
      
      mBodyMode = EReadWriteMode.cWriteOnly;
    }
    catch (Throwable e)
    {
      throw new JMSException("clearBody() failed", e.getMessage());
    }
  }

  /***************************************************************************************************************
   *  
   */
  @SuppressWarnings("unchecked")
  public <T> T getBody(Class<T> c) throws JMSException
  {
    internalAssertBodyReadable();
    
    if (mBody == null)
      return null;
    
    if (isBodyAssignableTo(c))
      return (T)mBody;
    
    throw new MessageFormatException("Body not assignable to type: " + c.getName());
  }

  /***************************************************************************************************************
   *  
   */
  @SuppressWarnings("rawtypes")
  public boolean isBodyAssignableTo(Class c) throws JMSException
  {
    return byte[].class.isAssignableFrom(c);
  }

  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    return super.toPrintableString(level);
  }
}
