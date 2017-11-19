package com.kas.q.server;

import java.io.IOException;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
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
    
    KasqShutdown shutdown = null;
    
    try
    {
      if (args.length != 2)
      {
        System.out.println("Invalid number of arguments");
        System.out.println("Usage: java -cp <...> com.kas.q.server.KasqShutdown <hostname> <port>");
      }
      else
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
        
        
        System.out.println("Signal KAS/Q server to shutdown itself...");
        shutdown.shutdown();
      }
    }
    catch (Throwable e)
    {
      System.out.println("KAS/Q Terminator ended unexpectedly. Exception caught:");
      e.printStackTrace();
    }
    finally
    {
      if (shutdown != null)
        shutdown.term();
    }
    
    System.out.println("KAS/Q Terminator ended");
  }
  
  /***************************************************************************************************************
   * Construct a {@code KasqShutdown} object
   * 
   * @param host hostname or IP-address of remote KasQ server
   * @param port port number on which the KasQ server listens for new connections
   */
  private KasqShutdown(String host, int port)
  {
    super(host, port);
  }
  
  /***************************************************************************************************************
   * Shutting down: 
   * KAS/Q server shutdown is actually a process in which we send a simple message with a specific set
   * of properties which the KAS/Q server interprets to shutdown itself.
   */
  private void shutdown() throws JMSException
  {
    try
    {
      Thread.sleep(10000);
    }
    catch (Throwable e) {}
    
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
    
    try
    {
      Thread.sleep(10000);
    }
    catch (Throwable e) {}
  }
}
