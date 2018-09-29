package com.kas.mq.server.processors;

import com.kas.infra.base.AKasObject;
import com.kas.infra.base.Properties;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.MqConfiguration;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.impl.messages.MqMessageFactory;
import com.kas.mq.impl.messages.MqStringMessage;
import com.kas.mq.internal.EMqCode;
import com.kas.mq.internal.MqResponse;
import com.kas.mq.server.IController;
import com.kas.mq.server.IRepository;

public abstract class AProcessor extends AKasObject implements IProcessor
{
  /**
   * Logger
   */
  protected ILogger mLogger;
  
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
   * These values are used to generate the response if either 
   * {@link #respond()} or {@link #respond(String, Properties)} are called
   */
  protected EMqCode mCode = EMqCode.cFail;
  protected int mValue = -1;
  protected String mDesc = "";
  
  /**
   * Construct abstract {@link IProcessor}
   * 
   * @param request The request message to be processed
   * @param controller The session controller
   * @param repository The server's repository
   */
  AProcessor(IMqMessage request, IController controller, IRepository repository)
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mRequest = request;
    mController = controller;
    mConfig = controller.getConfig();
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
