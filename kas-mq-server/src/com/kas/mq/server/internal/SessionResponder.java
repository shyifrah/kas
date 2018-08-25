package com.kas.mq.server.internal;

import com.kas.infra.base.AKasObject;
import com.kas.mq.impl.EMqResponseCode;
import com.kas.mq.impl.IMqConstants;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.MqMessage;
import com.kas.mq.impl.MqMessageFactory;
import com.kas.mq.impl.MqQueue;
import com.kas.mq.impl.MqResponse;
import com.kas.mq.server.IHandler;
import com.kas.mq.server.IRepository;

/**
 * A {@link SessionResponder session responder} object is the object that responsible for
 * performing the actual operations the client requests from its associated handler.
 *  
 * @author Pippo
 */
public class SessionResponder extends AKasObject
{
  /**
   * The handler
   */
  private IHandler mHandler;
  
  /**
   * Server's queue repository
   */
  private IRepository mRepository;
  
  /**
   * Construct a {@link SessionResponder}, specifying the handler
   * 
   * @param handler The handler that created this object
   */
  public SessionResponder(IHandler handler)
  {
    mHandler = handler;
    mRepository = handler.getRepository();
  }
  
  /**
   * Process authenticate request
   * 
   * @param request The request message
   * @return The {@link MqMessage} response object
   */
  public IMqMessage<?> authenticate(IMqMessage<?> request)
  {
    MqResponse response = null;
    
    String user = request.getStringProperty(IMqConstants.cKasPropertyUserName, null);
    String pwd  = request.getStringProperty(IMqConstants.cKasPropertyPassword, null);
    String confPwd = mHandler.getConfig().getUserPassword(user);
    
    if ((user == null) || (user.length() == 0))
      response = new MqResponse(EMqResponseCode.cError, "Invalid user name");
    else if (confPwd == null)
      response = new MqResponse(EMqResponseCode.cFail, "User " + user + " is not defined");
    else if (!confPwd.equals(pwd))
      response = new MqResponse(EMqResponseCode.cFail, "Password does not match");
    else
    {
      mHandler.setActiveUserName(user);
      response = new MqResponse(EMqResponseCode.cOkay, "");
    }
    
    return generateResponse(response);
  }
  
  /**
   * Process open queue request
   * 
   * @param request The request message
   * @return The {@link MqMessage} response object
   */
  public IMqMessage<?> open(IMqMessage<?> request)
  {
    MqResponse response = null;
    
    String queue = request.getStringProperty(IMqConstants.cKasPropertyQueueName, null);
    MqQueue mqq = mRepository.getQueue(queue);
    
    if ((queue == null) || (queue.length() == 0))
    {
      response = new MqResponse(EMqResponseCode.cError, "Invalid queue name");
    }
    else if (mqq == null)
    {
      response = new MqResponse(EMqResponseCode.cFail, "Queue does not exist");
    }
    else
    {
      mHandler.setActiveQueue(mqq);
      response = new MqResponse(EMqResponseCode.cOkay, "");
    }
    
    return generateResponse(response);
  }
  
  /**
   * Process close queue request
   * 
   * @param request The request message
   * @return The {@link MqMessage} response object
   */
  public IMqMessage<?> close(IMqMessage<?> request)
  {
    MqResponse response = null;
    
    if (mHandler.getActiveQueue() == null)
    {
      response = new MqResponse(EMqResponseCode.cWarn, "No open queue");
    }
    else
    {
      mHandler.setActiveQueue(null);
      response = new MqResponse(EMqResponseCode.cOkay, "");
    }
    
    return generateResponse(response);
  }
  
  /**
   * Process define queue request
   * 
   * @param request The request message
   * @return The {@link MqMessage} response object
   */
  public IMqMessage<?> define(IMqMessage<?> request)
  {
    MqResponse response = null;
    
    String queue = request.getStringProperty(IMqConstants.cKasPropertyQueueName, null);
    MqQueue mqq = mRepository.getQueue(queue);
    
    if ((queue == null) || (queue.length() == 0))
    {
      response = new MqResponse(EMqResponseCode.cError, "Invalid queue name");
    }
    else if (mqq != null)
    {
      response = new MqResponse(EMqResponseCode.cFail, "Queue with name \"" + queue + "\" already exists");
    }
    else
    {
      mqq = mRepository.createQueue(queue);
      response = new MqResponse(EMqResponseCode.cOkay, "");
    }
    
    return generateResponse(response);
  }
  
  /**
   * Process delete queue request
   * 
   * @param request The request message
   * @return The {@link MqMessage} response object
   */
  public IMqMessage<?> delete(IMqMessage<?> request)
  {
    MqResponse response = null;
    
    String queue = request.getStringProperty(IMqConstants.cKasPropertyQueueName, null);
    MqQueue mqq = mRepository.getQueue(queue);
    
    if ((queue == null) || (queue.length() == 0))
    {
      response = new MqResponse(EMqResponseCode.cError, "Invalid queue name");
    }
    else if (mqq == null)
    {
      response = new MqResponse(EMqResponseCode.cWarn, "Queue with name \"" + queue + "\" does not exist");
    }
    else
    {
      mqq = mRepository.removeQueue(queue);
      
      MqQueue activeq = mHandler.getActiveQueue();
      if ((activeq != null) && (activeq.getName().equals(queue)))
        mHandler.setActiveQueue(null);
      
      response = new MqResponse(EMqResponseCode.cOkay, "");
    }
    
    return generateResponse(response);
  }
  
  /**
   * Process put message to opened queue request
   * 
   * @param message The put request message (the actual message to put)
   * @return The {@link MqMessage} response object
   */
  public IMqMessage<?> put(IMqMessage<?> request)
  {
    MqResponse response = null;
    
    MqQueue activeq = mHandler.getActiveQueue();
    if (activeq == null)
      response = new MqResponse(EMqResponseCode.cFail, "No open queue");
    else
    {
      boolean success = activeq.put(request);
      if (!success)
        response = new MqResponse(EMqResponseCode.cFail, "Failed to put message in queue");
      else
        response = new MqResponse(EMqResponseCode.cOkay, "");
    }
    
    return generateResponse(response);
  }
  
  /**
   * Process get message from opened queue request
   * 
   * @param request The request message
   * @return The {@link MqMessage} response object
   */
  public IMqMessage<?> get(IMqMessage<?> request)
  {
    MqResponse response = null;
    IMqMessage<?>  result = null;
    
    int priority  = request.getIntProperty(IMqConstants.cKasPropertyGetPriority, IMqConstants.cDefaultPriority);
    long timeout  = request.getLongProperty(IMqConstants.cKasPropertyGetTimeout, IMqConstants.cDefaultTimeout);
    long interval = request.getLongProperty(IMqConstants.cKasPropertyGetInterval, IMqConstants.cDefaultPollingInterval);
    
    MqQueue activeq = mHandler.getActiveQueue();
    if (activeq == null)
    {
      response = new MqResponse(EMqResponseCode.cFail, "No open queue");
      result = generateResponse(response);
    }
    else
    {
      result = activeq.get(priority, timeout, interval);
      if (result == null)
      {
        response = new MqResponse(EMqResponseCode.cWarn, "No message was found");
        result = generateResponse(response);
      }
      else
      {
        response = new MqResponse(EMqResponseCode.cOkay, "");
        result = mergeResponse(response, result);
      }
    }
    
    return result;
  }
  
  /**
   * Merge {@code resp} into {@code msg}
   * 
   * @param resp The {@link MqResponse} object
   * @param msg The {@link MqMessage} object
   * @return the {@link MqMessage} with the response code and description from the {@link MqResponse} object
   */
  private IMqMessage<?> mergeResponse(MqResponse resp, IMqMessage<?> msg)
  {
    msg.setIntProperty(IMqConstants.cKasPropertyResponseCode, resp.getCode().ordinal());
    msg.setStringProperty(IMqConstants.cKasPropertyResponseDesc, resp.getDesc());
    return msg;
  }
  
  /**
   * Generate a {@link MqMessage} based on a {@link MqResponse} object
   * 
   * @param resp The {@link MqResponse} object
   * @return the {@link MqMessage} response object
   */
  private IMqMessage<?> generateResponse(MqResponse resp)
  {
    return MqMessageFactory.createResponse(resp.getCode(), resp.getDesc());
  }
  
  /**
   * Get the object's detailed string representation
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public String toPrintableString(int level)
  {
    return name();
  }
}
