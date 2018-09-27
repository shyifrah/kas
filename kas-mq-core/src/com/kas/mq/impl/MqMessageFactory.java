package com.kas.mq.impl;

import com.kas.mq.impl.internal.EMqCode;
import com.kas.mq.impl.internal.MqResponse;

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
  
  /**
   * Create a dummy text message as a response to {@code request}
   * 
   * @param request A {@link IMqMessage} to which we reply
   * @param code A {@link EMqCode} representing how successful was the request.
   * @param val An integer value that describes the {@code code}
   * @param desc The message sent by the server to the client which describes the response code.
   * @return a {@link MqStringMessage}
   */
  static public MqStringMessage createResponse(IMqMessage<?> request, EMqCode code, int val, String desc)
  {
    MqStringMessage message = new MqStringMessage();
    message.setReferenceId(request.getMessageId());
    message.setResponse(new MqResponse(code, val, desc));
    return message;
  }
}
