package com.kas.mq.admin.cmds.def;

import com.kas.infra.typedef.TokenDeque;
import com.kas.infra.utils.Validators;
import com.kas.mq.admin.cmds.ACliCommand;
import com.kas.mq.internal.EQueueDisp;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.internal.MqContextConnection;

/**
 * A DEFINE TEMPQUEUE command
 * 
 * @author Pippo
 */
public class DefTempQueueCommand extends ACliCommand
{
  /**
   * Construct a {@link DefTempQueueCommand} passing the command arguments and the client object
   * that will perform actions on behalf of this command.
   * 
   * @param args The command arguments specified when command was entered
   * @param client The client that will perform the actual connection
   */
  protected DefTempQueueCommand(TokenDeque args, MqContextConnection client)
  {
    super(args, client);
  }

  /**
   * Display help screen for this command.
   * Not in use for DEFINE TEMPQUEUE.
   */
  public void help()
  {
    writeln(" ");
  }
  
  /**
   * A Define temporary queue command.<br>
   * <br>
   * For only the "DEFINE TEMPQUEUE" verb, the command will fail with a missing queue name message.
   * The next argument is the queue name followed by the queue threshold. If queue threshold is missing,
   * than the default threshold is used.
   * If more arguments exist, the command will fail with excessive arguments message.
   * 
   * @return {@code false} always because there is no way that this command will terminate the command processor.
   */
  public boolean run()
  {
    if (mCommandArgs.size() == 0)
    {
      writeln("Missing queue name");
      writeln(" ");
      return false;
    }
    
    String queue = mCommandArgs.poll().toUpperCase();
    if (!Validators.isQueueName(queue))
    {
      writeln("Invalid queue name \"" + queue + "\"");
      writeln(" ");
      return false;
    }
    
    String sthreshold = mCommandArgs.poll();
    if (sthreshold == null)
      sthreshold = "" + IMqConstants.cDefaultQueueThreshold;
    
    int threshold = -1;
    try
    {
      threshold = Integer.valueOf(sthreshold.toUpperCase());
    }
    catch (NumberFormatException e) {}
    
    if (threshold <= 0)
    {
      writeln("Invalid threshold number \"" + sthreshold + "\"");
      writeln(" ");
      return false;
    }
    
    if (mCommandArgs.size() > 0)
    {
      writeln("Excessive token \"" + mCommandArgs.poll() + "\"");
      writeln(" ");
      return false;
    }
    
    mClient.defineQueue(queue, "", threshold, EQueueDisp.TEMPORARY);
    writeln(mClient.getResponse());
    writeln(" ");
    return false;
  }
}
