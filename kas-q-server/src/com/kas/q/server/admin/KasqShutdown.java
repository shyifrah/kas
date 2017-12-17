package com.kas.q.server.admin;

import java.io.IOException;
import javax.jms.JMSException;
import com.kas.config.MainConfiguration;
import com.kas.infra.base.threads.ThreadPool;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;

public class KasqShutdown
{
  /***************************************************************************************************************
   * Main function.
   * 
   * @param args arguments passed to the main function 
   */
  public static void main(String [] args) throws IOException
  {
    writeln("KAS/Q Terminator started");
    
    if (args.length != 2)
    {
      writeln("Invalid number of arguments");
      writeln("Usage: java -cp <...> " + KasqShutdown.class.getName() + " <hostname> <port>");
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
      
      MainConfiguration config = MainConfiguration.getInstance();
      config.init();
      
      KasqShutdown shutdown = null;
      try
      {
        shutdown = new KasqShutdown(host, port);
      }
      catch (JMSException e)
      {
        writeln(e.getMessage());
      }
      
      writeln("Signal KAS/Q server to shutdown itself...");
      if (shutdown != null) shutdown.run();
      
      
      config.term();
      ThreadPool.shutdownNow();
    }
    
    writeln("KAS/Q Terminator ended");
  }
  
  private ILogger             mLogger;
  private KasqAdminConnection mConnection;
  
  /***************************************************************************************************************
   * Construct a {@code KasqShutdown} object
   * 
   * @param host hostname or IP-address of remote KasQ server
   * @param port port number on which the KasQ server listens for new connections
   */
  private KasqShutdown(String host, int port) throws JMSException
  {
    mLogger     = LoggerFactory.getLogger(this.getClass());
    mConnection = new KasqAdminConnection(host, port);
  }
  
  /***************************************************************************************************************
   * Shutting down: 
   * KAS/Q server shutdown is actually a process in which we send a simple message with a specific set
   * of properties which the KAS/Q server interprets to shutdown itself.
   */
  private void run()
  {
    mLogger.debug("KasqShutdown::shutdown() - IN");

    mConnection.shutdown();
    
    try
    {
      Thread.sleep(3000);
    }
    catch (Throwable e) {}
    
    mLogger.debug("KasqShutdown::shutdown() - OUT");
  }
  
  /***************************************************************************************************************
   * Writing a message to STDOUT
   * 
   * @param message the message to print
   */
  private static void writeln(String message)
  {
    System.out.println(message);
  }
}
