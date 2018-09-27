package com.kas.mq.impl.internal;

import com.kas.infra.base.Properties;
import com.kas.infra.utils.Base64Utils;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.impl.IMqGlobals.EQueryType;
import com.kas.mq.impl.MqMessageFactory;
import com.kas.mq.impl.MqMessage;

public class MqRequestFactory
{
  static public MqMessage createLoginRequest(String user, String pass)
  {
    MqMessage message = MqMessageFactory.createMessage();
    message.setRequestType(ERequestType.cLogin);
    message.setStringProperty(IMqConstants.cKasPropertyLoginUserName, user);
    byte [] b64pass = Base64Utils.encode(pass.getBytes());
    message.setStringProperty(IMqConstants.cKasPropertyLoginPassword, StringUtils.asHexString(b64pass));
    return message;
  }
  
  static public MqMessage createGetRequest(String queue, long timeout, long interval)
  {
    MqMessage message = MqMessageFactory.createMessage();
    message.setRequestType(ERequestType.cGet);
    message.setStringProperty(IMqConstants.cKasPropertyGetQueueName, queue);
    message.setLongProperty(IMqConstants.cKasPropertyGetTimeout, timeout);
    message.setLongProperty(IMqConstants.cKasPropertyGetInterval, interval);
    return message;
  }
  
  static public MqMessage createDefineQueueRequest(String queue, int threshold)
  {
    MqMessage message = MqMessageFactory.createMessage();
    message.setRequestType(ERequestType.cDefineQueue);
    message.setStringProperty(IMqConstants.cKasPropertyDefqQueueName, queue);
    message.setIntProperty(IMqConstants.cKasPropertyDefqThreshold, threshold);
    return message;
  }
  
  static public MqMessage createDeleteQueueRequest(String queue, boolean force)
  {
    MqMessage message = MqMessageFactory.createMessage();
    message.setRequestType(ERequestType.cDeleteQueue);
    message.setStringProperty(IMqConstants.cKasPropertyDelqQueueName, queue);
    message.setBoolProperty(IMqConstants.cKasPropertyDelqForce, force);
    return message;
  }
  
  static public MqMessage createQueryServerRequest(EQueryType qType, Properties qProps)
  {
    MqMessage message = MqMessageFactory.createMessage();
    message.setRequestType(ERequestType.cQueryServer);
    message.setIntProperty(IMqConstants.cKasPropertyQrysQueryType, qType.ordinal());
    message.setSubset(qProps);
    return message;
  }
  
  static public MqMessage createSystemStateMessage(String qmgr, boolean active)
  {
    MqMessage message = MqMessageFactory.createMessage();
    message.setRequestType(ERequestType.cSysState);
    message.setStringProperty(IMqConstants.cKasPropertySyssQmgrName, qmgr);
    message.setBoolProperty(IMqConstants.cKasPropertySyssActive, active);
    return message;
  }
  
  static public MqMessage createRepositoryUpdateMessage(String qmgr, String queue, boolean added)
  {
    MqMessage message = MqMessageFactory.createMessage();
    message.setRequestType(ERequestType.cRepoUpdate);
    message.setStringProperty(IMqConstants.cKasPropertyRepoQmgrName, qmgr);
    message.setStringProperty(IMqConstants.cKasPropertyRepoQueueName, queue);
    message.setBoolProperty(IMqConstants.cKasPropertyRepoOperation, added);
    return message;
  }
  
  static public MqMessage createShutdownRequest(String user)
  {
    MqMessage message = MqMessageFactory.createMessage();
    message.setRequestType(ERequestType.cShutdown);
    message.setStringProperty(IMqConstants.cKasPropertyShutUserName, user);
    return message;
  }
}
