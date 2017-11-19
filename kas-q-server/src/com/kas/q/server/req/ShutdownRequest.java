package com.kas.q.server.req;

import com.kas.infra.base.AKasObject;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.ext.IKasqConstants;
import com.kas.q.ext.IKasqMessage;

final public class ShutdownRequest extends AKasObject implements IRequest
{
  /***************************************************************************************************************
   * 
   */
  private static ILogger sLogger = LoggerFactory.getLogger(ShutdownRequest.class);
  
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
  public ERequestType getRequestType()
  {
    return ERequestType.cShutdown;
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
