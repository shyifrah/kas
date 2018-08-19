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
  
  static public MqMessage createShowInfoRequest()
  {
    MqMessage message = new MqMessage();
    message.setRequestType(ERequestType.cShowInfo);
    return message;
  }
  
  static public MqMessage createGetRequest(String queue)
  {
    MqMessage message = new MqMessage();
    message.setRequestType(ERequestType.cGet);
    message.setQueueName(queue);
    return message;
  }
  
  /**
   * Create a response message
   * 
   * @param code The code of the response. An integer value representing how successful was the request.
   * @param response The message sent by the server to the client which describes the response code.
   * @return a {@link MqResponseMessage}
   */
  static public MqResponseMessage createResponse(int code, String response)
  {
    MqResponseMessage message = new MqResponseMessage();
    message.setResponse(code, response);
    message.setRequestType(ERequestType.cUnknown);
    return message;
  }
}
