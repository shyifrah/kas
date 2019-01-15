package com.kas.mq.console.cmds;

import com.kas.infra.utils.ConsoleUtils;
import com.kas.infra.utils.Validators;
import com.kas.mq.console.ACommand;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.impl.messages.MqMessageFactory;
import com.kas.mq.internal.MqContextConnection;

/**
 * PUT command
 * 
 * @author Pippo
 */
public class PutCommand extends ACommand
{
  /**
   * Get attributes
   */
  private String mQueue;
  private String mText;
  private int mCount;
  
  /**
   * Construct the command and setting its verbs
   */
  PutCommand()
  {
    mCommandVerbs.add("PUT");
    mCommandVerbs.add("P");
  }
  
  /**
   * Setting data members
   */
  protected void setup()
  {
    mQueue = getString("QUEUE", null);
    mText = getString("TEXT", "");
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
    if (mCount <= 0)
      throw new IllegalArgumentException("Invalid COUNT: [" + mCount + ']');
    
    for (int i = 0; i < mCount; ++i)
    {
      
      IMqMessage msg = MqMessageFactory.createStringMessage(mText);
      conn.put(mQueue, msg);
      ConsoleUtils.writeln(conn.getResponse());
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
    sb.append("PUT").append('\n')
      .append(" QUEUE(").append(mQueue).append(")\n")
      .append(" TEXT(").append(mText).append(")\n")
      .append(" COUNT(").append(mCount).append(")\n");
    
    return sb.toString();
  }
}
