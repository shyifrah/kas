package com.kas.mq.impl.messages;

import java.io.Serializable;
import java.util.Map;
import com.kas.comm.serializer.Deserializer;
import com.kas.comm.serializer.EClassId;

public class MqMessageFactory
{
  static
  {
    Deserializer.getInstance().register(MqMessage.class, EClassId.cClassMqMessage);
    Deserializer.getInstance().register(MqMapMessage.class, EClassId.cClassMqMapMessage);
    Deserializer.getInstance().register(MqObjectMessage.class, EClassId.cClassMqObjectMessage);
    Deserializer.getInstance().register(MqStreamMessage.class, EClassId.cClassMqStreamMessage);
    Deserializer.getInstance().register(MqStringMessage.class, EClassId.cClassMqStringMessage);
    Deserializer.getInstance().register(MqBytesMessage.class, EClassId.cClassMqBytesMessage);
  }
  
  /**
   * Create a no-body message
   * 
   * @return a new {@link MqMessage}
   */
  static public MqMessage createMessage()
  {
    return new MqMessage();
  }
  
  /**
   * Create a string message, with a body containing {@code body}
   * 
   * @param body The message body
   * @return a new {@link MqStringMessage}
   */
  static public MqStringMessage createStringMessage(String body)
  {
    MqStringMessage message = new MqStringMessage();
    message.setBody(body);
    return message;
  }
  
  /**
   * Create a Object message, with a body containing {@code body}
   * 
   * @param body The message body
   * @return a new {@link MqObjectMessage}
   */
  static public MqObjectMessage createObjectMessage(Serializable body)
  {
    MqObjectMessage message = new MqObjectMessage();
    message.setBody(body);
    return message;
  }
  
  /**
   * Create a byte [] message, with a body containing {@code body}
   * 
   * @param body The message body
   * @return a new {@link MqBytesMessage}
   */
  static public MqBytesMessage createBytesMessage(byte [] body)
  {
    MqBytesMessage message = new MqBytesMessage();
    message.setBody(body);
    return message;
  }
  
  /**
   * Create a map message, with a body containing {@code body}
   * 
   * @param body The message body
   * @return a new {@link MqMapMessage}
   */
  static public MqMapMessage createMapMessage(Map<?, ?> body)
  {
    MqMapMessage message = new MqMapMessage(body);
    return message;
  }
  
  /**
   * Create a stream message, without a body
   * 
   * @return a new {@link MqStreamMessage}
   */
  static public MqStreamMessage createStreamMessage()
  {
    MqStreamMessage message = new MqStreamMessage();
    return message;
  }
}
