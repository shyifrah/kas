//package com.kas.mq.server.processors;
//
//import com.kas.mq.impl.EQueryType;
//import com.kas.mq.impl.messages.IMqMessage;
//import com.kas.mq.internal.IMqConstants;
//import com.kas.mq.server.IRepository;
//import com.kas.mq.server.internal.SessionHandler;
//import com.kas.mq.server.processors.query.QueryConnectionProcessor;
//import com.kas.mq.server.processors.query.QueryGroupProcessor;
//import com.kas.mq.server.processors.query.QueryQueueProcessor;
//import com.kas.mq.server.processors.query.QuerySessionProcessor;
//import com.kas.mq.server.processors.query.QueryUserProcessor;
//
///**
// * Processor for querying server's information
// * 
// * @author Pippo
// */
//public class QueryServerProcessor extends AProcessor
//{
//  /**
//   * The sub-processor that will actually handle the query request
//   */
//  private IProcessor mSubProcessor = null;
//  
//  /**
//   * Input
//   */
//  private EQueryType mQueryType;
//  
//  /**
//   * Construct a {@link QueryServerProcessor}
//   * 
//   * @param request
//   *   The request message
//   * @param handler
//   *   The session handler
//   * @param repository
//   *   The server's repository
//   */
//  QueryServerProcessor(IMqMessage request, SessionHandler handler, IRepository repository)
//  {
//    super(request, handler, repository);
//  }
//  
//  /**
//   * Process request
//   * 
//   * @return
//   *   response message generated by {@link #respond()}
//   */
//  public IMqMessage process()
//  {
//    mLogger.trace("QueryServerProcessor::process() - IN");
//    
//    IMqMessage response = null;
//    
//    if (!mConfig.isEnabled())
//    {
//      mDesc = "KAS/MQ server is disabled";
//      mLogger.trace("QueryServerProcessor::process() - {}", mDesc);
//    }
//    else
//    {
//      int temp = mRequest.getIntProperty(IMqConstants.cKasPropertyQueryType, EQueryType.UNKNOWN.ordinal());
//      mQueryType = EQueryType.fromInt(temp);
//      mLogger.trace("QueryServerProcessor::process() - QueryType={}", mQueryType);
//      
//      switch (mQueryType)
//      {
//        case QUERY_CONFIG:
//          mSubProcessor = new QueryConfigProcessor(mRequest, mHandler, mRepository);
//          break;
//        case QUERY_SESSION:
//          mSubProcessor = new QuerySessionProcessor(mRequest, mHandler, mRepository);
//          break;
//        case QUERY_CONNECTION:
//          mSubProcessor = new QueryConnectionProcessor(mRequest, mHandler, mRepository);
//          break;
//        case QUERY_QUEUE:
//          mSubProcessor = new QueryQueueProcessor(mRequest, mHandler, mRepository);
//          break;
//        case QUERY_USER:
//          mSubProcessor = new QueryUserProcessor(mRequest, mHandler, mRepository);
//          break;
//        case QUERY_GROUP:
//          mSubProcessor = new QueryGroupProcessor(mRequest, mHandler, mRepository);
//          break;
//        default:
//      }
//      
//      if (mSubProcessor == null)
//      {
//        mDesc = "Unknown query type request: " + mQueryType.toString();
//        mLogger.trace("QueryServerProcessor::process() - {}", mDesc);
//      }
//      else
//      {
//        response = mSubProcessor.process();
//      }
//    }
//    
//    mLogger.trace("QueryServerProcessor::process() - OUT");
//    return response == null ? respond() : response;
//  }
//  
//  /**
//   * Post-process queue query request.<br>
//   * call the sub-processor's postprocess() method
//   * 
//   * @param reply
//   *   The reply message the processor's {@link #process()} method generated
//   * @return
//   *   always {@code true}
//   */
//  public boolean postprocess(IMqMessage reply)
//  {
//    mLogger.trace("QueryServerProcessor::postprocess() - IN");
//    
//    if (mSubProcessor != null)
//      mSubProcessor.postprocess(reply);
//    
//    mLogger.trace("QueryServerProcessor::postprocess() - OUT");
//    return true;
//  }
//}
