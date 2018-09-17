package com.kas.mq.impl.internal;

import com.kas.infra.utils.Base64Utils;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.MqMessageFactory;
import com.kas.mq.impl.MqObjectMessage;

public class MqRequestFactory
{
  static public IMqMessage<?> createLoginRequest(String user, String pass)
  {
    MqObjectMessage message = MqMessageFactory.createObjectMessage(null);
    message.setStringProperty(IMqConstants.cKasPropertyUserName, user);
    byte [] b64pass = Base64Utils.encode(pass.getBytes());
    message.setStringProperty(IMqConstants.cKasPropertyPassword, StringUtils.asHexString(b64pass));
    message.setRequestType(ERequestType.cLogin);
    return message;
  }
  
  static public IMqMessage<?> createGetRequest(String queue, long timeout, long interval)
  {
    MqObjectMessage message = MqMessageFactory.createObjectMessage(null);
    message.setRequestType(ERequestType.cGet);
    message.setStringProperty(IMqConstants.cKasPropertyGetQueueName, queue);
    message.setLongProperty(IMqConstants.cKasPropertyGetTimeout, timeout);
    message.setLongProperty(IMqConstants.cKasPropertyGetInterval, interval);
    return message;
  }
  
  static public IMqMessage<?> createDefineQueueRequest(String queue, int threshold)
  {
    MqObjectMessage message = MqMessageFactory.createObjectMessage(null);
    message.setRequestType(ERequestType.cDefineQueue);
    message.setStringProperty(IMqConstants.cKasPropertyDefqQueueName, queue);
    message.setIntProperty(IMqConstants.cKasPropertyDefqThreshold, threshold);
    return message;
  }
  
  static public IMqMessage<?> createDeleteQueueRequest(String queue, boolean force)
  {
    MqObjectMessage message = MqMessageFactory.createObjectMessage(null);
    message.setRequestType(ERequestType.cDeleteQueue);
    message.setStringProperty(IMqConstants.cKasPropertyDelqQueueName, queue);
    message.setBoolProperty(IMqConstants.cKasPropertyDelqForce, force);
    return message;
  }
  
  static public IMqMessage<?> createQueryQueueRequest(String name, boolean prefix, boolean all)
  {
    MqObjectMessage message = MqMessageFactory.createObjectMessage(null);
    message.setRequestType(ERequestType.cQueryQueue);
    message.setStringProperty(IMqConstants.cKasPropertyQryqQueueName, name);
    message.setBoolProperty(IMqConstants.cKasPropertyQryqPrefix, prefix);
    message.setBoolProperty(IMqConstants.cKasPropertyQryqAllData, all);
    return message;
  }
  
  static public IMqMessage<?> createSystemStateMessage(String qmgr, boolean active)
  {
    MqObjectMessage message = MqMessageFactory.createObjectMessage(null);
    message.setRequestType(ERequestType.cSysState);
    message.setStringProperty(IMqConstants.cKasPropertySyssQmgrName, qmgr);
    message.setBoolProperty(IMqConstants.cKasPropertySyssActive, active);
    return message;
  }
  
  static public IMqMessage<?> createRepositoryUpdateMessage(String qmgr, String queue, boolean added)
  {
    MqObjectMessage message = MqMessageFactory.createObjectMessage(null);
    message.setRequestType(ERequestType.cRepoUpdate);
    message.setStringProperty(IMqConstants.cKasPropertyRepoQmgrName, qmgr);
    message.setStringProperty(IMqConstants.cKasPropertyRepoQueueName, queue);
    message.setBoolProperty(IMqConstants.cKasPropertyRepoOperation, added);
    return message;
  }
  
  static public IMqMessage<?> createShutdownRequest(String user)
  {
    MqObjectMessage message = MqMessageFactory.createObjectMessage(null);
    message.setRequestType(ERequestType.cShutdown);
    message.setStringProperty(IMqConstants.cKasPropertyShutUserName, user);
    return message;
  }
}
