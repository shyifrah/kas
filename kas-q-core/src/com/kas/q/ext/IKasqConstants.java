package com.kas.q.ext;

public interface IKasqConstants
{
  static final String cKasqEyeCatcher = "JMS_KasQ_EyeCatcher";
  
  static final String cPropertyResponseMessage = "JMS_KasQ_ResponseMessage";
  static final String cPropertyResponseCode    = "JMS_KasQ_ResponseCode";
  static final int    cPropertyResponseCode_Okay = 0;
  static final int    cPropertyResponseCode_Fail = 1;
  
  static final String cPropertyRequestType = "JMS_KasQ_RequestType";
  static final int    cPropertyRequestType_Put          = 0;
  static final int    cPropertyRequestType_Get          = 1;
  static final int    cPropertyRequestType_Authenticate = 2;
  static final int    cPropertyRequestType_Shutdown     = 3;
  static final int    cPropertyRequestType_Define       = 4;
  static final int    cPropertyRequestType_Locate       = 5;
  static final int    cPropertyRequestType_MetaData     = 6;
  
  static final String cPropertyDestinationName = "JMS_KasQ_DestinationName";
  static final String cPropertyDestinationType = "JMS_KasQ_DestinationType";
  
  static final String cPropertyConsumerMessageSelector = "JMS_KasQ_ConsumerMessageSelector";
  static final String cPropertyConsumerNoLocal         = "JMS_KasQ_ConsumerNoLocal";
  static final String cPropertyConsumerQueue           = "JMS_KasQ_ConsumerQueue";
  static final String cPropertyConsumerSession         = "JMS_KasQ_ConsumerSession";
  
  static final String cPropertyProducerTimestamp       = "JMS_KasQ_ProducerTimestamp";
  static final String cPropertyProducerSession         = "JMS_KasQ_ProducerSession";
  static final String cPropertyProducerDeliveryDelay   = "JMS_KasQ_ProducerDeliveryDelay";
  
  static final String cPropertyUserName = "JMS_KasQ_UserName";
  static final String cPropertyPassword = "JMS_KasQ_Password";
  
  static final String cPropertyAdminMessage = "JMS_KasQ_Admin";
  
  static final String cPropertyMetaData = "JMS_KasQ_MetaData";
}
