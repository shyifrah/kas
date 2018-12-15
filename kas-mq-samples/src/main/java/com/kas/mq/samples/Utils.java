package com.kas.mq.samples;

import com.kas.infra.base.KasException;
import com.kas.infra.base.TimeStamp;
import com.kas.mq.impl.MqContext;

public class Utils
{
  /**
   * Create queue
   * @param queue
   * @throws KasException 
   */
  static public void createQueue(MqContext client, String queue, int threshold) throws KasException
  {
    System.out.println("Defining queue with name " + queue + " and a threshold of " + (threshold) + " messages");
    boolean defined = client.defineQueue(queue, threshold, true);
    System.out.println("Response: " + client.getResponse());
    if (!defined)
      throw new KasException("failed to define queue with name " + queue);
  }
  
  /**
   * Delete queue
   * @param client
   * @param queue
   */
  static public void deleteQueue(MqContext client, String queue)
  {
    System.out.println("Deleting forcefully queue with name " + queue);
    System.out.println("Response: " + client.getResponse());
    client.deleteQueue(queue, true);
  }
  
  /**
   * Report execution time
   * 
   * @param start The {@link TimeStamp timestamp} of the time the launcher started
   * @param end The {@link TimeStamp timestamp} of the time the launcher ended
   */
  static public String reportTime(TimeStamp start, TimeStamp end)
  {
    long diff = end.diff(start);
    long millis = diff % 1000;
    diff = diff / 1000;
    long seconds = diff % 60;
    diff = diff / 60;
    long minutes = diff % 60;
    diff = diff / 60;
    long hours = diff;
    
    String s1 = (hours > 0 ? hours + " hours, " : "");
    String s2 = (minutes > 0 ? minutes + " minutes, " : "");
    return String.format("%s%s%d.%03d seconds", s1, s2, seconds, millis);
  }
}
