package com.kas.mq.impl;

import com.kas.mq.internal.ERequestType;

public class MqMessageFactory
{
  static public MqMessage createAuthenticationRequest(String user, String pass)
  {
    MqMessage message = new MqMessage();
    message.setStringProperty(IMqConstants.cKasPropertyUserName, user);
    message.setStringProperty(IMqConstants.cKasPropertyPassword, pass);
    message.setRequestType(ERequestType.cAuthenticate);
    return message;
  }
  
  static public MqMessage createOpenRequest(String queue)
  {
    MqMessage message = new MqMessage();
    message.setRequestType(ERequestType.cOpenQueue);
    message.setStringProperty(IMqConstants.cKasPropertyQueueName, queue);
    return message;
  }
  
  static public MqMessage createCloseRequest(String queue)
  {
    MqMessage message = new MqMessage();
    message.setRequestType(ERequestType.cCloseQueue);
    return message;
  }
  
  static public MqMessage createDefineRequest(String queue)
  {
    MqMessage message = new MqMessage();
    message.setRequestType(ERequestType.cDefineQueue);
    message.setStringProperty(IMqConstants.cKasPropertyQueueName, queue);
    return message;
  }
  
  static public MqMessage createDeleteRequest(String queue)
  {
    MqMessage message = new MqMessage();
    message.setRequestType(ERequestType.cDeleteQueue);
    message.setStringProperty(IMqConstants.cKasPropertyQueueName, queue);
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
    message.setStringProperty(IMqConstants.cKasPropertyQueueName, queue);
    return message;
  }
  
  /**
   * Create a response message
   * 
   * @param code The code of the response. An integer value representing how successful was the request.
   * @param response The message sent by the server to the client which describes the response code.
   * @return a {@link MqResponse}
   */
  static public MqMessage createResponse(int code, String response)
  {
    MqMessage message = new MqMessage();
    message.setRequestType(ERequestType.cUnknown);
    message.setIntProperty(IMqConstants.cKasPropertyResponseCode, code);
    message.setStringProperty(IMqConstants.cKasPropertyResponseDesc, response);
    return message;
  }
}
