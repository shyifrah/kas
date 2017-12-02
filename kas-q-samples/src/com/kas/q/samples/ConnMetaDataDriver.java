package com.kas.q.samples;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.ConnectionMetaData;
import javax.jms.JMSException;
import com.kas.infra.base.KasException;
import com.kas.q.ext.KasqClient;

public class ConnMetaDataDriver
{
  private final static String cHostname  = "localhost";
  private final static int    cPort      = 14560;
  
  //============================================================================================================================================
  //
  //
  //
  //============================================================================================================================================
  public static void main(String[] args)
  {
    System.out.println("main() - IN");
    
    try
    {
      ConnMetaDataDriver driver = new ConnMetaDataDriver();
      driver.run(args);
    }
    catch (Throwable e)
    {
      System.err.println("main() - Exception caught:");
      e.printStackTrace();
    }
    System.out.println("main() - OUT");
  }
  
  //============================================================================================================================================
  //
  //
  //
  //============================================================================================================================================
  private void run(String [] args) throws KasException
  {
    System.out.println("Driver::run() - IN");
    
    String userName = "kas";
    String password = "kas";
    
    KasqClient client = new KasqClient(cHostname, cPort);
    
    System.out.println("Driver::run() - client created");
    
    client.init();
    
    try
    {
      ConnectionFactory factory = client.getConnectionFactory();
      Connection conn = factory.createConnection(userName, password);
      
      ConnectionMetaData metadata = conn.getMetaData();
      System.out.println("Driver::run() - metadata: ");
      System.out.println("Driver::run() - > JMS version.......: " + metadata.getJMSVersion());
      System.out.println("Driver::run() - > JMS provider......: " + metadata.getJMSProviderName());
      System.out.println("Driver::run() - > Provider version..: " + metadata.getProviderVersion());
    }
    catch (JMSException e)
    {
      System.out.println("Driver::run() - JMSException caught");
      e.printStackTrace();
    }
    
    client.term();

    System.out.println("Driver::run() - OUT");
  }
}
