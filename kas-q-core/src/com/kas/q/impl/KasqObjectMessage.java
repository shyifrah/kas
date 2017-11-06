package com.kas.q.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.jms.JMSException;
import javax.jms.MessageFormatException;
import javax.jms.ObjectMessage;
import com.kas.q.ext.MessageType;

public class KasqObjectMessage extends KasqMessage implements ObjectMessage
{
  protected Serializable mBody;
  
  /***************************************************************************************************************
   * Constructs a default {@code KasqObjectMessage} object
   */
  public KasqObjectMessage()
  {
    super();
    mMessageType = MessageType.cObjectMessage;
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
    mMessageType = MessageType.cObjectMessage;
    mBody = serializable;
  }
  
  /***************************************************************************************************************
   * Constructs a {@code KasqObjectMessage} object from {@code ObjectInputStream}
   * 
   * @param istream the {@code ObjectInputStream} from which the message will be deserialized
   * 
   * @throws IOException 
   * @throws ClassNotFoundException 
   */
  public KasqObjectMessage(ObjectInputStream istream) throws ClassNotFoundException, IOException
  {
    super(istream);
    mBody = (Serializable)istream.readObject();
  }
  
  /***************************************************************************************************************
   *  
   */
  public Serializable getObject() throws JMSException
  {
    return mBody;
  }

  /***************************************************************************************************************
   *  
   */
  public void setObject(Serializable serializable) throws JMSException
  {
    mBody = serializable;
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
