package com.kas.mq.server.processors;

import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.internal.ERequestType;
import com.kas.mq.server.IController;
import com.kas.mq.server.internal.SessionHandler;

/**
 * A class that generates {@link IProcessor processor} objects to handle requests.
 * 
 * @author Pippo
 * 
 * @see com.kas.mq.server.processors.IProcessor
 */
public class ProcessorFactory
{
  static private ILogger sLogger = LoggerFactory.getLogger(ProcessorFactory.class);
  
  static public IProcessor newProcessor(IController controller, SessionHandler handler, IMqMessage<?> request)
  {
    sLogger.debug("ProcessorFactory::newProcessor() - IN");
    
    ERequestType type = request.getRequestType();
    sLogger.debug("ProcessorFactory::newProcessor() - RequestType is: " + type);
    
    IProcessor processor = null;
    switch (type)
    {
      case cLogin:
        processor = new LoginProcessor(request, controller, handler);
        break;
      case cDefineQueue:
        processor = new DefineQueueProcessor(request, controller);
        break;
      case cDeleteQueue:
        processor = new DeleteQueueProcessor(request, controller);
        break;
      case cQueryQueue:
        processor = new QueryQueueProcessor(request, controller);
        break;
      case cRepoUpdate:
        processor = new RepoUpdateProcessor(request, controller);
        break;
      case cSysState:
        processor = new SysStateProcessor(request, controller);
        break;
      case cShutdown:
        processor = new ShutdownProcessor(request, controller, handler);
        break;
      case cGet:
        processor = new GetMessageProcessor(request, controller, handler);
        break;
//      case cUnknown:
//        processor = new PutMessageProcessor();
      default:
        break;
    }
    
    sLogger.debug("ProcessorFactory::newProcessor() - OUT, Returns=" + StringUtils.asPrintableString(processor));
    return processor;
  }
}
