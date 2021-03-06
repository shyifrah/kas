package com.kas.mq.server.processors;

import com.kas.infra.base.IObject;
import com.kas.infra.base.Properties;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.impl.messages.MqStringMessage;
import com.kas.mq.internal.EMqCode;

/**
 * A {@link IProcessor processor} is an object that process request.
 * To create a processor, we call {@link ProcessorFactory#newProcessor(MqStringMessage)}
 *  
 * @author Pippo
 */
public interface IProcessor extends IObject
{
  /**
   * Process request
   * 
   * @return
   *   {@code null} if there's no reply, a {@link IMqMessage} if there is one
   */
  public abstract IMqMessage process();
  
  /**
   * Post-process request
   * 
   * @param reply
   *   The reply the processor generated
   * @return
   *   {@code false} if the processor should cease its operation
   *   following this request, {@code true} otherwise
   */
  public abstract boolean postprocess(IMqMessage reply);
    
  /**
   * Get the processor's response code
   * 
   * @return
   *   the processor's response code
   */
  public abstract EMqCode getResponseCode();
  
  /**
   * Get the processor's response value
   * 
   * @return
   *   the processor's response value
   */
  public abstract int getResponseValue();
  
  /**
   * Get the processor's response description
   * 
   * @return
   *   the processor's response description
   */
  public abstract String getResponseDesc();
  
  /**
   * Generate a response message which will be sent back to remote client.
   * 
   * @return
   *   the {@link IMqMessage} response object
   */
  public abstract IMqMessage respond();
    
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
  public abstract IMqMessage respond(String body, Properties props);
  
  /**
   * Place response values on an existing message which will be sent back to remote client.
   * 
   * @param response
   *   The message that will be sent back
   * @return
   *   the {@link IMqMessage} response message
   */
  public abstract IMqMessage respond(IMqMessage response);
}
