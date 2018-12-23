package com.kas.mq.admin.cmds.query;

import com.kas.infra.base.Properties;
import com.kas.infra.typedef.TokenDeque;
import com.kas.mq.admin.cmds.ACliCommand;
import com.kas.mq.impl.EQueryConfigType;
import com.kas.mq.impl.EQueryType;
import com.kas.mq.impl.messages.MqStringMessage;
import com.kas.mq.internal.IMqConstants;
import com.kas.mq.internal.MqContextConnection;

/**
 * A QUERY CONFIGURATION command
 * 
 * @author Pippo
 */
public class QueryConfCommand extends ACliCommand
{
  /**
   * Construct a {@link QueryConfCommand} passing the command arguments and the client object
   * that will perform actions on behalf of this command.
   * 
   * @param args The command arguments specified when command was entered
   * @param client The client that will perform the actual connection
   */
  protected QueryConfCommand(TokenDeque args, MqContextConnection client)
  {
    super(args, client);
  }

  /**
   * Display help screen for this command.
   * Not in use for QUERY CONFIG.
   */
  public void help()
  {
    writeln(" ");
  }
  
  /**
   * A query configuration command.<br>
   * <br>
   * The command expects the "QUERY CONFIGURATION" followed by a zero or one argument. For zero arguments -
   * it's just as if "ALL" was specified. For one argument - it should be one of "LOGGING", "MQ", or "SERIALIZER" 
   * to get the specific configuration.<br>
   * If more arguments are specified, the command will fail with an "excessive arguments" message 
   * 
   * @return {@code false} always because there is no way that this command will terminate the command processor.
   */
  public boolean run()
  {
    String opt = mCommandArgs.poll();
    if (opt == null)
      opt = "ALL";
    opt = opt.toUpperCase();
    
    Properties qprops = new Properties();
    EQueryType qType = EQueryType.QUERY_CONFIG;
    int confType = -1;
    if (opt.equals("ALL"))
      confType = EQueryConfigType.ALL.ordinal();
    else if (opt.equals("LOGGING"))
      confType = EQueryConfigType.LOGGING.ordinal();
    else if (opt.equals("MQ"))
      confType = EQueryConfigType.MQ.ordinal();
    else if (opt.equals("DB"))
      confType = EQueryConfigType.DB.ordinal();
    
    if (confType == -1)
    {
      writeln("Invalid query type \"" + opt + "\"");
      writeln(" ");
      return false;
    }
    
    qprops.setIntProperty(IMqConstants.cKasPropertyQueryConfigType, confType);
    
    if (mCommandArgs.size() > 0)
    {
      writeln("Excessive token \"" + mCommandArgs.poll() + "\"");
      writeln(" ");
      return false;
    }
    
    MqStringMessage result = mClient.queryServer(qType, qprops);
    if (result != null)
      writeln(result.getBody());
    writeln(mClient.getResponse());
    writeln(" ");
    return false;
  }
}
