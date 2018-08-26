package com.kas.mq.client;

import java.util.Map;
import com.kas.infra.base.KasException;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.AKasMqAppl;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.MqContext;
import com.kas.mq.impl.MqMessageFactory;
import com.kas.mq.impl.MqTextMessage;

public class KasMqClient extends AKasMqAppl 
{
  static private final String cHost = "localhost";
  static private final int    cPort = 14560;
  static private final String cUser = "admin";
  static private final String cPass = "admin";
  
  static private final String cQueueName = "temp.admin.queue";
  static private final int cNumOfMessages = 10;
  
  public KasMqClient(Map<String, String> args)
  {
    super(args);
  }
  
  public String toPrintableString(int level)
  {
    return null;
  }
  
  public boolean init()
  {
    super.init();
    return true;
  }
  
  public boolean term()
  {
    super.term();
    return true;
  }
  
  public void run()
  {
    MqContext client = new MqContext();
    try
    {
      client.connect(cHost, cPort, cUser, cPass);
      boolean defined = client.define(cQueueName);
      if (defined)
      {
        try
        {
          client.open(cQueueName);
          
          // put messages
          for (int i = 0; i < cNumOfMessages; ++i)
          {
            String messageBody = "message number: " + (i + 1);
            MqTextMessage putMessage = MqMessageFactory.createTextMessage(messageBody);
            putMessage.setPriority(i);
            System.out.println("Write message: " + putMessage.getBody());
            client.put(putMessage);
          }
          
          // get messages
          long timeout = 1000L;
          long interval = 1000L;
          IMqMessage<?> getMessage = client.get(timeout, interval);
          System.out.println("Read message: " + StringUtils.asPrintableString(getMessage));
          while (getMessage != null)
          {
            getMessage = client.get(timeout, interval);
            System.out.println("Read message: " + StringUtils.asPrintableString(getMessage));
          }
          
          client.close();
        }
        catch (KasException e)
        {
          if (client.isOpen())
          {
            try
            { client.close(); }
            catch (KasException e1) {}
          }
        }
        
        client.delete(cQueueName);
      }
    }
    catch (KasException e)
    {
      e.printStackTrace();
    }
    finally
    {
      if (client != null)
      {
        if (client.isOpen())
        {
          try
          { client.close(); }
          catch (KasException e) {}
        }
        
        if (client.isConnected())
        {
          try
          { client.disconnect(); }
          catch (KasException e) {}
        }
      }
    }
  }
}
