package com.kas.q.server.req;

import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.ext.IKasqConstants;
import com.kas.q.ext.IKasqMessage;

public class RequestFactory
{
  private static ILogger sLogger = LoggerFactory.getLogger(RequestFactory.class);
  
  public static IRequest createRequest(IKasqMessage message)
  {
    sLogger.debug("RequestFactory::createRequest() - IN");
    
    IRequest request = null;
    try
    {
      int t = message.getIntProperty(IKasqConstants.cPropertyRequestType);
      ERequestType type = ERequestType.fromInt(t);
      
      switch (type)
      {
        case cGet:
          sLogger.debug("RequestFactory::createRequest() - Creating request: " + type.toString());
          request = new GetRequest(message);
          break;
        case cPut:
          sLogger.debug("RequestFactory::createRequest() - Creating request: " + type.toString());
          request = new PutRequest(message);
          break;
        case cAuthenticate:
          sLogger.debug("RequestFactory::createRequest() - Creating request: " + type.toString());
          request = new AuthenticateRequest(message);
          break;
        case cShutdown:
          sLogger.debug("RequestFactory::createRequest() - Creating request: " + type.toString());
          request = new ShutdownRequest(message);
          break;
        default:
          break;
      }
    }
    catch (Throwable e)
    {
      sLogger.error("Failed to create a Request object. Exception caught: ", e);
    }
    
    sLogger.debug("RequestFactory::createRequest() - OUT");
    return request;
  }
}
