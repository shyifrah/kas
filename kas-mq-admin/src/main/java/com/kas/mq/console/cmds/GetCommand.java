package com.kas.mq.console.cmds;

import com.kas.comm.serializer.EClassId;
import com.kas.infra.utils.ConsoleUtils;
import com.kas.infra.utils.Validators;
import com.kas.mq.console.ACommand;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.impl.messages.MqStringMessage;
import com.kas.mq.internal.MqContextConnection;

/**
 * GET command
 * 
 * @author Pippo
 */
public class GetCommand extends ACommand
{
  /**
   * Get attributes
   */
  private String mQueue;
  private long mTimeout;
  private long mInterval;
  private int mCount;
  
  /**
   * Construct the command and setting its verbs
   */
  GetCommand()
  {
    mCommandVerbs.add("GET");
    mCommandVerbs.add("G");
  }
  
  /**
   * Setting data members
   */
  protected void setup()
  {
    mQueue = getString("QUEUE", null);
    mTimeout = getLong("TIMEOUT", 0L);
    mInterval = getLong("INTERVAL", 1000L);
    mCount = getInteger("COUNT", 1);
  }
  
  /**
   * Execute the command using the specified {@link MqContextConnection}
   * 
   * @param conn The {@link MqContextConnection} that will be used to execute the command
   */
  public void exec(MqContextConnection conn)
  {
    if (!Validators.isQueueName(mQueue))
      throw new IllegalArgumentException("QUEUE was not specified or invalid: [" + mQueue + ']');
    if (mTimeout < 0)
      throw new IllegalArgumentException("Invalid TIMEOUT: [" + mTimeout + ']');
    if (mInterval <= 0)
      throw new IllegalArgumentException("Invalid INTERVAL: [" + mInterval + ']');
    if (mCount <= 0)
      throw new IllegalArgumentException("Invalid COUNT: [" + mCount + ']');
    
    for (int i = 0; i < mCount; ++i)
    {
      IMqMessage imsg = conn.get(mQueue, mTimeout, mInterval);
      ConsoleUtils.writeln(conn.getResponse());
      if ((imsg != null) && (imsg.createHeader().getClassId() == EClassId.cClassMqStringMessage))
      {
        MqStringMessage msg = (MqStringMessage)imsg; 
        ConsoleUtils.writeln(msg.getBody());
      }
    }
  }
  
  /**
   * Get the command text
   * 
   * @return the command text
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("GET").append('\n')
      .append("  QUEUE(").append(mQueue).append(")\n")
      .append("  TIMEOUT(").append(mTimeout).append(")\n")
      .append("  INTERVAL(").append(mInterval).append(")\n")
      .append("  COUNT(").append(mCount).append(")\n");
    return sb.toString();
  }
}
