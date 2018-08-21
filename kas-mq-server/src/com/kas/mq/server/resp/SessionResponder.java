package com.kas.mq.server.resp;

import com.kas.infra.base.AKasObject;
import com.kas.mq.impl.IMqConstants;
import com.kas.mq.impl.MqMessage;
import com.kas.mq.impl.MqMessageFactory;
import com.kas.mq.impl.MqQueue;
import com.kas.mq.impl.MqResponseMessage;
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
   * @return The {@link MqResponseMessage} object
   */
  public MqResponseMessage authenticate(MqMessage request)
  {
    Response response = new Response();
    
    String user = request.getStringProperty(IMqConstants.cKasPropertyUserName, null);
    String pwd  = request.getStringProperty(IMqConstants.cKasPropertyPassword, null);
    
    if ((user == null) || (user.length() == 0))
    {
      response = new Response("Invalid user name", 12, false);
    }
    else
    {
      String confPwd = mHandler.getConfig().getUserPassword(user);
      if (confPwd == null)
      {
        response = new Response("User " + user + " is not defined", 12, false);
      }
      else if (!confPwd.equals(pwd))
      {
        response = new Response("Password does not match", 8, false);
      }
      else
      {
        mHandler.setActiveUserName(user);
      }
    }
    
    return generateResponse(response);
  }
  
  /**
   * Process open queue request
   * 
   * @param request The request message
   * @return The {@link MqResponseMessage} object
   */
  public MqResponseMessage open(MqMessage request)
  {
    Response response = new Response();
    
    String queue = request.getStringProperty(IMqConstants.cKasPropertyQueueName, null);
    MqQueue mqq = mRepository.getQueue(queue);
    
    if (mqq == null)
    {
      response = new Response("Queue name is null, an empty string or does not exist", 8, true);
    }
    else
    {
      mHandler.setActiveQueue(mqq);
    }
    return generateResponse(response);
  }
  
  /**
   * Process close queue request
   * 
   * @param request The request message
   * @return The {@link MqResponseMessage} object
   */
  public MqResponseMessage close(MqMessage request)
  {
    Response response = new Response();
    
    if (mHandler.getActiveQueue() == null)
    {
      response = new Response("No opened queue", 4, true);
    }
    else
    {
      mHandler.setActiveQueue(null);
    }
    return generateResponse(response);
  }
  
  /**
   * Process define queue request
   * 
   * @param request The request message
   * @return The {@link MqResponseMessage} object
   */
  public MqResponseMessage define(MqMessage request)
  {
    Response response = new Response();
    
    String queue = request.getStringProperty(IMqConstants.cKasPropertyQueueName, null);
    if ((queue == null) || (queue.length() == 0))
    {
      response = new Response("Queue name is null or an empty string", 8, true);
    }
    else
    {
      MqQueue mqq = mRepository.getQueue(queue);
      if (mqq != null)
      {
        response = new Response("Queue with name \"" + queue + "\" already exists", 8, true);
      }
      else
      {
        mqq = mRepository.createQueue(queue);
      }
    }
    return generateResponse(response);
  }
  
  /**
   * Process delete queue request
   * 
   * @param request The request message
   * @return The {@link MqResponseMessage} object
   */
  public MqResponseMessage delete(MqMessage request)
  {
    Response response = new Response();
    
    String queue = request.getStringProperty(IMqConstants.cKasPropertyQueueName, null);
    if ((queue == null) || (queue.length() == 0))
    {
      response = new Response("Queue name is null or an empty string", 8, true);
    }
    else
    {
      MqQueue mqq = mRepository.getQueue(queue);
      if (mqq == null)
      {
        response = new Response("Queue with name \"" + queue + "\" does not exist", 8, true);
      }
      else
      {
        mqq = mRepository.removeQueue(queue);
        MqQueue activeq = mHandler.getActiveQueue();
        if ((activeq != null) && (activeq.getName().equals(queue)))
          mHandler.setActiveQueue(null);
      }
    }
    return generateResponse(response);
  }
  
  /**
   * Process show info request
   * 
   * @param request The request message
   * @return The {@link MqResponseMessage} object
   */
  public MqResponseMessage show(MqMessage request)
  {
    Response response = new Response();
    
    MqResponseMessage responseMessage = generateResponse(response);
    
    responseMessage.setStringProperty(IMqConstants.cKasPropertySessionId, mHandler.getSessionId().toString());
    responseMessage.setStringProperty(IMqConstants.cKasPropertyNetworkAddress, mHandler.getNetworkAddress().toString());
    responseMessage.setStringProperty(IMqConstants.cKasPropertyUserName, mHandler.getActiveUserName());
    if (mHandler.getActiveQueue() != null)
      responseMessage.setStringProperty(IMqConstants.cKasPropertyQueueName, mHandler.getActiveQueue().getName());
    
    return responseMessage;
  }
  
  /**
   * Generate a {@link MqResponseMessage} based on a {@link Response} object
   * 
   * @param resp The {@link Response} object
   * @return the {@link MqResponseMessage}
   */
  private MqResponseMessage generateResponse(Response resp)
  {
    return MqMessageFactory.createResponse(resp.getCode(), resp.getMessage());
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
