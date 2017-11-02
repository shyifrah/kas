package my.test.pkg;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import com.kas.infra.base.KasException;
import com.kas.q.ext.impl.KasqClient;

public class Driver
{
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
      Driver driver = new Driver();
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
    
    KasqClient client = new KasqClient("localhost", 14560);
    
    if (client != null)
    {
      System.out.println("Driver::run() - client created");
      
      client.init();
      
      ConnectionFactory factory = client.getFactory();
      
      try
      {
        Connection conn = factory.createConnection(userName, password);
        Session    sess = conn.createSession();
        
        Destination dest = null;
        
        MessageProducer producer = sess.createProducer(dest);
        TextMessage     msg  = sess.createTextMessage();
        
        System.out.println("Driver::run() - Message: " + msg.toString());
        
        producer.send(dest, msg);
      }
      catch (JMSException e)
      {
        System.out.println("Driver::run() - JMSException: ");
        e.printStackTrace();
      }
      
      client.term();
    }

    System.out.println("Driver::run() - OUT");
  }
}
