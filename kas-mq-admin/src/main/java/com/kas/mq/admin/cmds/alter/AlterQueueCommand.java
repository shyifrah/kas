package com.kas.mq.admin.cmds.alter;

import com.kas.infra.base.Properties;
import com.kas.infra.typedef.TokenDeque;
import com.kas.infra.utils.Validators;
import com.kas.mq.admin.cmds.ACliCommand;
import com.kas.mq.impl.MqContext;
import com.kas.mq.impl.EQueryType;
import com.kas.mq.impl.messages.MqStringMessage;
import com.kas.mq.internal.IMqConstants;

/**
 * AN ALTER QUEUE command
 * 
 * @author chen
 */
public class AlterQueueCommand extends ACliCommand
{
  /**
   * Construct a {@linkAlterQueueCommand} passing the command arguments and the client object
   * that will perform actions on behalf of this command.
   * 
   * @param args The command arguments specified when command was entered
   * @param client The client that will perform the actual connection
   */
  protected AlterQueueCommand(TokenDeque args, MqContext client)
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
   * An alter queue command.<br>
   * <br>
   * The command expects the "ALTER QUEUE" followed by two arguments. The first argument
   * is the queue name. This argument is mandatory.<br>
   * The second argument is the settings that needs to be changed for that queue.
   * Current support settings are: PERMANENT, TEMPORARY or THRESHOLD=<threshold_value>
   * If more arguments are specified, the command will fail with an "excessive arguments" message 
   * 
   * a q q1 permanent threshold(1)
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
    qprops.setStringProperty(IMqConstants.cKasPropertyQueryQueueName, queue);
    
    String opt = mCommandArgs.poll().toUpperCase();
    if (opt == null) 
    {
    	writeln("missing property to alter");
    	writeln(" ");
    	return false;
    }    
    
    String val=null;    
    if (opt.equals("PERMANENT")) 
    {
    	qprops.setBoolProperty(IMqConstants.cKasPropertyAltPermanent, true);
    }
	else if (opt.equals("TEMPORARY")) 
	{
		qprops.setBoolProperty(IMqConstants.cKasPropertyAltPermanent, false);
	}
	else if (opt.equals("THRESHOLD")) 
	{
			String threshold = mCommandArgs.poll();
			writeln(threshold);
		    if ((threshold.length() > 0) && (!Validators.isThreshold(threshold))) 
		    {		    	
		    	writeln("invalid threshold value");
		    	writeln(" ");
		    	return false;
		    }else
		    {
		    	qprops.setStringProperty(IMqConstants.cKasPropertyAltThreshold, threshold);
		    }
    }else {    	   
    	writeln("Not supported option");
    	writeln(" ");    
    	return false;
    }
    
    if (mCommandArgs.size() > 0)
    {
      writeln("Excessive token \"" + mCommandArgs.poll() + "\"");
      writeln(" ");
      return false;
    }
      
    mClient.alterQueue(queue, qprops);
    writeln(mClient.getResponse());
    writeln(" ");     
    return false;
  }
}
