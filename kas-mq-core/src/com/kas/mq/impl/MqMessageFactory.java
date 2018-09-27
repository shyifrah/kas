package com.kas.mq.impl;

public class MqMessageFactory
{
  /**
   * Create a string message, with a body containing {@code text}
   * 
   * @param text The message body
   * @return a new {@link MqStringMessage}
   */
  static public MqStringMessage createStringMessage(String text)
  {
    MqStringMessage message = new MqStringMessage();
    message.setBody(text);
    return message;
  }
  
  /**
   * Create a text message, with a body containing {@code text}
   * 
   * @param text The message body
   * @return a new {@link MqObjectMessage}
   */
  static public MqObjectMessage createObjectMessage(Object object)
  {
    MqObjectMessage message = new MqObjectMessage();
    message.setBody(object);
    return message;
  }
}
