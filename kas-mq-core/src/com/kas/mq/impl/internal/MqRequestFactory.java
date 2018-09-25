package com.kas.mq.impl.internal;

import com.kas.infra.utils.Base64Utils;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.impl.IMqGlobals.EQueryType;
import com.kas.mq.impl.MqMessageFactory;
import com.kas.mq.impl.MqTextMessage;

public class MqRequestFactory
{
  static public MqTextMessage createLoginRequest(String user, String pass)
  {
    MqTextMessage message = MqMessageFactory.createTextMessage(null);
    message.setRequestType(ERequestType.cLogin);
    message.setStringProperty(IMqConstants.cKasPropertyLoginUserName, user);
    byte [] b64pass = Base64Utils.encode(pass.getBytes());
    message.setStringProperty(IMqConstants.cKasPropertyLoginPassword, StringUtils.asHexString(b64pass));
    return message;
  }
  
  static public MqTextMessage createGetRequest(String queue, long timeout, long interval)
  {
    MqTextMessage message = MqMessageFactory.createTextMessage(null);
    message.setRequestType(ERequestType.cGet);
    message.setStringProperty(IMqConstants.cKasPropertyGetQueueName, queue);
    message.setLongProperty(IMqConstants.cKasPropertyGetTimeout, timeout);
    message.setLongProperty(IMqConstants.cKasPropertyGetInterval, interval);
    return message;
  }
  
  static public MqTextMessage createDefineQueueRequest(String queue, int threshold)
  {
    MqTextMessage message = MqMessageFactory.createTextMessage(null);
    message.setRequestType(ERequestType.cDefineQueue);
    message.setStringProperty(IMqConstants.cKasPropertyDefqQueueName, queue);
    message.setIntProperty(IMqConstants.cKasPropertyDefqThreshold, threshold);
    return message;
  }
  
  static public MqTextMessage createDeleteQueueRequest(String queue, boolean force)
  {
    MqTextMessage message = MqMessageFactory.createTextMessage(null);
    message.setRequestType(ERequestType.cDeleteQueue);
    message.setStringProperty(IMqConstants.cKasPropertyDelqQueueName, queue);
    message.setBoolProperty(IMqConstants.cKasPropertyDelqForce, force);
    return message;
  }
  
  static public MqTextMessage createQueryQueueRequest(String name, boolean prefix, boolean all, boolean outProps)
  {
    MqTextMessage message = MqMessageFactory.createTextMessage(null);
    message.setRequestType(ERequestType.cQueryQueue);
    message.setStringProperty(IMqConstants.cKasPropertyQryqQueueName, name);
    message.setBoolProperty(IMqConstants.cKasPropertyQryqPrefix, prefix);
    message.setBoolProperty(IMqConstants.cKasPropertyQryqAllData, all);
    message.setBoolProperty(IMqConstants.cKasPropertyQryqOutOnlyProps, outProps);
    return message;
  }
  
  static public MqTextMessage createQueryServerRequest(EQueryType qType)
  {
    MqTextMessage message = MqMessageFactory.createTextMessage(null);
    message.setRequestType(ERequestType.cQueryServer);
    message.setIntProperty(IMqConstants.cKasPropertyQrysQueryType, qType.ordinal());
    return message;
  }
  
  static public MqTextMessage createSystemStateMessage(String qmgr, boolean active)
  {
    MqTextMessage message = MqMessageFactory.createTextMessage(null);
    message.setRequestType(ERequestType.cSysState);
    message.setStringProperty(IMqConstants.cKasPropertySyssQmgrName, qmgr);
    message.setBoolProperty(IMqConstants.cKasPropertySyssActive, active);
    return message;
  }
  
  static public MqTextMessage createRepositoryUpdateMessage(String qmgr, String queue, boolean added)
  {
    MqTextMessage message = MqMessageFactory.createTextMessage(null);
    message.setRequestType(ERequestType.cRepoUpdate);
    message.setStringProperty(IMqConstants.cKasPropertyRepoQmgrName, qmgr);
    message.setStringProperty(IMqConstants.cKasPropertyRepoQueueName, queue);
    message.setBoolProperty(IMqConstants.cKasPropertyRepoOperation, added);
    return message;
  }
  
  static public MqTextMessage createShutdownRequest(String user)
  {
    MqTextMessage message = MqMessageFactory.createTextMessage(null);
    message.setRequestType(ERequestType.cShutdown);
    message.setStringProperty(IMqConstants.cKasPropertyShutUserName, user);
    return message;
  }
}
