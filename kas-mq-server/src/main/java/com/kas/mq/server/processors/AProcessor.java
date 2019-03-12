package com.kas.mq.server.processors;

import com.kas.infra.base.AKasObject;
import com.kas.infra.base.Properties;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.impl.messages.MqMessageFactory;
import com.kas.mq.impl.messages.MqStringMessage;
import com.kas.mq.internal.EMqCode;
import com.kas.mq.internal.MqResponse;
import com.kas.mq.server.IController;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.MqConfiguration;
import com.kas.mq.server.internal.SessionHandler;
import com.kas.sec.access.AccessLevel;
import com.kas.sec.entities.UserEntity;
import com.kas.sec.resources.EResourceClass;

/**
 * Abstract processor.<br>
 * Base class for processing client requests
 * 
 * @author Pippo
 */
public abstract class AProcessor extends AKasObject implements IProcessor
{
  /**
   * Logger
   */
  protected ILogger mLogger;
  
  /**
   * Session handler under which the processor executes.
   */
  protected SessionHandler mHandler;
  
  /**
   * Session controller
   */
  protected IController mController;
  
  /**
   * KAS/MQ server configuration
   */
  protected MqConfiguration mConfig;
  
  /**
   * KAS/MQ server's repository
   */
  protected IRepository mRepository;
  
  /**
   * The request message
   */
  protected IMqMessage mRequest;
  
  /**
   * The processor response values.<br>
   * These values are used to generate the {@link MqResponse} object whenever 
   * one of the respond() methods are called
   */
  protected EMqCode mCode = EMqCode.cFail;
  protected int mValue = -1;
  protected String mDesc = "";
  
  /**
   * Construct abstract {@link IProcessor}
   * 
   * @param request The request message to be processed
   * @param handler The session handler
   * @param repository The server's repository
   */
  AProcessor(IMqMessage request, SessionHandler handler, IRepository repository)
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mRequest = request;
    mHandler = handler;
    mController = handler.getController();
    mConfig = mController.getConfig();
    mRepository = repository;
  }
  
  /**
   * Process request
   * 
   * @return {@code null} if there's no reply, a {@link IMqMessage} if there is one
   * 
   * @see com.kas.mq.server.processors.IProcessor#process()
   */
  public abstract IMqMessage process();
  
  /**
   * Post-process request.<br>
   * <br>
   * Most requests won't need post-processing, so this class supply a default implementation.
   * 
   * @param reply The reply message the processor's {@link #process()} method generated
   * @return {@code true} in case the handler should continue process next request,
   * {@code false} if it should terminate 
   * 
   * @see com.kas.mq.server.processors.IProcessor#postprocess(IMqMessage)
   */
  public boolean postprocess(IMqMessage reply)
  {
    return true;
  }
  
  /**
   * Get the processor's response code
   * 
   * @return
   *   the processor's response code
   */
  public EMqCode getResponseCode()
  {
    return mCode;
  }
  
  /**
   * Get the processor's response value
   * 
   * @return
   *   the processor's response value
   */
  public int getResponseValue()
  {
    return mValue;
  }
  
  /**
   * Get the processor's response description
   * 
   * @return
   *   the processor's response description
   */
  public String getResponseDesc()
  {
    return mDesc;
  }
  
  /**
   * Generate a response message which will be sent back to remote client.
   * 
   * @return the {@link MqStringMessage} response object
   * 
   * @see com.kas.mq.server.processors.IProcessor#respond()
   */
  public IMqMessage respond()
  {
    return respond(null, null);
  }
  
  /**
   * Generate a response message which will be sent back to remote client.
   * 
   * @param body The message body to be placed on the response message
   * @param props The properties to place in the message's properties
   * @return the {@link MqStringMessage} response object
   * 
   * @see com.kas.mq.server.processors.IProcessor#respond(String, Properties)
   */
  public IMqMessage respond(String body, Properties props)
  {
    MqStringMessage response = MqMessageFactory.createStringMessage(body);
    response.setReferenceId(mRequest.getMessageId());
    response.setSubset(props);
    return respond(response);
  }
  
  /**
   * Generate a response message which will be sent back to remote client.
   * 
   * @param body The message body to be placed on the response message
   * @param props The properties to place in the message's properties
   * @return the {@link MqStringMessage} response object
   * 
   * @see com.kas.mq.server.processors.IProcessor#respond(String, Properties)
   */
  public IMqMessage respond(IMqMessage response)
  {
    if (response == null)
      return respond(null, null);
    
    response.setResponse(new MqResponse(mCode, mValue, mDesc));
    return response;
  }
  
  /**
   * Check if active user has READ access to the specified resource
   * 
   * @param resClass Resource Class
   * @param resName Resource name
   * @return {@code true} if user is permitted, {@code false} otherwise
   */
  protected boolean isAccessPermitted(EResourceClass resClass, String resName)
  {
    return isAccessPermitted(resClass, resName, AccessLevel.READ_ACCESS);
  }
  
  /**
   * Check if active user has specified access to the specified resource
   * 
   * @param resClass Resource Class
   * @param resName Resource name
   * @param level Access level
   * @return {@code true} if user is permitted, {@code false} otherwise
   */
  protected boolean isAccessPermitted(EResourceClass resClass, String resName, AccessLevel level)
  {
    return isAccessPermitted(mHandler.getActiveUser(), resClass, resName, level);
  }
  
  /**
   * Check if specified user has specified access to the specified resource
   * 
   * @param user User entity
   * @param resClass Resource Class
   * @param resName Resource name
   * @param level Access level
   * @return {@code true} if user is permitted, {@code false} otherwise
   */
  protected boolean isAccessPermitted(UserEntity user, EResourceClass resClass, String resName, AccessLevel level)
  {
    mLogger.diag("AProcessor::isAccessPermitted() - IN, User=" + user.toString() + ", ResClass=" + resClass.toString() + ", ResName=" + resName + ", Level=" + level.toPrintableString());
    
    boolean result = user.isAccessPermitted(resClass, resName, level);
    if (!result)
    {
      mLogger.error("User " + user.toString() + " is not permitted to access resource");
      mLogger.trace("Additional information: Class=" + resClass.toString() + "; Resource=" + resName + "; Level=" + level.toPrintableString());
    }
    
    mLogger.diag("AProcessor::isAccessPermitted() - OUT, Result=" + result);
    return result;
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
