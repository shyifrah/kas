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
  
  static public MqResponseMessage createResponse(int code, String response)
  {
    MqResponseMessage message = new MqResponseMessage();
    message.setResponse(code, response);
    message.setRequestType(ERequestType.cAuthenticate);
    return message;
  }
}
