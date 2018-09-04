package com.kas.mq.impl;

import com.kas.infra.utils.Base64Utils;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.internal.ERequestType;

public class MqMessageFactory
{
  static public MqObjectMessage createAuthenticationRequest(String user, String pass)
  {
    MqObjectMessage message = new MqObjectMessage();
    message.setStringProperty(IMqConstants.cKasPropertyUserName, user);
    byte [] b64pass = Base64Utils.encode(pass.getBytes());
    message.setStringProperty(IMqConstants.cKasPropertyPassword, StringUtils.asHexString(b64pass));
    message.setRequestType(ERequestType.cAuthenticate);
    return message;
  }
  
  static public MqObjectMessage createDefineQueueRequest(String queue, int threshold)
  {
    MqObjectMessage message = new MqObjectMessage();
    message.setRequestType(ERequestType.cDefineQueue);
    message.setStringProperty(IMqConstants.cKasPropertyDefqQueueName, queue);
    message.setIntProperty(IMqConstants.cKasPropertyDefqThreshold, threshold);
    return message;
  }
  
  static public MqObjectMessage createDeleteQueueRequest(String queue, boolean force)
  {
    MqObjectMessage message = new MqObjectMessage();
    message.setRequestType(ERequestType.cDeleteQueue);
    message.setStringProperty(IMqConstants.cKasPropertyDelqQueueName, queue);
    message.setBoolProperty(IMqConstants.cKasPropertyDelqForce, force);
    return message;
  }
  
  static public MqObjectMessage createQueryQueueRequest(String name, boolean prefix, boolean all)
  {
    MqObjectMessage message = new MqObjectMessage();
    message.setRequestType(ERequestType.cQueryQueue);
    message.setStringProperty(IMqConstants.cKasPropertyQryqQueueName, name);
    message.setBoolProperty(IMqConstants.cKasPropertyQryqPrefix, prefix);
    message.setBoolProperty(IMqConstants.cKasPropertyQryqAllData, all);
    return message;
  }
  
  static public MqObjectMessage createGetRequest(String queue, long timeout, long interval)
  {
    MqObjectMessage message = new MqObjectMessage();
    message.setRequestType(ERequestType.cGet);
    message.setStringProperty(IMqConstants.cKasPropertyGetQueueName, queue);
    message.setLongProperty(IMqConstants.cKasPropertyGetTimeout, timeout);
    message.setLongProperty(IMqConstants.cKasPropertyGetInterval, interval);
    return message;
  }
  
  static public MqObjectMessage createShutdownRequest()
  {
    MqObjectMessage message = new MqObjectMessage();
    message.setRequestType(ERequestType.cShutdown);
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
   * @param int An integer value representing how successful was the request.
   * @param response The message sent by the server to the client which describes the response code.
   * @return a {@link MqResponse}
   */
  static public MqObjectMessage createResponse(int code, String response)
  {
    MqObjectMessage message = new MqObjectMessage();
    message.setRequestType(ERequestType.cUnknown);
    message.setIntProperty(IMqConstants.cKasPropertyResponseCode, code);
    message.setStringProperty(IMqConstants.cKasPropertyResponseDesc, response);
    return message;
  }
}
