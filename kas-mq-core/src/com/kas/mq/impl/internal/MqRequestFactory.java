package com.kas.mq.impl.internal;

import com.kas.infra.base.Properties;
import com.kas.infra.utils.Base64Utils;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.impl.IMqGlobals.EQueryType;
import com.kas.mq.impl.MqMessageFactory;
import com.kas.mq.impl.MqStringMessage;

public class MqRequestFactory
{
  static public MqStringMessage createLoginRequest(String user, String pass)
  {
    MqStringMessage message = MqMessageFactory.createStringMessage(null);
    message.setRequestType(ERequestType.cLogin);
    message.setStringProperty(IMqConstants.cKasPropertyLoginUserName, user);
    byte [] b64pass = Base64Utils.encode(pass.getBytes());
    message.setStringProperty(IMqConstants.cKasPropertyLoginPassword, StringUtils.asHexString(b64pass));
    return message;
  }
  
  static public MqStringMessage createGetRequest(String queue, long timeout, long interval)
  {
    MqStringMessage message = MqMessageFactory.createStringMessage(null);
    message.setRequestType(ERequestType.cGet);
    message.setStringProperty(IMqConstants.cKasPropertyGetQueueName, queue);
    message.setLongProperty(IMqConstants.cKasPropertyGetTimeout, timeout);
    message.setLongProperty(IMqConstants.cKasPropertyGetInterval, interval);
    return message;
  }
  
  static public MqStringMessage createDefineQueueRequest(String queue, int threshold)
  {
    MqStringMessage message = MqMessageFactory.createStringMessage(null);
    message.setRequestType(ERequestType.cDefineQueue);
    message.setStringProperty(IMqConstants.cKasPropertyDefqQueueName, queue);
    message.setIntProperty(IMqConstants.cKasPropertyDefqThreshold, threshold);
    return message;
  }
  
  static public MqStringMessage createDeleteQueueRequest(String queue, boolean force)
  {
    MqStringMessage message = MqMessageFactory.createStringMessage(null);
    message.setRequestType(ERequestType.cDeleteQueue);
    message.setStringProperty(IMqConstants.cKasPropertyDelqQueueName, queue);
    message.setBoolProperty(IMqConstants.cKasPropertyDelqForce, force);
    return message;
  }
  
  static public MqStringMessage createQueryServerRequest(EQueryType qType, Properties qProps)
  {
    MqStringMessage message = MqMessageFactory.createStringMessage(null);
    message.setRequestType(ERequestType.cQueryServer);
    message.setIntProperty(IMqConstants.cKasPropertyQrysQueryType, qType.ordinal());
    message.setSubset(qProps);
    return message;
  }
  
  static public MqStringMessage createSystemStateMessage(String qmgr, boolean active)
  {
    MqStringMessage message = MqMessageFactory.createStringMessage(null);
    message.setRequestType(ERequestType.cSysState);
    message.setStringProperty(IMqConstants.cKasPropertySyssQmgrName, qmgr);
    message.setBoolProperty(IMqConstants.cKasPropertySyssActive, active);
    return message;
  }
  
  static public MqStringMessage createRepositoryUpdateMessage(String qmgr, String queue, boolean added)
  {
    MqStringMessage message = MqMessageFactory.createStringMessage(null);
    message.setRequestType(ERequestType.cRepoUpdate);
    message.setStringProperty(IMqConstants.cKasPropertyRepoQmgrName, qmgr);
    message.setStringProperty(IMqConstants.cKasPropertyRepoQueueName, queue);
    message.setBoolProperty(IMqConstants.cKasPropertyRepoOperation, added);
    return message;
  }
  
  static public MqStringMessage createShutdownRequest(String user)
  {
    MqStringMessage message = MqMessageFactory.createStringMessage(null);
    message.setRequestType(ERequestType.cShutdown);
    message.setStringProperty(IMqConstants.cKasPropertyShutUserName, user);
    return message;
  }
}
