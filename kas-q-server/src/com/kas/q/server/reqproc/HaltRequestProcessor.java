package com.kas.q.server.reqproc;

import java.io.IOException;
import javax.jms.JMSException;
import com.kas.infra.base.AKasObject;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.ext.IKasqConstants;
import com.kas.q.ext.IKasqMessage;
import com.kas.q.requests.ERequestType;
import com.kas.q.server.IClientHandler;
import com.kas.q.server.IClientController;
import com.kas.q.server.KasqServer;

final public class HaltRequestProcessor extends AKasObject implements IRequestProcessor
{
  /***************************************************************************************************************
   * 
   */
  private static ILogger sLogger = LoggerFactory.getLogger(HaltRequestProcessor.class);
  private static IClientController sController = KasqServer.getInstance().getController();
  
  /***************************************************************************************************************
   * 
   */
  private IKasqMessage mMessage;
  private boolean mAdmin;
  
  /***************************************************************************************************************
   * Construct a {@code HaltRequestProcessor} out of a {@link IKasqMessage}.
   * The construction includes extraction of several message properties and then verify their validity.
   * 
   * @param requestMessage the {@code IKasqMessage} that the ClientHandler received from the client.
   * 
   * @throws IllegalArgumentException if one of the extracted properties is invalid
   */
  HaltRequestProcessor(IKasqMessage requestMessage) throws IllegalArgumentException
  {
    mMessage = requestMessage;
    Boolean admin = null;
    try
    {
      admin = requestMessage.getBooleanProperty(IKasqConstants.cPropertyAdminMessage);
    }
    catch (Throwable e) {}
    
    if (admin == null)
    {
      sLogger.warn("Received HaltRequest without admin property");
      throw new IllegalArgumentException("Invalid HaltRequest: null admin property");
    }
    
    mAdmin = admin;
  }
  
  /***************************************************************************************************************
   * Get admin property
   * 
   * @return true if admin property was set, false otherwise
   */
  public boolean isAdmin()
  {
    return mAdmin;
  }
  
  /***************************************************************************************************************
   *  
   */
  public boolean process(IClientHandler handler) throws JMSException, IOException
  {
    sLogger.debug("HaltRequestProcessor::process() - IN");
    
    sLogger.debug("HaltRequestProcessor::process() - Processing request: " + mMessage.toPrintableString(0));
    
    boolean result = false;
    if (!handler.isAuthenticated())
    {
      sLogger.debug("HaltRequestProcessor::process() - ClientHandler was not authenticated, cannot continue");
    }
    else
    if (!mAdmin)
    {
      sLogger.warn("Received halt request from non-authorized client. Ignoring...");
      result = true;
    }
    else
    {
      sController.onShutdownRequest();
      result = true;
    }
    
    sLogger.debug("HaltRequestProcessor::process() - OUT, Result=" + result);
    return result;
  }
  
  /***************************************************************************************************************
   *  
   */
  public ERequestType getRequestType()
  {
    return ERequestType.cHalt;
  }
  
  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    sb.append(name()).append("(\n")
      .append(pad).append("  ByAdmin=(").append(mAdmin).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
