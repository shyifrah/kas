package com.kas.mq.server;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import com.kas.infra.base.ConsoleLogger;
import com.kas.infra.base.TimeStamp;
import com.kas.infra.logging.IBaseLogger;
import com.kas.infra.utils.RunTimeUtils;

/**
 * MQ server launcher.<br>
 * <br>
 * This class has only static methods in order to launch KAS/MQ server. It basically processes the arguments passed
 * to the {@link #main(String[])} function and creates the {@link KasMqServer} object.
 * 
 * @author Pippo
 */
public class KasMqLauncher
{
  static private IBaseLogger sLogger = new ConsoleLogger(KasMqLauncher.class.getSimpleName());
  
  static public void main(String [] args)
  {
    TimeStamp start = new TimeStamp();
    sLogger.info("KAS/MQ launcher started at " + start.toString());
    
    // process argument list if any
    Map<String, String> pArgumentsMap = new HashMap<String, String>();
    if ((args != null) && (args.length > 0))
    {
      for (String arg : args)
      {
        String [] keyValue = arg.split("=");
        if (keyValue.length > 2)
        {
          sLogger.warn("Invalid argument: '" + arg + "'. Argument should be in key=value format. Ignoring...");
        }
        else if (keyValue.length <= 1)
        {
          sLogger.warn("Invalid argument: '" + arg + "'. Argument should be in key=value format. Ignoring...");
        }
        else
        {
          String key = keyValue[0];
          String value = keyValue[1];
          pArgumentsMap.put(key, value);
        }
      }
    }
    
    // home directory
    String kasHome = pArgumentsMap.get(RunTimeUtils.cProductHomeDirProperty);
    if (kasHome != null)
    {
      File dir = new File(kasHome);
      if ((dir.exists()) && (dir.isDirectory()) && (dir.canRead()))
      {
        kasHome = dir.getAbsolutePath();
      }
    }
    else
    {
      kasHome = RunTimeUtils.getProductHomeDir();
    }
    sLogger.info("KAS/MQ home directory was set: " + kasHome);
    
    TimeStamp end = new TimeStamp();
    sLogger.info("KAS/MQ launcher ended at " + end.toString());
  }
}
