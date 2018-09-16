package com.kas.mq.server.processors;

import com.kas.infra.base.IObject;
import com.kas.mq.impl.IMqMessage;

/**
 * A {@link IProcessor processor} is an object that process request.
 * To create a processor, we call {@link ProcessorFactory#newProcessor(IMqMessage)}
 *  
 * @author Pippo
 * 
 * @see com.kas.mq.server.processors.ProcessorFactory
 * @see com.kas.mq.server.processors.AProcessor
 */
public interface IProcessor extends IObject
{
  /**
   * Process request
   * 
   * @return {@code null} if there's no reply, a {@link IMqMessage} if there is one
   */
  public abstract IMqMessage<?> process();
  
  /**
   * Post-process request
   * 
   * @param reply The reply the processor generated
   * @return {@code null} if there's no reply, a {@link IMqMessage} if there is one
   */
  public abstract boolean postprocess(IMqMessage<?> reply);
  
  /**
   * Generate a response message which will be sent back to remote client.
   * 
   * @return the {@link IMqMessage} response object
   */
 public abstract IMqMessage<?> respond();
}
