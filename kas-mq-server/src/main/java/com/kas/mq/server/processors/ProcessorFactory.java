package com.kas.mq.server.processors;

import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.ERequestType;
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
  
  static public IProcessor newProcessor(IMqMessage request, SessionHandler handler, IRepository repository)
  {
    sLogger.debug("ProcessorFactory::newProcessor() - IN");
    
    ERequestType type = request.getRequestType();
    sLogger.debug("ProcessorFactory::newProcessor() - RequestType is: " + type);
    
    IProcessor processor = null;
    switch (type)
    {
      case cLogin:
        processor = new LoginProcessor(request, handler, repository);
        break;
      case cDefineGroup:
        processor = new DefineGroupProcessor(request, handler, repository);
        break;
      case cDeleteGroup:
        processor = new DeleteGroupProcessor(request, handler, repository);
        break;
      case cDefineUser:
        processor = new DefineUserProcessor(request, handler, repository);
        break;
      case cDeleteUser:
        processor = new DeleteUserProcessor(request, handler, repository);
        break;
      case cDefineQueue:
        processor = new DefineQueueProcessor(request, handler, repository);
        break;
      case cDeleteQueue:
        processor = new DeleteQueueProcessor(request, handler, repository);
        break;
      case cAlterQueue:
        processor = new AlterQueueProcessor(request, handler, repository);
        break;
      case cQueryServer:
        processor = new QueryServerProcessor(request, handler, repository);
        break;
      case cNotifyRepoUpdate:
        processor = new RepoUpdateProcessor(request, handler, repository);
        break;
      case cNotifySysState:
        processor = new SysStateProcessor(request, handler, repository);
        break;
      case cTermServer:
        processor = new TermServerProcessor(request, handler, repository);
        break;
      case cGet:
        processor = new MessageGetProcessor(request, handler, repository);
        break;
      case cTermConn:
        processor = new TermConnectionProcessor(request, handler, repository);
        break;
      case cTermSess:
        processor = new TermSessionProcessor(request, handler, repository);
        break;
      case cUnknown:
      default:
        processor = new MessagePutProcessor(request, handler, repository);
        break;
    }
    
    sLogger.debug("ProcessorFactory::newProcessor() - OUT, Returns=" + StringUtils.asPrintableString(processor));
    return processor;
  }
}
