package com.kas.q.server;

import java.io.IOException;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import com.kas.config.MainConfiguration;
import com.kas.q.ext.IKasqConstants;
import com.kas.q.ext.KasqClient;

public class KasqShutdown extends KasqClient
{
  /***************************************************************************************************************
   * Main function.
   * 
   * @param args arguments passed to the main function 
   */
  public static void main(String [] args) throws IOException
  {
    System.out.println("KAS/Q Terminator started");
    
    try
    {
      MainConfiguration mainConfig = new MainConfiguration();
      mainConfig.init();
      
      KasqShutdown shutdown = null;
      if (args.length == 2)
      {
        String host = args[0];
        int    port = -1;
        try
        {
          port = Integer.valueOf(args[1]);
        }
        catch (Throwable e) {}
        
        shutdown = new KasqShutdown(host, port);
        shutdown.init();
        
        shutdown.shutdown();
        
        shutdown.term();
      }
      
      mainConfig.term();
    }
    catch (Throwable e) {}
    
    System.out.println("KAS/Q Terminator ended");
  }
  
  /***************************************************************************************************************
   * Construct a {@code KasqShutdown} objcet
   * 
   * @param host hostname or IP-address of remote KasQ server
   * @param port port number on which the KasQ server listens for new connections
   */
  
  private KasqShutdown(String host, int port)
  {
    super(host, port);
  }
  
  //============================================================================================================================================
  //
  //
  //
  //============================================================================================================================================
  private void shutdown() throws JMSException
  {
    String userName = "admin";
    String password = "admin";
    
    ConnectionFactory factory = getFactory();
    Connection        conn    = factory.createConnection(userName, password);
    Session           sess    = conn.createSession();
    MessageProducer   prod    = sess.createProducer(null);
    Message           msg     = sess.createMessage();
    msg.setBooleanProperty(IKasqConstants.cPropertyAdminMessage, true);
    msg.setIntProperty(IKasqConstants.cPropertyRequestType, IKasqConstants.cPropertyRequestType_Shutdown);
    
    prod.send(msg);
  }
}
