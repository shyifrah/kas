package com.kas.q.impl.messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import javax.jms.MessageFormatException;
import com.kas.q.ext.MessageType;

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
   * @throws IOException 
   */
  public KasqBytesMessage() throws IOException
  {
    super();
    mMessageType = MessageType.cBytesMessage;
    mBody = null;
    mBodyMode = ReadWriteMode.cWriteOnly;
    
    mOutputArray  = new ByteArrayOutputStream();
    mOutputStream = new ObjectOutputStream(mOutputArray);
  }
  
  /***************************************************************************************************************
   * Constructs a {@code KasqBytesMessage} object from {@code ObjectInputStream}
   * 
   * @param istream the {@code ObjectInputStream} from which the message will be deserialized
   * 
   * @throws IOException 
   * @throws ClassNotFoundException 
   */
  public KasqBytesMessage(ObjectInputStream istream) throws ClassNotFoundException, IOException
  {
    super(istream);
    mBody = (byte [])istream.readObject();
  }
  
  /***************************************************************************************************************
   *  
   */
  public boolean readBoolean() throws JMSException
  {
    assertBodyReadable();
    boolean result;
    try
    {
      result = mInputStream.readBoolean();
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("readBoolean failed", "Exception caught. ", e);
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void writeBoolean(boolean value) throws JMSException
  {
    assertBodyWriteable();
    try
    {
      mOutputStream.writeBoolean(value);
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("writeBoolean failed", "Exception caught. ", e);
    }
  }

  /***************************************************************************************************************
   *  
   */
  public byte readByte() throws JMSException
  {
    assertBodyReadable();
    byte result;
    try
    {
      result = mInputStream.readByte();
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("readByte failed", "Exception caught. ", e);
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void writeByte(byte value) throws JMSException
  {
    assertBodyWriteable();
    try
    {
      mOutputStream.writeByte(value);
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("writeByte failed", "Exception caught. ", e);
    }
  }

  /***************************************************************************************************************
   *  
   */
  public int readBytes(byte[] value) throws JMSException
  {
    assertBodyReadable();
    int result;
    try
    {
      result = mInputStream.read(value);
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("readBytes failed", "Exception caught. ", e);
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void writeBytes(byte[] value) throws JMSException
  {
    assertBodyWriteable();
    try
    {
      mOutputStream.write(value);
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("writeBytes failed", "Exception caught. ", e);
    }
  }

  /***************************************************************************************************************
   *  
   */
  public int readBytes(byte[] value, int length) throws JMSException
  {
    assertBodyReadable();
    int result;
    try
    {
      result = mInputStream.read(value, 0, length);
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("readBytes failed", "Exception caught. ", e);
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void writeBytes(byte[] value, int offset, int length) throws JMSException
  {
    assertBodyWriteable();
    try
    {
      mOutputStream.write(value, offset, length);
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("writeBytes failed", "Exception caught. ", e);
    }
  }

  /***************************************************************************************************************
   *  
   */
  public char readChar() throws JMSException
  {
    assertBodyReadable();
    char result;
    try
    {
      result = mInputStream.readChar();
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("readChar failed", "Exception caught. ", e);
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void writeChar(char value) throws JMSException
  {
    assertBodyWriteable();
    try
    {
      mOutputStream.writeChar(value);
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("writeChar failed", "Exception caught. ", e);
    }
  }

  /***************************************************************************************************************
   *  
   */
  public double readDouble() throws JMSException
  {
    assertBodyReadable();
    double result;
    try
    {
      result = mInputStream.readDouble();
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("readDouble failed", "Exception caught. ", e);
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void writeDouble(double value) throws JMSException
  {
    assertBodyWriteable();
    try
    {
      mOutputStream.writeDouble(value);
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("writeDouble failed", "Exception caught. ", e);
    }
  }

  /***************************************************************************************************************
   *  
   */
  public float readFloat() throws JMSException
  {
    assertBodyReadable();
    float result;
    try
    {
      result = mInputStream.readFloat();
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("readFloat failed", "Exception caught. ", e);
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void writeFloat(float value) throws JMSException
  {
    assertBodyWriteable();
    try
    {
      mOutputStream.writeFloat(value);
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("writeFloat failed", "Exception caught. ", e);
    }
  }

  /***************************************************************************************************************
   *  
   */
  public int readInt() throws JMSException
  {
    assertBodyReadable();
    int result;
    try
    {
      result = mInputStream.readInt();
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("readInt failed", "Exception caught. ", e);
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void writeInt(int value) throws JMSException
  {
    assertBodyWriteable();
    try
    {
      mOutputStream.writeInt(value);
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("writeInt failed", "Exception caught. ", e);
    }
  }

  /***************************************************************************************************************
   *  
   */
  public long readLong() throws JMSException
  {
    assertBodyReadable();
    long result;
    try
    {
      result = mInputStream.readLong();
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("readLong failed", "Exception caught. ", e);
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void writeLong(long value) throws JMSException
  {
    assertBodyWriteable();
    try
    {
      mOutputStream.writeLong(value);
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("writeLong failed", "Exception caught. ", e);
    }
  }

  /***************************************************************************************************************
   *  
   */
  public short readShort() throws JMSException
  {
    assertBodyReadable();
    short result;
    try
    {
      result = mInputStream.readShort();
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("readShort failed", "Exception caught. ", e);
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void writeShort(short value) throws JMSException
  {
    assertBodyWriteable();
    try
    {
      mOutputStream.writeShort(value);
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("writeShort failed", "Exception caught. ", e);
    }
  }

  /***************************************************************************************************************
   *  
   */
  public String readUTF() throws JMSException
  {
    assertBodyReadable();
    String result;
    try
    {
      result = mInputStream.readUTF();
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("readUTF failed", "Exception caught. ", e);
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void writeUTF(String value) throws JMSException
  {
    assertBodyWriteable();
    try
    {
      mOutputStream.writeUTF(value);
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("writeUTF failed", "Exception caught. ", e);
    }
  }
  
  /***************************************************************************************************************
   *  
   */
  public int readUnsignedByte() throws JMSException
  {
    assertBodyReadable();
    int result;
    try
    {
      result = mInputStream.readUnsignedByte();
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("readUnsignedByte failed", "Exception caught. ", e);
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public int readUnsignedShort() throws JMSException
  {
    assertBodyReadable();
    int result;
    try
    {
      result = mInputStream.readUnsignedShort();
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("readUnsignedShort failed", "Exception caught. ", e);
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void writeObject(Object value) throws JMSException
  {
    assertBodyWriteable();
    try
    {
      mOutputStream.writeObject(value);
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("writeObject failed", "Exception caught. ", e);
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
      throw new JMSRuntimeException("Message reset failed", "Exception caught. ", e);
    }
  }

  /***************************************************************************************************************
   *  
   */
  public long getBodyLength() throws JMSException
  {
    assertBodyReadable();
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
      
      mBodyMode = ReadWriteMode.cWriteOnly;
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("clearBody failed", "Exception caught. ", e);
    }
  }

  /***************************************************************************************************************
   *  
   */
  @SuppressWarnings("unchecked")
  public <T> T getBody(Class<T> c) throws JMSException
  {
    assertBodyReadable();
    
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
