package com.kas.mq.server.processors;

import com.kas.mq.impl.EQueryType;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.internal.SessionHandler;

/**
 * Processor for querying server's information
 * 
 * @author Pippo
 */
public class QueryServerProcessor extends AProcessor
{
  private IProcessor mSubProcessor = null;
  
  /**
   * Extracted input from the request: 
   * the type of information requested
   */
  private EQueryType mQueryType;
  
  /**
   * Construct a {@link QueryServerProcessor}
   * 
   * @param request The request message
   * @param handler The session handler
   * @param repository The server's repository
   */
  QueryServerProcessor(IMqMessage request, SessionHandler handler, IRepository repository)
  {
    super(request, handler, repository);
  }
  
  /**
   * Process query server request
   * 
   * @return {@code null} if there's no reply, a {@link IMqMessage} if there is one
   */
  public IMqMessage process()
  {
    mLogger.debug("QueryServerProcessor::process() - IN");
    
    IMqMessage response = null;
    
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.debug("QueryServerProcessor::process() - " + mDesc);
    }
    else
    {
      int temp = mRequest.getIntProperty(IMqConstants.cKasPropertyQueryType, EQueryType.UNKNOWN.ordinal());
      mQueryType = EQueryType.fromInt(temp);
      mLogger.debug("QueryServerProcessor::process() - QueryType=" + mQueryType.toString());
      
      switch (mQueryType)
      {
        case QUERY_CONFIG:
          mSubProcessor = new QueryConfigSubProcessor(mRequest, mHandler, mRepository);
          break;
        case QUERY_SESSION:
          mSubProcessor = new QuerySessionSubProcessor(mRequest, mHandler, mRepository);
          break;
        case QUERY_CONNECTION:
          mSubProcessor = new QueryConnectionSubProcessor(mRequest, mHandler, mRepository);
          break;
        case QUERY_QUEUE:
          mSubProcessor = new QueryQueueSubProcessor(mRequest, mHandler, mRepository);
          break;
        case QUERY_USER:
          mSubProcessor = new QueryUserSubProcessor(mRequest, mHandler, mRepository);
          break;
        case QUERY_GROUP:
          mSubProcessor = new QueryGroupSubProcessor(mRequest, mHandler, mRepository);
          break;
        default:
      }
      
      if (mSubProcessor == null)
      {
        mDesc = "Unknown query type request: " + mQueryType.toString();
        mLogger.debug("QueryServerProcessor::process() - " + mDesc);
      }
      else
      {
        response = mSubProcessor.process();
      }
    }
    
    mLogger.debug("QueryServerProcessor::process() - OUT");
    return response == null ? respond() : response;
  }
  
  /**
   * Post-process queue query request.<br>
   * <br>
   * call the sub-processor's postprocess() method
   * 
   * @param reply The reply message the processor's {@link #process()} method generated
   * @return always {@code true}
   */
  public boolean postprocess(IMqMessage reply)
  {
    mLogger.debug("QueryServerProcessor::postprocess() - IN");
    
    if (mSubProcessor != null)
      mSubProcessor.postprocess(reply);
    
    mLogger.debug("QueryServerProcessor::postprocess() - OUT");
    return true;
  }
}
