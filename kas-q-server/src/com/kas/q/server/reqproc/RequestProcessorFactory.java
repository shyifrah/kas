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
      
      switch (type)
      {
        case cGet:
          sLogger.debug("RequestProcessorFactory::createRequestProcessor() - Creating request: " + type.toString());
          request = new GetRequestProcessor(message);
          break;
        case cAuthenticate:
          sLogger.debug("RequestProcessorFactory::createRequestProcessor() - Creating request: " + type.toString());
          request = new AuthRequestProcessor(message);
          break;
        case cHalt:
          sLogger.debug("RequestProcessorFactory::createRequestProcessor() - Creating request: " + type.toString());
          request = new HaltRequestProcessor(message);
          break;
        case cDefine:
          sLogger.debug("RequestProcessorFactory::createRequestProcessor() - Creating request: " + type.toString());
          request = new DefineRequestProcessor(message);
          break;
        case cLocate:
          sLogger.debug("RequestProcessorFactory::createRequestProcessor() - Creating request: " + type.toString());
          request = new LocateRequestProcessor(message);
          break;
        case cMetaData:
          sLogger.debug("RequestProcessorFactory::createRequestProcessor() - Creating request: " + type.toString());
          request = new MetaRequestProcessor(message);
          break;
        case cPut:
        default:
          sLogger.debug("RequestProcessorFactory::createRequestProcessor() - Creating request: " + type.toString());
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
