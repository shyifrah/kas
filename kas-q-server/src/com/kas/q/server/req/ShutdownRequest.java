package com.kas.q.server.req;

import java.io.IOException;
import javax.jms.JMSException;
import com.kas.infra.base.AKasObject;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.ext.IKasqConstants;
import com.kas.q.ext.IKasqMessage;
import com.kas.q.server.IClientHandler;
import com.kas.q.server.IController;
import com.kas.q.server.KasqServer;

final public class ShutdownRequest extends AKasObject implements IRequestProcessor
{
  /***************************************************************************************************************
   * 
   */
  private static ILogger sLogger = LoggerFactory.getLogger(ShutdownRequest.class);
  private static IController sController = KasqServer.getInstance().getController();
  
  /***************************************************************************************************************
   * 
   */
  private boolean mAdmin;
  
  /***************************************************************************************************************
   * Construct a {@code ShutdownRequest} out of a {@link IKasqMessage}.
   * The construction includes extraction of several message properties and then verify their validity.
   * 
   * @param requestMessage the {@code IKasqMessage} that the ClientHandler received from the client.
   * 
   * @throws IllegalArgumentException if one of the extracted properties is invalid
   */
  ShutdownRequest(IKasqMessage requestMessage) throws IllegalArgumentException
  {
    Boolean admin = null;
    try
    {
      admin = requestMessage.getBooleanProperty(IKasqConstants.cPropertyAdminMessage);
    }
    catch (Throwable e) {}
    
    if (admin == null)
    {
      sLogger.warn("Received ShutdownRequest without admin property");
      throw new IllegalArgumentException("Invalid ShutdownRequest: null admin property");
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
    sLogger.debug("ShutdownRequest::process() - IN");
    
    boolean result = false;
    if (!handler.isAuthenticated())
    {
      sLogger.debug("ShutdownRequest::process() - ClientHandler was not authenticated, cannot continue");
    }
    else
    {
      if (mAdmin)
      {
        sController.onShutdownRequest();
      }
      else
      {
        sLogger.warn("Received shutdown request from non-authorized client. Ignoring...");
      }
      result = true;
    }
    
    sLogger.debug("ShutdownRequest::process() - OUT, Result=" + result);
    return result;
  }
  
  /***************************************************************************************************************
   *  
   */
  public ERequestType getRequestType()
  {
    return ERequestType.cShutdown;
  }
  
  /***************************************************************************************************************
   *  
   */
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append(name())
      .append("(ByAdmin=").append(mAdmin).append(")");
    return sb.toString();
  }
  
  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    sb.append(name()).append("(\n")
      .append(pad).append("  Admin=(").append(mAdmin).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
