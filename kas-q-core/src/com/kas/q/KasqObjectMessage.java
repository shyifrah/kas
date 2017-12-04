package com.kas.q;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.jms.JMSException;
import javax.jms.MessageFormatException;
import javax.jms.ObjectMessage;
import com.kas.q.ext.EMessageType;
import com.kas.q.ext.EReadWriteMode;

public class KasqObjectMessage extends KasqMessage implements ObjectMessage
{
  protected Serializable mBody;
  
  /***************************************************************************************************************
   * Constructs a default {@code KasqObjectMessage} object
   */
  public KasqObjectMessage()
  {
    super();
    mBody = null;
  }
  
  /***************************************************************************************************************
   * Constructs a {@code KasqObjectMessage} object and initialize its body
   * 
   * @param serializable message body
   */
  public KasqObjectMessage(Serializable serializable)
  {
    super();
    mBody = serializable;
  }
  
  /***************************************************************************************************************
   * Constructs a {@code KasqObjectMessage} object from {@code ObjectInputStream}
   * 
   * @param istream the {@code ObjectInputStream} from which the message will be deserialized
   * 
   * @throws IOException 
   */
  public KasqObjectMessage(ObjectInputStream istream) throws IOException
  {
    super(istream);
    try
    {
      mBody = (Serializable)istream.readObject();
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
  public EMessageType getType()
  {
    return EMessageType.cKasqMessageObject;
  }  
  
  /***************************************************************************************************************
   *  
   */
  public Serializable getObject() throws JMSException
  {
    internalAssertBodyReadable();
    return mBody;
  }

  /***************************************************************************************************************
   *  
   */
  public void setObject(Serializable serializable) throws JMSException
  {
    internalAssertBodyWriteable();
    mBody = serializable;
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
    return Serializable.class.isAssignableFrom(c);
  }

  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    return super.toPrintableString(level);
  }
}
