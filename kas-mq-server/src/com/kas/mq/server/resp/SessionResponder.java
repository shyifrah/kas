package com.kas.mq.server.resp;

import com.kas.infra.base.AKasObject;
import com.kas.mq.impl.MqMessage;
import com.kas.mq.impl.MqMessageFactory;
import com.kas.mq.impl.MqQueue;
import com.kas.mq.impl.MqResponseMessage;
import com.kas.mq.server.IHandler;

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
   * Construct a {@link SessionResponder}, specifying the handler
   * 
   * @param handler The handler that created this object
   */
  SessionResponder(IHandler handler)
  {
    mHandler = handler;
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
    
    String user = request.getUserName();
    String pwd  = request.getPassword();
    
    if (!mHandler.isPasswordMatch(user, pwd))
    {
      response = new Response("Password does not match", 8, false);
    }
    else
    {
      mHandler.setActiveUserName(user);
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
    
    String queue = request.getQueueName();
    MqQueue mqq = mHandler.getQueue(queue);
    
    if (mqq == null)
    {
      response = new Response("Queue does not exist", 8, true);
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
