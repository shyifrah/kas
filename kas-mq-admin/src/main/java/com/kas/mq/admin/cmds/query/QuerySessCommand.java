package com.kas.mq.admin.cmds.query;

import com.kas.infra.base.Properties;
import com.kas.infra.base.UniqueId;
import com.kas.infra.typedef.TokenDeque;
import com.kas.mq.admin.cmds.ACliCommand;
import com.kas.mq.impl.MqContext;
import com.kas.mq.impl.EQueryType;
import com.kas.mq.impl.messages.MqStringMessage;
import com.kas.mq.internal.IMqConstants;

/**
 * A QUERY SESSION command
 * 
 * @author Pippo
 */
public class QuerySessCommand extends ACliCommand
{
  /**
   * Construct a {@link QuerySessCommand} passing the command arguments and the client object
   * that will perform actions on behalf of this command.
   * 
   * @param args The command arguments specified when command was entered
   * @param client The client that will perform the actual connection
   */
  protected QuerySessCommand(TokenDeque args, MqContext client)
  {
    super(args, client);
  }

  /**
   * Display help screen for this command.
   * Not in use for QUERY SESSION.
   */
  public void help()
  {
    writeln(" ");
  }
  
  /**
   * A query session command.<br>
   * <br>
   * The command expects the "QUERY SESSION" followed by a zero or one argument. For zero arguments -
   * it's just as if "ALL" was specified. For one argument - it should be a valid session ID.<br>
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
    EQueryType qType = EQueryType.QUERY_SESSION;
    
    if (!opt.equals("ALL"))
    {
      UniqueId uuid = null;
      try
      {
        uuid = UniqueId.fromString(opt);
        qprops.put(IMqConstants.cKasPropertyQuerySessId, uuid.toString());
      }
      catch (IllegalArgumentException e)
      {
        writeln("Invalid session id \"" + opt + "\"");
        writeln(" ");
        return false;
      }
    }
    
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
