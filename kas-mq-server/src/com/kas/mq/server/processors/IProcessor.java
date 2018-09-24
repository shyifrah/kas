package com.kas.mq.server.processors;

import com.kas.infra.base.IObject;
import com.kas.infra.base.Properties;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.MqTextMessage;

/**
 * A {@link IProcessor processor} is an object that process request.
 * To create a processor, we call {@link ProcessorFactory#newProcessor(MqTextMessage)}
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
   * @return {@code false} if the processor should cease its operation following
   * this request, {@code true} otherwise
   */
  public abstract boolean postprocess(IMqMessage<?> reply);
  
  /**
   * Generate a response message which will be sent back to remote client.
   * 
   * @return the {@link IMqMessage} response object
   */
  public abstract MqTextMessage respond();
    
  /**
   * Generate a response message which will be sent back to remote client.
   * 
   * @param body The message body to be placed on the response message
   * @param props The properties to place in the message's properties
   * @return the {@link MqTextMessage} response object
   * 
   * @see com.kas.mq.server.processors.IProcessor#respond(String, Properties)
   */
  public abstract MqTextMessage respond(String body, Properties props);
}
