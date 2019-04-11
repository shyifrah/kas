package com.kas.mq.server.processors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.ERequestType;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.internal.SessionHandler;
import com.kas.mq.server.processors.alter.AlterQueueProcessor;
import com.kas.mq.server.processors.define.DefineGroupProcessor;
import com.kas.mq.server.processors.define.DefineQueueProcessor;
import com.kas.mq.server.processors.define.DefineUserProcessor;
import com.kas.mq.server.processors.delete.DeleteGroupProcessor;
import com.kas.mq.server.processors.delete.DeleteQueueProcessor;
import com.kas.mq.server.processors.delete.DeleteUserProcessor;
import com.kas.mq.server.processors.query.QueryConfigProcessor;
import com.kas.mq.server.processors.query.QueryConnectionProcessor;
import com.kas.mq.server.processors.query.QueryGroupProcessor;
import com.kas.mq.server.processors.query.QueryQueueProcessor;
import com.kas.mq.server.processors.query.QuerySessionProcessor;
import com.kas.mq.server.processors.query.QueryUserProcessor;
import com.kas.mq.server.processors.term.TermConnectionProcessor;
import com.kas.mq.server.processors.term.TermServerProcessor;
import com.kas.mq.server.processors.term.TermSessionProcessor;

/**
 * A class that generates {@link IProcessor processor} objects to handle requests.
 * 
 * @author Pippo
 * 
 * @see com.kas.mq.server.processors.IProcessor
 */
public class ProcessorFactory
{
  /**
   * Logger
   */
  static private Logger sLogger = LogManager.getLogger(ProcessorFactory.class);
  
  static public IProcessor newProcessor(IMqMessage request, SessionHandler handler, IRepository repository)
  {
    sLogger.trace("ProcessorFactory::newProcessor() - IN");
    
    IProcessor processor = null;
    
    ERequestType type = request.getRequestType();
    sLogger.trace("ProcessorFactory::newProcessor() - RequestType is: {}", type);
    
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
      case cQueryGroup:
        processor = new QueryGroupProcessor(request, handler, repository);
        break;
      case cDefineUser:
        processor = new DefineUserProcessor(request, handler, repository);
        break;
      case cDeleteUser:
        processor = new DeleteUserProcessor(request, handler, repository);
        break;
      case cQueryUser:
        processor = new QueryUserProcessor(request, handler, repository);
        break;
      case cDefineQueue:
        processor = new DefineQueueProcessor(request, handler, repository);
        break;
      case cDeleteQueue:
        processor = new DeleteQueueProcessor(request, handler, repository);
        break;
      case cQueryQueue:
        processor = new QueryQueueProcessor(request, handler, repository);
        break;
      case cAlterQueue:
        processor = new AlterQueueProcessor(request, handler, repository);
        break;
      case cQueryConfig:
        processor = new QueryConfigProcessor(request, handler, repository);
        break;
      case cQueryConnection:
        processor = new QueryConnectionProcessor(request, handler, repository);
        break;
      case cTerminateConnection:
        processor = new TermConnectionProcessor(request, handler, repository);
        break;
      case cQuerySession:
        processor = new QuerySessionProcessor(request, handler, repository);
        break;
      case cTerminateSession:
        processor = new TermSessionProcessor(request, handler, repository);
        break;
      case cNotifyRepoUpdate:
        processor = new RepoUpdateProcessor(request, handler, repository);
        break;
      case cNotifySysState:
        processor = new SysStateProcessor(request, handler, repository);
        break;
      case cTerminateServer:
        processor = new TermServerProcessor(request, handler, repository);
        break;
      case cGet:
        processor = new MessageGetProcessor(request, handler, repository);
        break;
      case cUnknown:
      default:
        processor = new MessagePutProcessor(request, handler, repository);
        break;
    }
    
    sLogger.trace("ProcessorFactory::newProcessor() - OUT, Returns={}", StringUtils.asPrintableString(processor));
    return processor;
  }
}
