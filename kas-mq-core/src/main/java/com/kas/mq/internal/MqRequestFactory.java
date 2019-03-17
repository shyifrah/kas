package com.kas.mq.internal;

import com.kas.infra.base.Properties;
import com.kas.infra.base.UniqueId;
import com.kas.infra.typedef.StringList;
import com.kas.infra.utils.Base64Utils;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.impl.EQueryType;
import com.kas.mq.impl.messages.MqMessage;
import com.kas.mq.impl.messages.MqMessageFactory;

/**
 * Internal factory for generating various types of requests
 * 
 * @author Pippo
 */
public class MqRequestFactory
{
  static public MqMessage createDefineGroupRequest(String group, String desc)
  {
    MqMessage message = MqMessageFactory.createMessage();
    message.setRequestType(ERequestType.cDefineGroup);
    message.setStringProperty(IMqConstants.cKasPropertyDefGroupName, group);
    message.setStringProperty(IMqConstants.cKasPropertyDefGroupDesc, desc);
    return message;
  }
  
  static public MqMessage createDeleteGroupRequest(String group)
  {
    MqMessage message = MqMessageFactory.createMessage();
    message.setRequestType(ERequestType.cDeleteGroup);
    message.setStringProperty(IMqConstants.cKasPropertyDelGroupName, group);
    return message;
  }
  
  static public MqMessage createDefineUserRequest(String user, String pass, String desc, StringList groups)
  {
    MqMessage message = MqMessageFactory.createMessage();
    message.setRequestType(ERequestType.cDefineUser);
    message.setStringProperty(IMqConstants.cKasPropertyDefUserName, user);
    message.setStringProperty(IMqConstants.cKasPropertyDefUserPass, pass);
    message.setStringProperty(IMqConstants.cKasPropertyDefUserDesc, desc);
    message.setStringProperty(IMqConstants.cKasPropertyDefUserGrps, groups.toString());
    return message;
  }
  
  static public MqMessage createDeleteUserRequest(String user)
  {
    MqMessage message = MqMessageFactory.createMessage();
    message.setRequestType(ERequestType.cDeleteUser);
    message.setStringProperty(IMqConstants.cKasPropertyDelUserName, user);
    return message;
  }
  
  static public MqMessage createLoginRequest(String user, String pass, String appName)
  {
    MqMessage message = MqMessageFactory.createMessage();
    message.setRequestType(ERequestType.cLogin);
    message.setStringProperty(IMqConstants.cKasPropertyLoginUserName, user);
    byte [] b64pass = Base64Utils.encode(pass.getBytes());
    message.setStringProperty(IMqConstants.cKasPropertyLoginPassword, StringUtils.asHexString(b64pass));
    message.setStringProperty(IMqConstants.cKasPropertyLoginAppName, appName);
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
  
  static public MqMessage createDefineQueueRequest(String queue, String desc, int threshold, EQueueDisp disp)
  {
    MqMessage message = MqMessageFactory.createMessage();
    message.setRequestType(ERequestType.cDefineQueue);
    message.setStringProperty(IMqConstants.cKasPropertyDefQueueName, queue);
    message.setStringProperty(IMqConstants.cKasPropertyDefQueueDesc, desc);
    message.setIntProperty(IMqConstants.cKasPropertyDefThreshold, threshold);
    message.setStringProperty(IMqConstants.cKasPropertyDefDisposition, disp.name());
    return message;
  }
  
  static public MqMessage createAlterQueueRequest(String queue, Properties qProps)
  {
    MqMessage message = MqMessageFactory.createMessage();
    message.setRequestType(ERequestType.cAlterQueue);
    message.setStringProperty(IMqConstants.cKasPropertyAltQueueName, queue);
    message.setSubset(qProps);
    return message;
  }
  
  static public MqMessage createDeleteQueueRequest(String queue, boolean force)
  {
    MqMessage message = MqMessageFactory.createMessage();
    message.setRequestType(ERequestType.cDeleteQueue);
    message.setStringProperty(IMqConstants.cKasPropertyDelQueueName, queue);
    message.setBoolProperty(IMqConstants.cKasPropertyDelForce, force);
    return message;
  }
  
  static public MqMessage createQueryServerRequest(EQueryType qType, Properties qProps)
  {
    MqMessage message = MqMessageFactory.createMessage();
    message.setRequestType(ERequestType.cQueryServer);
    message.setIntProperty(IMqConstants.cKasPropertyQueryType, qType.ordinal());
    message.setSubset(qProps);
    return message;
  }
  
  static public MqMessage createTermConnRequest(UniqueId id)
  {
    MqMessage message = MqMessageFactory.createMessage();
    message.setRequestType(ERequestType.cTermConn);
    message.setStringProperty(IMqConstants.cKasPropertyTermConnId, id.toString());
    return message;
  }
  
  static public MqMessage createTermSessRequest(UniqueId id)
  {
    MqMessage message = MqMessageFactory.createMessage();
    message.setRequestType(ERequestType.cTermSess);
    message.setStringProperty(IMqConstants.cKasPropertyTermSessId, id.toString());
    return message;
  }
  
  static public MqMessage createTermServerRequest(String user)
  {
    MqMessage message = MqMessageFactory.createMessage();
    message.setRequestType(ERequestType.cTermServer);
    message.setStringProperty(IMqConstants.cKasPropertyTermUserName, user);
    return message;
  }
  
  static public MqMessage createSystemStateMessage(String qmgr, boolean active)
  {
    MqMessage message = MqMessageFactory.createMessage();
    message.setRequestType(ERequestType.cNotifySysState);
    message.setStringProperty(IMqConstants.cKasPropertySyssQmgrName, qmgr);
    message.setBoolProperty(IMqConstants.cKasPropertySyssActive, active);
    return message;
  }
  
  static public MqMessage createRepositoryUpdateMessage(String qmgr, String queue, boolean added)
  {
    MqMessage message = MqMessageFactory.createMessage();
    message.setRequestType(ERequestType.cNotifyRepoUpdate);
    message.setStringProperty(IMqConstants.cKasPropertyRepoQmgrName, qmgr);
    message.setStringProperty(IMqConstants.cKasPropertyRepoQueueName, queue);
    message.setBoolProperty(IMqConstants.cKasPropertyRepoOperation, added);
    return message;
  }
}
