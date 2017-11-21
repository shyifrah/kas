package com.kas.q.server.req;

import javax.jms.Destination;
import com.kas.infra.base.AKasObject;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.ext.IKasqDestination;
import com.kas.q.ext.IKasqMessage;

final public class PutRequest extends AKasObject implements IRequest
{
  /***************************************************************************************************************
   * 
   */
  private static ILogger sLogger = LoggerFactory.getLogger(PutRequest.class);
  
  /***************************************************************************************************************
   * 
   */
  private IKasqMessage mMessage = null;
  private IKasqDestination mDestination = null;
  
  /***************************************************************************************************************
   * Construct a {@code PutRequest} out of a {@link IKasqMessage}.
   * The construction includes extraction of several message properties and then verify their validity.
   * 
   * @param requestMessage the {@code IKasqMessage} that the ClientHandler received from the MessageConsumer.
   * 
   * @throws IllegalArgumentException if one of the extracted properties is invalid
   */
  PutRequest(IKasqMessage requestMessage) throws IllegalArgumentException
  {
    mMessage  = requestMessage;
    Destination jmsDest = null;
    try
    {
      jmsDest = requestMessage.getJMSDestination();
    }
    catch (Throwable e) {}
    
    if (jmsDest == null)
    {
      sLogger.warn("Received PutRequest with invalid destination: dest=[" + StringUtils.asString(jmsDest) + "]");
      throw new IllegalArgumentException("Invalid PutRequest: null destination name");
    }
    
    if (!(jmsDest instanceof IKasqDestination))
    {
      sLogger.warn("Received PutRequest with invalid destination: Not managed by KAS/Q");
      throw new IllegalArgumentException("Invalid PutRequest: destination not managed by KAS/Q");
    }
    
    mDestination = (IKasqDestination)jmsDest;
  }
  
  /***************************************************************************************************************
   * Get the message
   * 
   * @return the message to be put
   */
  public IKasqMessage getMessage()
  {
    return mMessage;
  }
  
  /***************************************************************************************************************
   * Get the message destination
   * 
   * @return the message destination
   */
  public IKasqDestination getDestination()
  {
    return mDestination;
  }
  
  /***************************************************************************************************************
   *  
   */
  public ERequestType getRequestType()
  {
    return ERequestType.cPut;
  }
  
  /***************************************************************************************************************
   *  
   */
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append(name())
      .append("(ToDest=").append(mDestination.getName()).append(")");
    return sb.toString();
  }
  
  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    String msgId = "unknown";
    try
    {
      msgId = mMessage.getJMSMessageID();
    }
    catch (Throwable e) {}
    
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    sb.append(name()).append("(\n")
      .append(pad).append("  MessageId=").append(msgId).append("\n")
      .append(pad).append("  Destination=").append(mDestination.getName()).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
