package com.kas.q;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Map;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageFormatException;
import com.kas.infra.base.KasException;
import com.kas.infra.base.Properties;
import com.kas.q.ext.EMessageType;
import com.kas.q.ext.EReadWriteMode;

public class KasqMapMessage extends KasqMessage implements MapMessage
{
  protected Properties mBody;
  
  /***************************************************************************************************************
   * Constructs a default {@code KasqMapMessage} object
   */
  public KasqMapMessage()
  {
    super();
    mBody = null;
  }
  
  /***************************************************************************************************************
   * Constructs a {@code KasqMapMessage} object from {@code ObjectInputStream}
   * 
   * @param istream the {@code ObjectInputStream} from which the message will be deserialized
   * 
   * @throws IOException
   */
  public KasqMapMessage(ObjectInputStream istream) throws IOException
  {
    super(istream);
    try
    {
      mBody = (Properties)istream.readObject();
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
    return EMessageType.cKasqMessageMap;
  }
  
  /***************************************************************************************************************
   *  
   */
  public boolean getBoolean(String name) throws JMSException
  {
    internalVerify(name);
    internalAssertBodyReadable();
    boolean result;
    try
    {
      result = mBody.getBoolProperty(name);
    }
    catch (KasException e)
    {
      throw new JMSException("getBoolean failed", e.getMessage());
    }
    return result;
  }
  
  /***************************************************************************************************************
   *  
   */
  public void setBoolean(String name, boolean value) throws JMSException
  {
    internalVerify(name);
    internalAssertBodyWriteable();
    try
    {
      mBody.setBoolProperty(name, value);
    }
    catch (Throwable e)
    {
      throw new JMSException("setBoolean failed", "Exception caught. ");
    } 
  }

  /***************************************************************************************************************
   *  
   */
  public byte getByte(String name) throws JMSException
  {
    internalVerify(name);
    internalAssertBodyReadable();
    byte result;
    try
    {
      result = mBody.getByteProperty(name);
    }
    catch (KasException e)
    {
      throw new JMSException("getByte failed", e.getMessage());
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void setByte(String name, byte value) throws JMSException
  {
    internalVerify(name);
    internalAssertBodyWriteable();
    try
    {
      mBody.setByteProperty(name, value);
    }
    catch (Throwable e)
    {
      throw new JMSException("setByte failed", "Exception caught. ");
    } 
  }

  /***************************************************************************************************************
   *  
   */
  public byte[] getBytes(String name) throws JMSException
  {
    internalVerify(name);
    internalAssertBodyReadable();
    byte [] result;
    try
    {
      result = (byte [])mBody.getObjectProperty(name);
    }
    catch (Throwable e)
    {
      throw new JMSException("getBytes failed", e.getMessage());
    }
    return result;
  }
  
  /***************************************************************************************************************
   *  
   */
  public void setBytes(String name, byte[] value) throws JMSException
  {
    internalVerify(name);
    internalAssertBodyWriteable();
    try
    {
      mBody.setObjectProperty(name, value);
    }
    catch (Throwable e)
    {
      throw new JMSException("setBytes failed", "Exception caught. ");
    } 
  }

  /***************************************************************************************************************
   *  
   */
  public void setBytes(String name, byte[] value, int offset, int length) throws JMSException
  {
    internalVerify(name);
    internalAssertBodyWriteable();
    try
    {
      byte [] newvalue = new byte [length];
      System.arraycopy(value, offset, newvalue, 0, length);
      mBody.setObjectProperty(name, newvalue);
    }
    catch (Throwable e)
    {
      throw new JMSException("setBytes failed", "Exception caught. ");
    }
  }

  /***************************************************************************************************************
   *  
   */
  public char getChar(String name) throws JMSException
  {
    internalVerify(name);
    internalAssertBodyReadable();
    char result;
    try
    {
      result = (char)mBody.getObjectProperty(name);
    }
    catch (Throwable e)
    {
      throw new JMSException("getChar failed", e.getMessage());
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void setChar(String name, char value) throws JMSException
  {
    internalVerify(name);
    internalAssertBodyWriteable();
    try
    {
      mBody.setObjectProperty(name, value);
    }
    catch (Throwable e)
    {
      throw new JMSException("setChar failed", "Exception caught. ");
    } 
  }

  /***************************************************************************************************************
   *  
   */
  public double getDouble(String name) throws JMSException
  {
    internalVerify(name);
    internalAssertBodyReadable();
    double result;
    try
    {
      result = mBody.getDoubleProperty(name);
    }
    catch (KasException e)
    {
      throw new JMSException("getDouble failed", e.getMessage());
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void setDouble(String name, double value) throws JMSException
  {
    internalVerify(name);
    internalAssertBodyWriteable();
    try
    {
      mBody.setDoubleProperty(name, value);
    }
    catch (Throwable e)
    {
      throw new JMSException("setDouble failed", "Exception caught. ");
    }
  }

  /***************************************************************************************************************
   *  
   */
  public float getFloat(String name) throws JMSException
  {
    internalVerify(name);
    internalAssertBodyReadable();
    float result;
    try
    {
      result = mBody.getFloatProperty(name);
    }
    catch (KasException e)
    {
      throw new JMSException("getFloat failed", e.getMessage());
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void setFloat(String name, float value) throws JMSException
  {
    internalVerify(name);
    internalAssertBodyWriteable();
    try
    {
      mBody.setFloatProperty(name, value);
    }
    catch (Throwable e)
    {
      throw new JMSException("setFloat failed", "Exception caught. ");
    }
  }

  /***************************************************************************************************************
   *  
   */
  public int getInt(String name) throws JMSException
  {
    internalVerify(name);
    internalAssertBodyReadable();
    int result;
    try
    {
      result = mBody.getIntProperty(name);
    }
    catch (KasException e)
    {
      throw new JMSException("getInt failed", e.getMessage());
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void setInt(String name, int value) throws JMSException
  {
    internalVerify(name);
    internalAssertBodyWriteable();
    try
    {
      mBody.setIntProperty(name, value);
    }
    catch (Throwable e)
    {
      throw new JMSException("setInt failed", "Exception caught. ");
    }
  }

  /***************************************************************************************************************
   *  
   */
  public long getLong(String name) throws JMSException
  {
    internalVerify(name);
    internalAssertBodyReadable();
    long result;
    try
    {
      result = mBody.getLongProperty(name);
    }
    catch (KasException e)
    {
      throw new JMSException("getLong failed", e.getMessage());
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void setLong(String name, long value) throws JMSException
  {
    internalVerify(name);
    internalAssertBodyWriteable();
    try
    {
      mBody.setLongProperty(name, value);
    }
    catch (Throwable e)
    {
      throw new JMSException("setLong failed", "Exception caught. ");
    }
  }

  /***************************************************************************************************************
   *  
   */
  public Object getObject(String name) throws JMSException
  {
    internalVerify(name);
    internalAssertBodyReadable();
    Object result;
    try
    {
      result = mBody.getObjectProperty(name);
    }
    catch (KasException e)
    {
      throw new JMSException("getObject failed", e.getMessage());
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void setObject(String name, Object value) throws JMSException
  {
    internalVerify(name);
    internalAssertBodyWriteable();
    try
    {
      mBody.setObjectProperty(name, value);
    }
    catch (Throwable e)
    {
      throw new JMSException("setObject failed", "Exception caught. ");
    }
  }

  /***************************************************************************************************************
   *  
   */
  public short getShort(String name) throws JMSException
  {
    internalVerify(name);
    internalAssertBodyReadable();
    short result;
    try
    {
      result = mBody.getShortProperty(name);
    }
    catch (KasException e)
    {
      throw new JMSException("getShort failed", e.getMessage());
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void setShort(String name, short value) throws JMSException
  {
    internalVerify(name);
    internalAssertBodyWriteable();
    try
    {
      mBody.setShortProperty(name, value);
    }
    catch (Throwable e)
    {
      throw new JMSException("setShort failed", "Exception caught. ");
    }
  }

  /***************************************************************************************************************
   *  
   */
  public String getString(String name) throws JMSException
  {
    internalVerify(name);
    internalAssertBodyReadable();
    String result;
    try
    {
      result = mBody.getStringProperty(name);
    }
    catch (KasException e)
    {
      throw new JMSException("getString failed", e.getMessage());
    }
    return result;
  }

  /***************************************************************************************************************
   *  
   */
  public void setString(String name, String value) throws JMSException
  {
    internalVerify(name);
    internalAssertBodyWriteable();
    try
    {
      mBody.setStringProperty(name, value);
    }
    catch (Throwable e)
    {
      throw new JMSException("setString failed", "Exception caught. ");
    }
  }
  
  /***************************************************************************************************************
   *  
   */
  public boolean itemExists(String name) throws JMSException
  {
    internalVerify(name);
    internalAssertBodyReadable();
    return mBody.containsKey(name);
  }

  /***************************************************************************************************************
   *  
   */
  public Enumeration<?> getMapNames() throws JMSException
  {
    internalAssertBodyReadable();
    Enumeration<?> result;
    try
    {
      result = mBody.keys();
    }
    catch (Throwable e)
    {
      throw new JMSException("getMapNames failed", "Exception caught. ");
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
    mBodyMode = EReadWriteMode.cReadWrite;
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
    return Map.class.isAssignableFrom(c);
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
  public String toPrintableString(int level)
  {
    return super.toPrintableString(level);
  }
}
