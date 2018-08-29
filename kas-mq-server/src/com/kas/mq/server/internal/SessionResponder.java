package com.kas.mq.server.internal;

import com.kas.infra.base.AKasObject;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.impl.EMqResponseCode;
import com.kas.mq.impl.IMqConstants;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.AMqMessage;
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
   * @return The {@link AMqMessage} response object
   */
  public IMqMessage<?> authenticate(IMqMessage<?> request)
  {
    MqResponse response = null;
    
    String user = request.getStringProperty(IMqConstants.cKasPropertyUserName, null);
    String sb64pass = request.getStringProperty(IMqConstants.cKasPropertyPassword, null);
    byte [] confPwd = mHandler.getConfig().getUserPassword(user);
    String sb64ConfPass = StringUtils.asHexString(confPwd);
    
    if ((user == null) || (user.length() == 0))
      response = new MqResponse(EMqResponseCode.cError, "Invalid user name");
    else if (confPwd == null)
      response = new MqResponse(EMqResponseCode.cFail, "User " + user + " is not defined");
    else if (!sb64pass.equals(sb64ConfPass))
      response = new MqResponse(EMqResponseCode.cFail, "Password does not match");
    else
    {
      mHandler.setActiveUserName(user);
      response = new MqResponse(EMqResponseCode.cOkay, "");
    }
    
    return generateResponse(response);
  }
  
  /**
   * Process define queue request
   * 
   * @param request The request message
   * @return The {@link AMqMessage} response object
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
   * @return The {@link AMqMessage} response object
   */
  public IMqMessage<?> delete(IMqMessage<?> request)
  {
    MqResponse response = null;
    
//    String queue = request.getStringProperty(IMqConstants.cKasPropertyQueueName, null);
//    MqQueue mqq = mRepository.getQueue(queue);
//    
//    if ((queue == null) || (queue.length() == 0))
//    {
//      response = new MqResponse(EMqResponseCode.cError, "Invalid queue name");
//    }
//    else if (mqq == null)
//    {
//      response = new MqResponse(EMqResponseCode.cWarn, "Queue with name \"" + queue + "\" does not exist");
//    }
//    else
//    {
//      mqq = mRepository.removeQueue(queue);
//      
//      MqQueue activeq = mHandler.getActiveQueue();
//      if ((activeq != null) && (activeq.getName().equals(queue)))
//        mHandler.setActiveQueue(null);
//      
//      response = new MqResponse(EMqResponseCode.cOkay, "");
//    }
    
    return generateResponse(response);
  }
  
  /**
   * Process put message to specified queue
   * 
   * @param message The put request message (the actual message to put)
   * @return The {@link AMqMessage} response object
   */
  public IMqMessage<?> put(IMqMessage<?> request)
  {
    MqResponse response = null;
    
    EMqResponseCode rc = EMqResponseCode.cOkay;
    String rsn = "";
    String qname = request.getStringProperty(IMqConstants.cKasPropertyPutQueueName, null);
    MqQueue queue = mHandler.getRepository().getQueue(qname);
    MqQueue deadq = mHandler.getRepository().getDeadQueue();
    if (queue == null)
    {
      boolean putToDeadQ = deadq.put(request);
      if (putToDeadQ)
        rc = EMqResponseCode.cWarn;
      else
        rc = EMqResponseCode.cError;
      rsn = "Queue " + qname + " does not exist, or target queue not specified";
    }
    else
    {
      boolean putToQueue = queue.put(request);
      if (!putToQueue)
      {
        boolean putToDeadQ = deadq.put(request);
        if (putToDeadQ)
          rc = EMqResponseCode.cFail;
        else
          rc = EMqResponseCode.cError;
        rsn = "Failed to put message with ID " + request.getMessageId().toString() + " into queue " + qname;
      } 
    }
    response = new MqResponse(rc, rsn);
    return generateResponse(response);
  }
  
  /**
   * Process get message from specified queue
   * 
   * @param request The request message
   * @return The {@link IMqMessage}
   */
  public IMqMessage<?> get(IMqMessage<?> request)
  {
    IMqMessage<?> result = null;
    
    long timeout  = request.getLongProperty(IMqConstants.cKasPropertyGetTimeout, IMqConstants.cDefaultTimeout);
    long interval = request.getLongProperty(IMqConstants.cKasPropertyGetInterval, IMqConstants.cDefaultPollingInterval);
    String qname  = request.getStringProperty(IMqConstants.cKasPropertyGetQueueName, null);
    MqQueue queue = mHandler.getRepository().getQueue(qname);
    
    if (qname == null)
      result = generateResponse(new MqResponse(EMqResponseCode.cFail, "Queue name not specified"));
    else if (queue == null)
      result = generateResponse(new MqResponse(EMqResponseCode.cFail, "Queue with name " + qname + " does not exist"));
    else
    {
      IMqMessage<?> msg = queue.get(timeout, interval);
      if (msg == null)
      {
        result = generateResponse(new MqResponse(EMqResponseCode.cWarn, "No message found in queue " + qname));
      }
      else
      {
        MqResponse resp = new MqResponse(EMqResponseCode.cOkay, "");
        result = mergeResponse(resp, msg);
      }
    }
    return result;
  }
  
  /**
   * Process shutdown request
   * 
   * @param request The request message
   * @return The {@link AMqMessage} response object
   */
  public IMqMessage<?> shutdown(IMqMessage<?> request)
  {
    MqResponse response = null;
    
    if (!"admin".equalsIgnoreCase(mHandler.getActiveUserName()))
    {
      response = new MqResponse(EMqResponseCode.cFail, "Cannot shutdown KAS/MQ server with non-admin user");
    }
    else
    {
      mHandler.getController().shutdown();
      response = new MqResponse(EMqResponseCode.cOkay, "");
    }
    
    return generateResponse(response);
  }
  
  /**
   * Merge {@code resp} into {@code msg}
   * 
   * @param resp The {@link MqResponse} object
   * @param msg The {@link AMqMessage} object
   * @return the {@link AMqMessage} with the response code and description from the {@link MqResponse} object
   */
  private IMqMessage<?> mergeResponse(MqResponse resp, IMqMessage<?> msg)
  {
    msg.setIntProperty(IMqConstants.cKasPropertyResponseCode, resp.getCode().ordinal());
    msg.setStringProperty(IMqConstants.cKasPropertyResponseDesc, resp.getDesc());
    return msg;
  }
  
  /**
   * Generate a {@link AMqMessage} based on a {@link MqResponse} object
   * 
   * @param resp The {@link MqResponse} object
   * @return the {@link AMqMessage} response object
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
