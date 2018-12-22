package com.kas.mq.admin.cmds.alter;

import com.kas.infra.base.Properties;
import com.kas.infra.typedef.TokenDeque;
import com.kas.infra.utils.Validators;
import com.kas.mq.admin.cmds.ACliCommand;
import com.kas.mq.internal.EQueueDisp;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.internal.MqContextConnection;

/**
 * An ALTER QUEUE command
 * 
 * @author Chen
 */
public class AltQueueCommand extends ACliCommand
{
  private boolean mWasDispositionSpecified = false;
  private boolean mWasThresholdSpecified = false;
  
  /**
   * Construct a {@link AltQueueCommand} passing the command arguments and the client object
   * that will perform actions on behalf of this command.
   * 
   * @param args The command arguments specified when command was entered
   * @param client The client that will perform the actual connection
   */
  protected AltQueueCommand(TokenDeque args, MqContextConnection client)
  {
    super(args, client);
  }

  /**
   * Display help screen for this command.
   */
  public void help()
  {
    writeln(" ");
  }
  
  /**
   * An Alter queue command.<br>
   * <br>
   * The command expects the "ALTER QUEUE" followed by at least two arguments. The first argument
   * is the queue name. This argument is mandatory.<br>
   * The second argument (and so forth), is one of the following settings:
   * PERMANENT, TEMPORARY or THRESHOLD <threshold_value>. Note that PERMANENT and TEMPORARY are
   * Mutually exclusive.<br>
   * If more arguments are specified, the command will fail with an "excessive arguments" message 
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
    
    Properties qprops = new Properties();    
    
    String queue = mCommandArgs.poll().toUpperCase();
    if ((queue.length() > 0) && (!Validators.isQueueName(queue)))
    {
      writeln("Invalid queue name \"" + queue + "\"");
      writeln(" ");
      return false;
    }
    
    boolean exitCommand    = false;
    
    while (mCommandArgs.size() > 0)
    {
      String opt = mCommandArgs.poll().toUpperCase();
	    
      switch (opt)
      {
        case "PERMANENT":
        case "PERM":
          exitCommand = handleDispositionOption(EQueueDisp.PERMANENT, qprops);
          if (exitCommand)
            return false;
          break;
        case "TEMPORARY":
        case "TEMP":
          exitCommand = handleDispositionOption(EQueueDisp.TEMPORARY, qprops);
          if (exitCommand)
            return false;
          break;
        case "THRESHOLD":
          exitCommand = handleThresholdOption(qprops);
          if (exitCommand)
            return false;
          break;
        default:
          writeln("Invalid argument: \"" + opt + "\"");
          writeln(" ");
          return false;
      }
    }
    
    mClient.alterQueue(queue, qprops);
    writeln(mClient.getResponse());
    writeln(" ");     
    return false;
  }
  
  /**
   * Handle the TEMPORARY and PERMANENT options
   * 
   * @param newDisp One of the {@link EQueueDisp} values
   * @param qprops The alter command properties
   * @return {@code true} if the command should end, {@code false} otherwise
   */
  private boolean handleDispositionOption(EQueueDisp newDisp, Properties qprops)
  {
    if (mWasDispositionSpecified)
    {
      writeln("Cannot specify two persistency options on the same ALTER command");
      writeln(" ");
      return true;
    }
    
    mWasDispositionSpecified = true;
    qprops.setStringProperty(IMqConstants.cKasPropertyAltDisp, newDisp.name());
    return false;
  }
    
  /**
   * Handle the THRESHOLD option
   * 
   * @param qprops The alter command properties
   * @return {@code true} if the command should end, {@code false} otherwise
   */
  private boolean handleThresholdOption(Properties qprops)
  {
    if (mWasThresholdSpecified)
    {
      writeln("Cannot specify two threshold value options on the same ALTER command");
      writeln(" ");
      return true;
    }
    
    String threshold = mCommandArgs.poll();
    if (threshold == null)
    {
      writeln("Missing threshold value");
      writeln(" ");
      return true;
    }
    
    if (!Validators.isThreshold(threshold)) 
    {         
      writeln("Invalid threshold value: \"" + threshold + "\"");
      writeln(" ");
      return true;
    }
    
    mWasThresholdSpecified = true;
    int th = Integer.parseInt(threshold);
    qprops.setIntProperty(IMqConstants.cKasPropertyAltThreshold, th);
    return false;
  }
}
