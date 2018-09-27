package com.kas.mq.admin.commands;

import java.util.Scanner;
import com.kas.infra.base.Properties;
import com.kas.infra.base.UniqueId;
import com.kas.infra.typedef.TokenDeque;
import com.kas.mq.impl.MqContext;
import com.kas.mq.impl.MqStringMessage;
import com.kas.mq.impl.IMqGlobals.EQueryType;
import com.kas.mq.impl.internal.IMqConstants;

/**
 * A QUERY CONNECTION command
 * 
 * @author Pippo
 */
public class QryConnectionCommand extends ACliCommand
{
  /**
   * Construct a {@link QryConnectionCommand} passing the command arguments and the client object
   * that will perform actions on behalf of this command.
   * 
   * @param scanner A scanner to be used in case of further interaction is needed 
   * @param args The command arguments specified when command was entered
   * @param client The client that will perform the actual connection
   */
  protected QryConnectionCommand(Scanner scanner, TokenDeque args, MqContext client)
  {
    super(scanner, args, client);
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
   * A query connection command.<br>
   * <br>
   * The command expects the "QUERY CONNECTION" followed by a zero or one argument. For zero arguments -
   * it's just as if "ALL" was specified. For one argument - it should be a valid connection ID.<br>
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
    EQueryType qType = EQueryType.cQueryConnection;
    
    if (!opt.equals("ALL"))
    {
      UniqueId uuid = null;
      try
      {
        uuid = UniqueId.fromString(opt);
        qprops.put(IMqConstants.cKasPropertyQrysConnId, uuid.toString());
      }
      catch (IllegalArgumentException e)
      {
        writeln("Invalid connection id \"" + opt + "\"");
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
