package com.kas.mq.impl;

import com.kas.mq.internal.ERequestType;

public class MqMessageFactory
{
  static public MqObjectMessage createAuthenticationRequest(String user, String pass)
  {
    MqObjectMessage message = new MqObjectMessage();
    message.setStringProperty(IMqConstants.cKasPropertyUserName, user);
    message.setStringProperty(IMqConstants.cKasPropertyPassword, pass);
    message.setRequestType(ERequestType.cAuthenticate);
    return message;
  }
  
  static public MqObjectMessage createOpenRequest(String queue)
  {
    MqObjectMessage message = new MqObjectMessage();
    message.setRequestType(ERequestType.cOpenQueue);
    message.setStringProperty(IMqConstants.cKasPropertyQueueName, queue);
    return message;
  }
  
  static public MqObjectMessage createCloseRequest(String queue)
  {
    MqObjectMessage message = new MqObjectMessage();
    message.setRequestType(ERequestType.cCloseQueue);
    return message;
  }
  
  static public MqObjectMessage createDefineRequest(String queue)
  {
    MqObjectMessage message = new MqObjectMessage();
    message.setRequestType(ERequestType.cDefineQueue);
    message.setStringProperty(IMqConstants.cKasPropertyQueueName, queue);
    return message;
  }
  
  static public MqObjectMessage createDeleteRequest(String queue)
  {
    MqObjectMessage message = new MqObjectMessage();
    message.setRequestType(ERequestType.cDeleteQueue);
    message.setStringProperty(IMqConstants.cKasPropertyQueueName, queue);
    return message;
  }
  
  static public MqObjectMessage createGetRequest(int priority, long timeout, long interval)
  {
    MqObjectMessage message = new MqObjectMessage();
    message.setRequestType(ERequestType.cGet);
    message.setIntProperty(IMqConstants.cKasPropertyGetPriority, priority);
    message.setLongProperty(IMqConstants.cKasPropertyGetTimeout, timeout);
    message.setLongProperty(IMqConstants.cKasPropertyGetInterval, interval);
    return message;
  }
  
  /**
   * Create a text message, with a body containing {@code text}
   * 
   * @param text The message body
   * @return a new {@link MqTextMessage}
   */
  static public MqTextMessage createTextMessage(String text)
  {
    MqTextMessage message = new MqTextMessage();
    message.setBody(text);
    message.setRequestType(ERequestType.cPut);
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
    message.setRequestType(ERequestType.cPut);
    return message;
  }
  
  /**
   * Create a response message
   * 
   * @param code The code of the response. An integer value representing how successful was the request.
   * @param response The message sent by the server to the client which describes the response code.
   * @return a {@link MqResponse}
   */
  static public MqObjectMessage createResponse(EMqResponseCode code, String response)
  {
    MqObjectMessage message = new MqObjectMessage();
    message.setRequestType(ERequestType.cUnknown);
    message.setIntProperty(IMqConstants.cKasPropertyResponseCode, code.ordinal());
    message.setStringProperty(IMqConstants.cKasPropertyResponseDesc, response);
    return message;
  }
}
