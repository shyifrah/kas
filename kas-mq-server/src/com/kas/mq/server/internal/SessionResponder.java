package com.kas.mq.server.internal;

import java.util.Collection;
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
  public IMqMessage<?> defineQueue(IMqMessage<?> request)
  {
    MqResponse response = null;
    
    String queue = request.getStringProperty(IMqConstants.cKasPropertyDefQueueName, null);
    int threshold = request.getIntProperty(IMqConstants.cKasPropertyDefThreshold, IMqConstants.cDefaultQueueThreshold);
    MqQueue mqq = mRepository.getQueue(queue);
    
    if ((queue == null) || (queue.length() == 0))
    {
      response = new MqResponse(EMqResponseCode.cError, "Invalid queue name");
    }
    else if (mqq != null)
    {
      response = new MqResponse(EMqResponseCode.cWarn, "Queue with name \"" + queue + "\" already exists");
    }
    else
    {
      mqq = mRepository.createQueue(queue, threshold);
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
  public IMqMessage<?> deleteQueue(IMqMessage<?> request)
  {
    MqResponse response = null;
    
    String queue = request.getStringProperty(IMqConstants.cKasPropertyDelQueueName, null);
    boolean force = request.getBoolProperty(IMqConstants.cKasPropertyDelForce, false);
    MqQueue mqq = mRepository.getQueue(queue);
    
    if ((queue == null) || (queue.length() == 0))
    {
      response = new MqResponse(EMqResponseCode.cError, "Invalid queue name");
    }
    else if (mqq == null)
    {
      response = new MqResponse(EMqResponseCode.cWarn, "Queue with name \"" + queue + "\" doesn't exist");
    }
    else
    {
      int size = mqq.size();
      if (size == 0)
      {
        mRepository.removeQueue(queue);
        response = new MqResponse(EMqResponseCode.cOkay, "");
      }
      else if (force)
      {
        mRepository.removeQueue(queue);
        response = new MqResponse(EMqResponseCode.cOkay, "");
      }
      else
      {
        response = new MqResponse(EMqResponseCode.cFail, "Queue is not empty (" + size + " messages) and FORCE was not specified");
      }
    }
    return generateResponse(response);
  }
  
  /**
   * Process delete queue request
   * 
   * @param request The request message
   * @return The {@link AMqMessage} response object
   */
  public IMqMessage<?> queryQueue(IMqMessage<?> request)
  {
    MqResponse response = null;
    
    String prefix = request.getStringProperty(IMqConstants.cKasPropertyQryQueueName, null);
    boolean alldata = request.getBoolProperty(IMqConstants.cKasPropertyQryAllData, false);
    if ((prefix == null) || (prefix.length() == 0))
    {
      response = new MqResponse(EMqResponseCode.cError, "Invalid queue name prefix");
    }
    else 
    {
      Collection<MqQueue> queues = mRepository.getElements();
      StringBuilder sb = new StringBuilder();
      sb.append("Query ").append((alldata ? "all" : "basic")).append(" data on ").append(prefix).append(":\n").append("  \n");
      int total = 0;
      for (MqQueue mqq : queues)
      {
        if (mqq.getName().startsWith(prefix))
        {
          ++total;
          sb.append("Queue....................: ").append(mqq.getName()).append('\n');
          sb.append("    ID...............: ").append(mqq.getId().toString()).append('\n');
          if (alldata)
          {
            sb.append("    Threshold........: ").append(mqq.getThreshold()).append('\n');
            sb.append("    Size.............: ").append(mqq.size()).append('\n');
          }
          sb.append(" ").append('\n');
        }
      }
      
      EMqResponseCode rc;
      if (total == 0)
      {
        sb.append(" ").append('\n');
        sb.append("No queues matched specified prefix");
        rc = EMqResponseCode.cWarn;
      }
      else
      {
        sb.append(" ").append('\n');
        sb.append(total).append(" queues matched specified prefix");
        rc = EMqResponseCode.cOkay;
      }
      response = new MqResponse(rc, sb.toString());
    }
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
      deadq.put(request);
      rc = EMqResponseCode.cError;
      rsn = "Target queue " + (qname == null ? "not specified" : "does not exist");
    }
    else if (!queue.put(request))
    {
      deadq.put(request);
      rc = EMqResponseCode.cFail;
      rsn = "Failed to put message with ID " + request.getMessageId().toString() + " into queue " + qname;
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
