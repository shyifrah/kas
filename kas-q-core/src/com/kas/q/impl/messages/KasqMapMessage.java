package com.kas.q.impl.messages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Map;
import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import javax.jms.MapMessage;
import javax.jms.MessageFormatException;
import com.kas.infra.base.KasException;
import com.kas.infra.base.Properties;
import com.kas.q.ext.MessageType;

public class KasqMapMessage extends KasqMessage implements MapMessage
{
  protected Properties mBody;
  
  /***************************************************************************************************************
   * Constructs a default {@code KasqMapMessage} object
   */
  public KasqMapMessage()
  {
    super();
    mMessageType = MessageType.cMapMessage;
    mBody = null;
  }
  
  /***************************************************************************************************************
   * Constructs a {@code KasqMapMessage} object and initialize its body
   * 
   * @param text message body
   */
  public KasqMapMessage(Map<Object, Object> map)
  {
    super();
    mMessageType = MessageType.cMapMessage;
    mBody = new Properties(map);
  }
  
  /***************************************************************************************************************
   * Constructs a {@code KasqMapMessage} object from {@code ObjectInputStream}
   * 
   * @param istream the {@code ObjectInputStream} from which the message will be deserialized
   * 
   * @throws IOException 
   * @throws ClassNotFoundException 
   */
  public KasqMapMessage(ObjectInputStream istream) throws ClassNotFoundException, IOException
  {
    super(istream);
    mBody = (Properties)istream.readObject();
  }
  
  /***************************************************************************************************************
   * Verify a mapped name is valid
   * 
   * @param name the mapped name
   * 
   * @throws JMSException if name is null or an empty string
   */
  private void internalVerify(String name) throws JMSException
  {
    if (name == null)
      throw new JMSException("Invalid name - null");
    
    if (name.length() == 0)
      throw new JMSException("Invalid name - empty string");
  }
  
  /***************************************************************************************************************
   *  
   */
  public boolean getBoolean(String name) throws JMSException
  {
    internalVerify(name);
    boolean result;
    try
    {
      result = mBody.getBoolProperty(name);
    }
    catch (KasException e)
    {
      throw new JMSRuntimeException("getBoolean failed", e.getMessage(), e);
    }
    return result;
  }
  
  /***************************************************************************************************************
   *  
   */
  public void setBoolean(String name, boolean value) throws JMSException
  {
    internalVerify(name);
    try
    {
      mBody.setBoolProperty(name, value);
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("setBoolean failed", "Exception caught. ", e);
    } 
  }

  /***************************************************************************************************************
   *  
   */
  public byte getByte(String name) throws JMSException
  {
    internalVerify(name);
    byte result;
    try
    {
      result = mBody.getByteProperty(name);
    }
    catch (KasException e)
    {
      throw new JMSRuntimeException("getByte failed", e.getMessage(), e);
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void setByte(String name, byte value) throws JMSException
  {
    internalVerify(name);
    try
    {
      mBody.setByteProperty(name, value);
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("setByte failed", "Exception caught. ", e);
    } 
  }

  /***************************************************************************************************************
   *  
   */
  public byte[] getBytes(String name) throws JMSException
  {
    internalVerify(name);
    byte [] result;
    try
    {
      result = (byte [])mBody.getObjectProperty(name);
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("getBytes failed", e.getMessage(), e);
    }
    return result;
  }
  
  /***************************************************************************************************************
   *  
   */
  public void setBytes(String name, byte[] value) throws JMSException
  {
    internalVerify(name);
    try
    {
      mBody.setObjectProperty(name, value);
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("setBytes failed", "Exception caught. ", e);
    } 
  }

  /***************************************************************************************************************
   *  
   */
  public void setBytes(String name, byte[] value, int offset, int length) throws JMSException
  {
    internalVerify(name);
    try
    {
      byte [] newvalue = new byte [length];
      System.arraycopy(value, offset, newvalue, 0, length);
      mBody.setObjectProperty(name, newvalue);
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("setBytes failed", "Exception caught. ", e);
    }
  }

  /***************************************************************************************************************
   *  
   */
  public char getChar(String name) throws JMSException
  {
    internalVerify(name);
    char result;
    try
    {
      result = (char)mBody.getObjectProperty(name);
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("getChar failed", e.getMessage(), e);
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void setChar(String name, char value) throws JMSException
  {
    internalVerify(name);
    try
    {
      mBody.setObjectProperty(name, value);
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("setChar failed", "Exception caught. ", e);
    } 
  }

  /***************************************************************************************************************
   *  
   */
  public double getDouble(String name) throws JMSException
  {
    internalVerify(name);
    double result;
    try
    {
      result = mBody.getDoubleProperty(name);
    }
    catch (KasException e)
    {
      throw new JMSRuntimeException("getDouble failed", e.getMessage(), e);
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void setDouble(String name, double value) throws JMSException
  {
    internalVerify(name);
    try
    {
      mBody.setDoubleProperty(name, value);
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("setDouble failed", "Exception caught. ", e);
    }
  }

  /***************************************************************************************************************
   *  
   */
  public float getFloat(String name) throws JMSException
  {
    internalVerify(name);
    float result;
    try
    {
      result = mBody.getFloatProperty(name);
    }
    catch (KasException e)
    {
      throw new JMSRuntimeException("getFloat failed", e.getMessage(), e);
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void setFloat(String name, float value) throws JMSException
  {
    internalVerify(name);
    try
    {
      mBody.setFloatProperty(name, value);
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("setFloat failed", "Exception caught. ", e);
    }
  }

  /***************************************************************************************************************
   *  
   */
  public int getInt(String name) throws JMSException
  {
    internalVerify(name);
    int result;
    try
    {
      result = mBody.getIntProperty(name);
    }
    catch (KasException e)
    {
      throw new JMSRuntimeException("getInt failed", e.getMessage(), e);
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void setInt(String name, int value) throws JMSException
  {
    internalVerify(name);
    try
    {
      mBody.setIntProperty(name, value);
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("setInt failed", "Exception caught. ", e);
    }
  }

  /***************************************************************************************************************
   *  
   */
  public long getLong(String name) throws JMSException
  {
    internalVerify(name);
    long result;
    try
    {
      result = mBody.getLongProperty(name);
    }
    catch (KasException e)
    {
      throw new JMSRuntimeException("getLong failed", e.getMessage(), e);
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void setLong(String name, long value) throws JMSException
  {
    internalVerify(name);
    try
    {
      mBody.setLongProperty(name, value);
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("setLong failed", "Exception caught. ", e);
    }
  }

  /***************************************************************************************************************
   *  
   */
  public Object getObject(String name) throws JMSException
  {
    internalVerify(name);
    Object result;
    try
    {
      result = mBody.getObjectProperty(name);
    }
    catch (KasException e)
    {
      throw new JMSRuntimeException("getObject failed", e.getMessage(), e);
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void setObject(String name, Object value) throws JMSException
  {
    internalVerify(name);
    try
    {
      mBody.setObjectProperty(name, value);
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("setObject failed", "Exception caught. ", e);
    }
  }

  /***************************************************************************************************************
   *  
   */
  public short getShort(String name) throws JMSException
  {
    internalVerify(name);
    short result;
    try
    {
      result = mBody.getShortProperty(name);
    }
    catch (KasException e)
    {
      throw new JMSRuntimeException("getShort failed", e.getMessage(), e);
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void setShort(String name, short value) throws JMSException
  {
    internalVerify(name);
    try
    {
      mBody.setShortProperty(name, value);
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("setShort failed", "Exception caught. ", e);
    }
  }

  /***************************************************************************************************************
   *  
   */
  public String getString(String name) throws JMSException
  {
    internalVerify(name);
    String result;
    try
    {
      result = mBody.getStringProperty(name);
    }
    catch (KasException e)
    {
      throw new JMSRuntimeException("getString failed", e.getMessage(), e);
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void setString(String name, String value) throws JMSException
  {
    internalVerify(name);
    try
    {
      mBody.setStringProperty(name, value);
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("setString failed", "Exception caught. ", e);
    }
  }
  
  /***************************************************************************************************************
   *  
   */
  public boolean itemExists(String name) throws JMSException
  {
    internalVerify(name);
    return mBody.containsKey(name);
  }

  /***************************************************************************************************************
   *  
   */
  public Enumeration<?> getMapNames() throws JMSException
  {
    Enumeration<?> result;
    try
    {
      result = mBody.keys();
    }
    catch (Throwable e)
    {
      throw new JMSRuntimeException("getMapNames failed", "Exception caught. ", e);
    }
    return result;
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
    return Map.class.isAssignableFrom(c);
  }

  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    return super.toPrintableString(level);
  }
}
