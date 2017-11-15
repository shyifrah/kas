package com.kas.q.ext;

public interface IKasqConstants
{
  static final String cKasqEyeCatcher      = "KasQ_EyeCatcher";
  static final String cPropertyRequestType = "KasQ_RequestType";
  
  static final String cPropertyResponseMessage = "KasQ_ResponseMessage";
  static final String cPropertyResponseCode = "KasQ_ResponseCode";
  static final int    cPropertyResponseCode_Okay = 0;
  static final int    cPropertyResponseCode_Fail = -1;
  
  static final int    cPropertyRequestType_Authenticate = 1;
  static final String cPropertyUserName = "KasQ_UserName";
  static final String cPropertyPassword = "KasQ_Password";
  
  static final int    cPropertyRequestType_Shutdown = 2;
  static final String cPropertyAdminMessage = "KasQ_Admin";
  
  static final int    cPropertyRequestType_Consume = 3;
  static final String cPropertyDestinationName= "KasQ_DestinationName";
  static final String cPropertyDestinationType = "KasQ_DestinationType";
  static final int    cPropertyDestinationType_Queue = 1;
  static final int    cPropertyDestinationType_Topic = 2;
  static final String cPropertyMessageSelector= "KasQ_MessageSelector";
  static final String cPropertyNoLocal = "KasQ_NoLocal";
  
  /*
   * 
   * 
  
  static final String cKasqAuthenticateRequest = "KasQ_Authenticate";
  static final String cKasqShutdownRequest     = "KasQ_Shutdown";
  
  
  
  static final String cKasqResponseCode = "KasQ_ResponseCode";
  static final int    cKasqResponseCode_Okay = 0;
  static final int    cKasqResponseCode_Fail = -1;
  
  static final String cKasqResponseDescription = "KasQ_ResponseDescription";
  */
}
