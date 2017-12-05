package com.kas.q.server.reqproc;

import javax.jms.JMSException;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.ext.IKasqConstants;
import com.kas.q.ext.IKasqMessage;
import com.kas.q.requests.ERequestType;

public class RequestProcessorFactory
{
  private static ILogger sLogger = LoggerFactory.getLogger(RequestProcessorFactory.class);
  
  public static IRequestProcessor createRequestProcessor(IKasqMessage message)
  {
    sLogger.debug("RequestProcessorFactory::createRequestProcessor() - IN");
    
    int ord = 0;
    try
    {
      ord = message.getIntProperty(IKasqConstants.cPropertyRequestType);
    }
    catch (JMSException e) {}
    
    IRequestProcessor request = null;
    try
    {
      ERequestType type = ERequestType.fromInt(ord);
      sLogger.debug("RequestProcessorFactory::createRequestProcessor() - Creating processor for request of type: " + type.toString());
      
      switch (type)
      {
        case cGet:
          request = new GetRequestProcessor(message);
          break;
        case cAuthenticate:
          request = new AuthRequestProcessor(message);
          break;
        case cHalt:
          request = new HaltRequestProcessor(message);
          break;
        case cDefine:
          request = new DefineRequestProcessor(message);
          break;
        case cLocate:
          request = new LocateRequestProcessor(message);
          break;
        case cMetaData:
          request = new MetaRequestProcessor(message);
          break;
        case cPut:
        default:
          request = new PutRequestProcessor(message);
          break;
      }
    }
    catch (Throwable e)
    {
      sLogger.error("Failed to create a RequestProcessor object. Exception caught: ", e);
    }
    
    sLogger.debug("RequestProcessorFactory::createRequestProcessor() - OUT");
    return request;
  }
}
