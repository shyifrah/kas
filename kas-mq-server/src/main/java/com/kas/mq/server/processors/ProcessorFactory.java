package com.kas.mq.server.processors;

import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.ERequestType;
import com.kas.mq.server.IController;
import com.kas.mq.server.IRepository;
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
  
  static public IProcessor newProcessor(IController controller, IRepository repository, SessionHandler handler, IMqMessage request)
  {
    sLogger.debug("ProcessorFactory::newProcessor() - IN");
    
    ERequestType type = request.getRequestType();
    sLogger.debug("ProcessorFactory::newProcessor() - RequestType is: " + type);
    
    IProcessor processor = null;
    switch (type)
    {
      case cLogin:
        processor = new LoginProcessor(request, controller, repository, handler);
        break;
      case cDefineQueue:
        processor = new DefineQueueProcessor(request, controller, repository);
        break;
      case cDeleteQueue:
        processor = new DeleteQueueProcessor(request, controller, repository);
        break;
      case cQueryServer:
        processor = new QueryServerProcessor(request, controller, repository);
        break;
      case cRepoUpdate:
        processor = new RepoUpdateProcessor(request, controller, repository);
        break;
      case cSysState:
        processor = new SysStateProcessor(request, controller, repository);
        break;
      case cShutdown:
        processor = new ShutdownProcessor(request, controller, repository);
        break;
      case cGet:
        processor = new MessageGetProcessor(request, controller, repository);
        break;
      case cTermConn:
        processor = new TermConnectionProcessor(request, controller, repository);
        break;
      case cTermSess:
        processor = new TermSessionProcessor(request, controller, repository);
        break;
      case cUnknown:
      default:
        processor = new MessagePutProcessor(request, controller, repository);
        break;
    }
    
    sLogger.debug("ProcessorFactory::newProcessor() - OUT, Returns=" + StringUtils.asPrintableString(processor));
    return processor;
  }
}
