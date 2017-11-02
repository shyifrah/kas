package com.kas.q.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.jms.JMSException;
import javax.jms.MessageFormatException;
import javax.jms.TextMessage;
import com.kas.q.ext.MessageType;

public class KasqTextMessage extends KasqMessage implements TextMessage
{
  protected String mBody;
  
  /***************************************************************************************************************
   * Constructs a default {@code KasqTextMessage} object
   */
  public KasqTextMessage()
  {
    this("");
  }
  
  /***************************************************************************************************************
   * Constructs a {@code KasqTextMessage} object and initialize its body
   * 
   * @param text message body
   */
  public KasqTextMessage(String text)
  {
    super();
    mMessageType = MessageType.cTextMessage;
    mBody = text;
  }
  
  /***************************************************************************************************************
   * Constructs a {@code KasqTextMessage} object from {@code ObjectInputStream}
   * 
   * @param istream the {@code ObjectInputStream} from which the message will be deserialized
   * 
   * @throws IOException 
   * @throws ClassNotFoundException 
   */
  public KasqTextMessage(ObjectInputStream istream) throws ClassNotFoundException, IOException
  {
    super(istream);
    mBody = (String)istream.readObject();
  }
  
  public String getText() throws JMSException
  {
    return mBody;
  }

  public void setText(String text) throws JMSException
  {
    mBody = text;
  }
  
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
  
  public void clearBody() throws JMSException
  {
    mBody = null;
  }

  @SuppressWarnings("unchecked")
  public <T> T getBody(Class<T> c) throws JMSException
  {
    if (isBodyAssignableTo(c))
      return (T)mBody;
    
    throw new MessageFormatException("Body not assignable to type: " + c.getName());
  }

  @SuppressWarnings("rawtypes")
  public boolean isBodyAssignableTo(Class c) throws JMSException
  {
    return String.class.isAssignableFrom(c);
  }

  public String toPrintableString(int level)
  {
    return super.toPrintableString(level);
  }
}
