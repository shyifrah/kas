package com.kas.mq.impl;

import com.kas.mq.internal.ERequestType;

public class MqMessageFactory
{
  static public MqMessage createAuthenticationRequest(String user, String pass)
  {
    MqMessage message = new MqMessage();
    message.setCredentials(user, pass);
    message.setRequestType(ERequestType.cAuthenticate);
    return message;
  }
  
  static public MqMessage createOpenRequest(String queue)
  {
    MqMessage message = new MqMessage();
    message.setRequestType(ERequestType.cOpenQueue);
    message.setQueueName(queue);
    return message;
  }
  
  static public MqMessage createCloseRequest(String queue)
  {
    MqMessage message = new MqMessage();
    message.setRequestType(ERequestType.cCloseQueue);
    message.setQueueName(queue);
    return message;
  }
  
  static public MqMessage createGetRequest(String queue)
  {
    MqMessage message = new MqMessage();
    message.setRequestType(ERequestType.cGet);
    message.setQueueName(queue);
    return message;
  }
  
  static public MqResponseMessage createResponse(int code, String response)
  {
    MqResponseMessage message = new MqResponseMessage();
    message.setResponse(code, response);
    message.setRequestType(ERequestType.cAuthenticate);
    return message;
  }
}
