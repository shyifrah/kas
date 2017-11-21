package com.kas.q.server.req;

import java.io.IOException;
import javax.jms.JMSException;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.q.KasqMessage;
import com.kas.q.ext.IKasqConstants;
import com.kas.q.ext.IKasqDestination;
import com.kas.q.ext.IKasqMessage;
import com.kas.q.server.KasqRepository;
import com.kas.q.server.KasqServer;
import com.kas.q.server.internal.ClientHandler;
import com.kas.q.server.internal.IController;

public class RequestProcessor
{
  /***************************************************************************************************************
   * 
   */
  private static ILogger        sLogger     = LoggerFactory.getLogger(RequestProcessor.class);
  private static KasqRepository sRepository = KasqServer.getInstance().getRepository();
  private static IController    sController = KasqServer.getInstance().getController();
  
  /***************************************************************************************************************
   * Process a {@link PutRequest}.
   * 
   * We locate the destination by it name in the repository and call put() on that destination.
   * If we don't find the destination, we put the message in the dead queue.
   * 
   * @param the request object
   */
  public static void handlePutRequest(PutRequest request)
  {
    sLogger.debug("RequestProcessor::handlePutRequest() - IN");
    
    IKasqDestination kqDestination = request.getDestination();
    IKasqMessage     kqMessage     = request.getMessage();

    sLogger.debug("RequestProcessor::handlePutRequest() - message destination is managed by KAS/Q. Name=[" + kqDestination.getFormattedName() + "]");
    
    String destinationName = kqDestination.getName();
    IKasqDestination destinationFromRepo = sRepository.locate(destinationName);
    if (destinationFromRepo != null)
    {
      destinationFromRepo.put(kqMessage);
    }
    else
    {
      sLogger.debug("RequestProcessor::handlePutRequest() - Destination is not defined, define it now...");
      boolean defined = sRepository.defineQueue(destinationName);
      if (defined)
      {
        destinationFromRepo = sRepository.locate(destinationName);
        destinationFromRepo.put(kqMessage);
      }
      else
      {
        IKasqDestination deadq = sRepository.getDeadQueue();
        deadq.put(kqMessage);
        sLogger.warn("Destination " + destinationName + " failed definition; message sent to deadq");
      }
    }
    
    sLogger.debug("RequestProcessor::handlePutRequest() - OUT");
  }
  
  /***************************************************************************************************************
   * Process a {@link GetRequest}.
   * 
   * According to the destination type - queue or topic - we locate the destination by its name and call
   * getNoWait() on it.
   * If we get back a message object, we set the origin properties and send it.    
   * 
   * @param the request object
   * 
   * @throws IOException 
   * @throws JMSException 
   */
  public static void handleGetRequest(ClientHandler handler, GetRequest request) throws IOException, JMSException
  {
    sLogger.debug("RequestProcessor::handleGetRequest() - IN");
    
    //
    // TODO: use the following message criteria to select the consumed message
    //
    // get the filtering criteria
    //String selector = "";
    //boolean noLocal = false;
    //try
    //{
    //  selector = request.getStringProperty(IKasqConstants.cPropertyMessageSelector);
    //  noLocal = request.getBooleanProperty(IKasqConstants.cPropertyNoLocal);
    //}
    //catch (Throwable e) {}
    
    IKasqMessage message;
    int code = IKasqConstants.cPropertyResponseCode_Fail;
    String msg = "Failed to retrieve a message within the specified timeout";
    
    // now we address the repository and locate the destination
    if (request.getDestinationType() == IKasqConstants.cPropertyDestinationType_Queue)
    {
      IKasqDestination dest = sRepository.locateQueue(request.getDestinationName());
      message = dest.getNoWait();
    }
    else
    {
      IKasqDestination dest = sRepository.locateTopic(request.getDestinationName());
      message = dest.getNoWait();
    }
    
    
    if (message == null)
    {
      message = new KasqMessage();
    }
    else
    {
      code = IKasqConstants.cPropertyResponseCode_Fail;
      msg  = "";
    }
    
    message.setJMSCorrelationID(request.getJmsMessageId());
    message.setIntProperty(IKasqConstants.cPropertyResponseCode, code);
    message.setStringProperty(IKasqConstants.cPropertyResponseMessage, msg);
      
    sLogger.diag("RequestProcessor::handleGetRequest() - Sending to origin consumed message: " + message.toPrintableString(0));
    handler.send(message);
    
    sLogger.debug("RequestProcessor::handleGetRequest() - OUT");
  }
  
  /***************************************************************************************************************
   * Process a {@link AuthenticateRequest}.
   * 
   * @return true if client authenticated successfully, false otherwise
   * 
   * @throws IOException 
   * @throws JMSException 
   */
  public static boolean handleAuthenticateRequest(ClientHandler handler, AuthenticateRequest request) throws JMSException, IOException
  {
    sLogger.debug("RequestProcessor::handleAuthenticateRequest() - IN");
    boolean authenticated = false;
    
    //String userName = request.getUserName();
    //String password = request.getPassword();
    // TODO: address some security manager and find out if the credentials are okay
    //       if they are okay, set authenticated to "true" and code to "Fail"
    //
    authenticated = true;
    String msg = "";
    int code = IKasqConstants.cPropertyResponseCode_Okay;
    
    IKasqMessage response = new KasqMessage();
    response.setJMSCorrelationID(request.getJmsMessageId());
    response.setIntProperty(IKasqConstants.cPropertyResponseCode, code);
    response.setStringProperty(IKasqConstants.cPropertyResponseMessage, msg);
    
    sLogger.diag("RequestProcessor::handleAuthenticateRequest() - Sending response message: " + response.toPrintableString(0));
    handler.send(response);
    
    sLogger.debug("RequestProcessor::handleAuthenticateRequest() - OUT, Result=" + authenticated);
    return authenticated;
  }
  
  /***************************************************************************************************************
   * Process a {@link ShutdownRequest}.
   * 
   * If the current {@code ShutdownRequest} is an administrative one, we call the controller to shutdown
   * the KAS/Q server. Otherwise, this is an un-authorized request and we deny it.
   * 
   * @param the request object
   */
  public static void handleShutdownRequest(ShutdownRequest request)
  {
    sLogger.debug("RequestProcessor::handleShutdownRequest() - IN");
    
    if (request.isAdmin())
    {
      sController.onShutdownRequest();
    }
    else
    {
      sLogger.warn("Received shutdown request from non-authorized client. Ignoring...");
    }
    
    sLogger.debug("RequestProcessor::handleShutdownRequest() - OUT");
  }
}
