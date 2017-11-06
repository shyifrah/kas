package com.kas.q.impl.messages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MessageFormatException;
import com.kas.q.ext.MessageType;

public class KasqBytesMessage extends KasqMessage implements BytesMessage
{
  protected byte [] mBody;
  
  /***************************************************************************************************************
   * Constructs a default {@code KasqBytesMessage} object
   */
  public KasqBytesMessage()
  {
    super();
    mMessageType = MessageType.cBytesMessage;
    mBody = null;
  }
  
  /***************************************************************************************************************
   * Constructs a {@code KasqBytesMessage} object and initialize its body
   * 
   * @param bytes message body
   */
  public KasqBytesMessage(byte [] bytes)
  {
    super();
    mMessageType = MessageType.cBytesMessage;
    mBody = bytes;
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
  
  public boolean readBoolean() throws JMSException
  {
    return false;
  }

  public void writeBoolean(boolean arg0) throws JMSException
  {
  }

  public byte readByte() throws JMSException
  {
    return 0;
  }

  public void writeByte(byte arg0) throws JMSException
  {
  }

  public int readBytes(byte[] arg0) throws JMSException
  {
    return 0;
  }

  public void writeBytes(byte[] arg0) throws JMSException
  {
  }

  public int readBytes(byte[] arg0, int arg1) throws JMSException
  {
    return 0;
  }

  public void writeBytes(byte[] arg0, int arg1, int arg2) throws JMSException
  {
  }

  public char readChar() throws JMSException
  {
    return 0;
  }

  public void writeChar(char arg0) throws JMSException
  {
  }

  public double readDouble() throws JMSException
  {
    return 0;
  }

  public void writeDouble(double arg0) throws JMSException
  {
  }

  public float readFloat() throws JMSException
  {
    return 0;
  }

  public void writeFloat(float arg0) throws JMSException
  {
  }

  public int readInt() throws JMSException
  {
    return 0;
  }

  public void writeInt(int arg0) throws JMSException
  {
  }

  public long readLong() throws JMSException
  {
    return 0;
  }

  public void writeLong(long arg0) throws JMSException
  {
  }

  public short readShort() throws JMSException
  {
    return 0;
  }

  public void writeShort(short arg0) throws JMSException
  {
  }

  public String readUTF() throws JMSException
  {
    return null;
  }

  public void writeUTF(String arg0) throws JMSException
  {
  }
  
  public int readUnsignedByte() throws JMSException
  {
    return 0;
  }

  public int readUnsignedShort() throws JMSException
  {
    return 0;
  }

  public void writeObject(Object arg0) throws JMSException
  {
  }

  public void reset() throws JMSException
  {
  }

  public long getBodyLength() throws JMSException
  {
    return 0;
  }

  /***************************************************************************************************************
   *  
   */
  public void serialize(ObjectOutputStream ostream)
  {
    super.serialize(ostream);
    try
    {
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
    mBody = null;
  }

  /***************************************************************************************************************
   *  
   */
  @SuppressWarnings("unchecked")
  public <T> T getBody(Class<T> c) throws JMSException
  {
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
