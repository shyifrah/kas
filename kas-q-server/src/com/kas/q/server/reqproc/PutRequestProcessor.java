package com.kas.q.server.reqproc;

import java.io.IOException;
import javax.jms.Destination;
import javax.jms.JMSException;
import com.kas.infra.base.AKasObject;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.ext.IKasqDestination;
import com.kas.q.ext.IKasqMessage;
import com.kas.q.requests.ERequestType;
import com.kas.q.server.IClientHandler;
import com.kas.q.server.KasqRepository;
import com.kas.q.server.KasqServer;

final public class PutRequestProcessor extends AKasObject implements IRequestProcessor
{
  /***************************************************************************************************************
   * 
   */
  private static ILogger sLogger = LoggerFactory.getLogger(PutRequestProcessor.class);
  private static KasqRepository sRepository = KasqServer.getInstance().getRepository();
  
  /***************************************************************************************************************
   * 
   */
  private IKasqMessage mMessage = null;
  private IKasqDestination mDestination = null;
  
  /***************************************************************************************************************
   * Construct a {@code PutRequestProcessor} out of a {@link IKasqMessage}.
   * The construction includes extraction of several message properties and then verify their validity.
   * 
   * @param requestMessage the {@code IKasqMessage} that the ClientHandler received from the MessageConsumer.
   * 
   * @throws IllegalArgumentException if one of the extracted properties is invalid
   */
  PutRequestProcessor(IKasqMessage requestMessage) throws IllegalArgumentException
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
  public boolean process(IClientHandler handler) throws JMSException, IOException
  {
    sLogger.debug("PutRequestProcessor::process() - IN");
    
    sLogger.debug("PutRequestProcessor::process() - Processing request: " + mMessage.toPrintableString(0));
    
    boolean result = false;
    if (!handler.isAuthenticated())
    {
      sLogger.debug("PutRequestProcessor::process() - ClientHandler was not authenticated, cannot continue");
    }
    else
    {
      sLogger.debug("PutRequestProcessor::process() - Message destination is managed by KAS/Q. Name=[" + mDestination.getFormattedName() + "]");
      
      String destinationName = mDestination.getName();
      IKasqDestination destinationFromRepo = sRepository.locate(destinationName);
      if (destinationFromRepo != null)
      {
        destinationFromRepo.put(mMessage);
      }
      else
      {
        sLogger.debug("PutRequestProcessor::process() - Destination is not defined, sending to DEADQ...");
        IKasqDestination deadq = sRepository.getDeadQueue();
        deadq.put(mMessage);
      }
      result = true;
    }
    
    sLogger.debug("PutRequestProcessor::process() - OUT, Result=" + result);
    return result;
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
      .append(pad).append("  InDest(=").append(mDestination.getFormattedName()).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}