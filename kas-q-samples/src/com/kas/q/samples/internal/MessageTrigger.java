package com.kas.q.samples.internal;

import javax.jms.Message;
import javax.jms.MessageListener;
import com.kas.q.KasqMessage;

public class MessageTrigger implements MessageListener
{

  public void onMessage(Message message)
  {
    if (message instanceof KasqMessage)
    {
      KasqMessage kqMessage = (KasqMessage)message;
      System.out.println("MDB::onMessage() - Got message=" + kqMessage.toPrintableString(0));
    }
  }
}
