package com.kas.mq.server.processors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.IObject;
import com.kas.infra.base.Properties;
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
  protected Logger mLogger;
  
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
   * These values are used to generate the {@link MqResponse} object
   * whenever one of the respond() methods are called
   */
  protected EMqCode mCode = EMqCode.cFail;
  protected int mValue = -1;
  protected String mDesc = "";
  
  /**
   * Construct abstract {@link IProcessor}
   * 
   * @param request
   *   The request message to be processed
   * @param handler
   *   The session handler
   * @param repository
   *   The server's repository
   */
  protected AProcessor(IMqMessage request, SessionHandler handler, IRepository repository)
  {
    mLogger = LogManager.getLogger(getClass());
    mRequest = request;
    mHandler = handler;
    mController = handler.getController();
    mConfig = mController.getConfig();
    mRepository = repository;
  }
  
  /**
   * Process request
   * 
   * @return
   *   {@code null} if there's no reply, a {@link IMqMessage} if there is one
   */
  public abstract IMqMessage process();
  
  /**
   * Post-process request.<br>
   * Most requests won't need post-processing, so this class supply a default implementation.
   * 
   * @param reply
   *   The reply message the processor's {@link #process()} method generated
   * @return
   *   {@code true} in case the handler should continue process next request,
   *   {@code false} if it should terminate
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
   * @return
   *   the {@link MqStringMessage} response object
   */
  public IMqMessage respond()
  {
    return respond(null, null);
  }
  
  /**
   * Generate a response message which will be sent back to remote client.
   * 
   * @param body
   *   The message body to be placed on the response message
   * @param props
   *   The properties to place in the message's properties
   * @return
   *   the {@link MqStringMessage} response object
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
   * @param body
   *   The message body to be placed on the response message
   * @param props
   *   The properties to place in the message's properties
   * @return
   *   the {@link MqStringMessage} response object
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
   * @param resClass
   *   Resource Class
   * @param resName
   *   Resource name
   * @return
   *   {@code true} if user is permitted, {@code false} otherwise
   */
  protected boolean isAccessPermitted(EResourceClass resClass, String resName)
  {
    return isAccessPermitted(resClass, resName, AccessLevel.READ_ACCESS);
  }
  
  /**
   * Check if active user has specified access to the specified resource
   * 
   * @param resClass
   *   Resource Class
   * @param resName
   *   Resource name
   * @param level
   *   Access level
   * @return
   *   {@code true} if user is permitted, {@code false} otherwise
   */
  protected boolean isAccessPermitted(EResourceClass resClass, String resName, AccessLevel level)
  {
    return isAccessPermitted(mHandler.getActiveUser(), resClass, resName, level);
  }
  
  /**
   * Check if specified user has specified access to the specified resource
   * 
   * @param user
   *   User entity
   * @param resClass
   *   Resource Class
   * @param resName
   *   Resource name
   * @param level
   *   Access level
   * @return
   *   {@code true} if user is permitted, {@code false} otherwise
   */
  protected boolean isAccessPermitted(UserEntity user, EResourceClass resClass, String resName, AccessLevel level)
  {
    mLogger.trace("AProcessor::isAccessPermitted() - IN, User={}, ResClass={}, ResName={}, Level={}", user, resClass, resName, level.toPrintableString());
    
    boolean result = user.isAccessPermitted(resClass, resName, level);
    if (!result)
    {
      mLogger.error("User {} is not permitted to access resource", user);
      mLogger.trace("Additional information: Class={}; Resource={}; Level={}", resClass, resName, level.toPrintableString());
    }
    
    mLogger.trace("AProcessor::isAccessPermitted() - OUT, Result={}", result);
    return result;
  }
  
  /**
   * Returns the {@link IObject} string representation.
   * 
   * @param level
   *   The required padding level
   * @return
   *   the string representation with the specified level of padding
   */
  public String toPrintableString(int level)
  {
    return name();
  }
}
