package com.kas.mq.server.processors;

import com.kas.infra.base.AKasObject;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.MqConfiguration;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.MqMessageFactory;
import com.kas.mq.impl.internal.EMqCode;
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
  protected IMqMessage<?> mRequest;
  
  /**
   * The processor response values
   */
  protected EMqCode mCode = EMqCode.cFail;
  protected int mValue = -1;
  protected String mDesc = "";
  
  /**
   * Construct abstract {@link IProcessor}
   * 
   * @param request The request message to be processed
   * @param controller The session conrtoller
   */
  AProcessor(IMqMessage<?> request, IController controller)
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mRequest = request;
    mController = controller;
    mConfig = controller.getConfig();
    mRepository = controller.getRepository();
  }
  
  /**
   * Process request
   * 
   * @return {@code null} if there's no reply, a {@link IMqMessage} if there is one
   * 
   * @see com.kas.mq.server.processors.IProcessor#process(IMqMessage)
   */
  public abstract IMqMessage<?> process();
  
  /**
   * Post-process request.<br>
   * <br>
   * Most requests won't need post-processing, so this class supply a default implementation.
   * 
   * @param reply The reply message the processor's {@link #process()} method generated
   * @return {@code true} in case the handler should continue process next request,
   * {@code false} if it should terminate 
   * 
   * @see com.kas.mq.server.processors.IProcessor#postprocess(IMqMessage, IMqMessage)
   */
  public boolean postprocess(IMqMessage<?> reply)
  {
    return true;
  }
  
  /**
   * Generate a response message which will be sent back to remote client.
   * 
   * @return the {@link IMqMessage} response object
   * 
   * @see com.kas.mq.server.processors.IProcessor#respond()
   */
  public IMqMessage<?> respond()
  {
    return MqMessageFactory.createResponse(mRequest, mCode, mValue, mDesc);
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
