package com.kas.q.ext;

public interface IKasqConstants
{
  static final String cKasqEyeCatcher      = "KasQ_EyeCatcher";
  
  static final String cPropertyResponseMessage = "KasQ_ResponseMessage";
  static final String cPropertyResponseCode    = "KasQ_ResponseCode";
  static final int    cPropertyResponseCode_Okay = 0;
  static final int    cPropertyResponseCode_Fail = -1;
  
  static final String cPropertyRequestType = "KasQ_RequestType";
  static final int    cPropertyRequestType_Put          = 0;
  static final int    cPropertyRequestType_Get          = 1;
  static final int    cPropertyRequestType_Authenticate = 2;
  static final int    cPropertyRequestType_Shutdown     = 3;
  static final int    cPropertyRequestType_Define       = 4;
  static final int    cPropertyRequestType_Locate       = 5;
  static final int    cPropertyRequestType_Delete       = 6;
  
  static final String cPropertyDestinationName = "KasQ_DestinationName";
  static final String cPropertyDestinationType = "KasQ_DestinationType";
  
  static final String cPropertyConsumerMessageSelector = "KasQ_ConsumerMessageSelector";
  static final String cPropertyConsumerNoLocal         = "KasQ_ConsumerNoLocal";
  static final String cPropertyConsumerQueue           = "KasQ_ConsumerQueue";
  static final String cPropertyConsumerSession         = "KasQ_ConsumerSession";
  
  static final String cPropertyProducerTimestamp       = "KasQ_ProducerTimestamp";
  static final String cPropertyProducerSession         = "KasQ_ProducerSession";
  static final String cPropertyProducerDeliveryDelay   = "KasQ_ProducerDeliveryDelay";
  
  static final String cPropertyUserName = "KasQ_UserName";
  static final String cPropertyPassword = "KasQ_Password";
  
  static final String cPropertyAdminMessage = "KasQ_Admin";
}
