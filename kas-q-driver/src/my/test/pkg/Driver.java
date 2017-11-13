package my.test.pkg;

import java.util.ArrayList;
import java.util.List;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import com.kas.infra.base.KasException;
import com.kas.q.KasqQueue;
import com.kas.q.KasqTextMessage;
import com.kas.q.ext.IKasqMessage;
import com.kas.q.ext.KasqClient;

public class Driver
{
  private final static String cQueueName = "shy.local.queue";
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
    
    KasqClient client = new KasqClient(cHostname, cPort);
    
    if (client != null)
    {
      System.out.println("Driver::run() - client created");
      
      client.init();
      
      ConnectionFactory factory = client.getFactory();
      
      try
      {
        Connection conn = factory.createConnection(userName, password);
        System.out.println("connection created: " + conn.toString());
        
        Session sess = conn.createSession();
        
        /*
        KasqTextMessage textMessage1 = (KasqTextMessage)sess.createTextMessage("shy1");
        KasqTextMessage textMessage2 = (KasqTextMessage)sess.createTextMessage("shy2");
        KasqTextMessage textMessage3 = (KasqTextMessage)sess.createTextMessage("shy3");
        
        KasqQueue q = new KasqQueue("shyq", "shyQmgr");
        q.put(textMessage1);
        q.put(textMessage2);
        q.put(textMessage3);
        
        System.out.println("queue: " + q.toPrintableString(0));
        
        IKasqMessage msg = q.get();
        while (msg != null)
        {
          System.out.println("read msg: " + msg.toPrintableString(0));
          msg = q.get();
        }
        */
        
        Queue queue = sess.createQueue(cQueueName);
        
        
        sendThreeMessages(sess, queue);
        //sendThreeMessages(sess, queue);
        //
        sleepForSeconds(300);
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
  
  //============================================================================================================================================
  //
  //
  //
  //============================================================================================================================================
  private void sendThreeMessages(Session session, Queue queue) throws JMSException
  {
    MessageProducer producer = session.createProducer(queue);
    
    for (int i = 0; i < 10; i++)
    {
      TextMessage msg = session.createTextMessage("shyifrah-" + Integer.toString(i));
      producer.send(queue, msg);
    }
  }
  
  //============================================================================================================================================
  //
  //
  //
  //============================================================================================================================================
  private void sleepForSeconds(int seconds)
  {
    try
    {
      Thread.sleep((long)(seconds * 1000));
    }
    catch (Throwable e) {}
  }
}
