package com.kas.mq.server.processors;

import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.EMqCode;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.internal.MqRemoteQueue;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.internal.SessionHandler;

/**
 * Processor for handling repository updates (define/delete queues)
 * 
 * @author Pippo
 */
public class RepoUpdateProcessor extends AProcessor
{
  /**
   * Extracted input from the request: 
   * the repo-update originator, the name of the queue and whether it was defined/deleted
   */
  private String mManager;
  private String mQueue;
  private boolean mAdded;
  
  /**
   * Construct a {@link RepoUpdateProcessor}
   * 
   * @param request The request message
   * @param handler The session handler
   * @param repository The server's repository
   */
  RepoUpdateProcessor(IMqMessage request, SessionHandler handler, IRepository repository)
  {
    super(request, handler, repository);
  }
  
  /**
   * Process queue deletion request.
   * 
   * @return {@code null} if there's no reply, a {@link IMqMessage} if there is one
   */
  public IMqMessage process()
  {
    mLogger.debug("RepoUpdateProcessor::process() - IN");
    
    if (!mConfig.isEnabled())
    {
      mDesc = "KAS/MQ server is disabled";
      mLogger.debug("RepoUpdateProcessor::process() - " + mDesc);
    }
    else
    {
      mManager = mRequest.getStringProperty(IMqConstants.cKasPropertyRepoQmgrName, "");
      mQueue = mRequest.getStringProperty(IMqConstants.cKasPropertyRepoQueueName, "");
      mAdded = mRequest.getBoolProperty(IMqConstants.cKasPropertyRepoOperation, false);
      mLogger.debug("RepoUpdateProcessor::process() - Manager=" + mManager + "; Queue=" + mQueue + "; Operation=" + (mAdded ? "define" : "delete"));
      
      MqRemoteQueue mqrq;
      if (mAdded)
        mqrq = mRepository.defineRemoteQueue(mManager, mQueue);
      else
        mqrq = mRepository.deleteRemoteQueue(mManager, mQueue);
      
      if (mqrq != null)
      {
        mCode = EMqCode.cOkay;
        mDesc = "Queue " + mQueue + " was successfully " + (mAdded ? "defined in" : " deleted from ") + " remote KAS/MQ manager " + mManager;
      }
      else
      {
        mCode = EMqCode.cFail;
        mDesc = "Failed to " + (mAdded ? "define" : "delete") + " queue " + mQueue + " in Remote KAS/MQ manager " + mManager;
      }
      mLogger.debug("RepoUpdateProcessor::process() - " + mDesc);
    }
    
    mLogger.debug("RepoUpdateProcessor::process() - OUT");
    return respond();
  }
}
