package com.kas.q.ext;

public interface IKasqConstants
{
  static final String cKasqEyeCatcher      = "KasQ_EyeCatcher";
  
  static final String cPropertyResponseMessage = "KasQ_ResponseMessage";
  
  static final String cPropertyResponseCode = "KasQ_ResponseCode";
  static final int    cPropertyResponseCode_Okay = 0;
  static final int    cPropertyResponseCode_Fail = -1;
  
  static final String cPropertyRequestType = "KasQ_RequestType";
  static final int    cPropertyRequestType_Put          = 0;
  static final int    cPropertyRequestType_Get          = 1;
  static final int    cPropertyRequestType_Authenticate = 2;
  static final int    cPropertyRequestType_Shutdown     = 3;
  static final int    cPropertyRequestType_Define       = 4;
  
  static final String cPropertyDestinationName = "KasQ_DestinationName";
  
  static final String cPropertyDestinationType = "KasQ_DestinationType";
  static final int    cPropertyDestinationType_Queue = 1;
  static final int    cPropertyDestinationType_Topic = 2;
  
  static final String cPropertyMessageSelector = "KasQ_MessageSelector";
  static final String cPropertyNoLocal         = "KasQ_NoLocal";
  
  static final String cPropertyConsumerQueue   = "KasQ_ConsumerQueue";
  static final String cPropertyConsumerSession = "KasQ_ConsumerSession";
  
  static final String cPropertyRequestRepetitions = "KasQ_RequestRepetitions";
  
  static final String cPropertyUserName = "KasQ_UserName";
  static final String cPropertyPassword = "KasQ_Password";
  
  static final String cPropertyAdminMessage = "KasQ_Admin";
}
