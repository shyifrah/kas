package com.kas.mq.server;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
  
  static private String sHomeDirectory = null;
  static private String sKey = null;
  static private List<String> sUnknownArguments = new ArrayList<String>();
  
  /**
   * Main function
   * 
   * @param args Arguments passed from command line
   */
  static public void main(String[] args)
  {
    if (args == null)
    {
      sLogger.fatal("Null argument list, cannot launch KAS/MQ server");
      System.exit(1);
    }
    
    TimeStamp start = new TimeStamp();
    
    sLogger.info("KAS/MQ server launched at " + start.toString());
    
    if (args.length > 0)
    {
      for (int i = 0; i < args.length; ++i)
      {
        String param = args[i];
        process(param);
      }
      
      // Verifying we have all necessary data
      
      // Verifying we have our KAS/MQ home directory
      sLogger.info("Verifying data...");
      if (sHomeDirectory == null)
      {
        sLogger.fatal("Home directory could not be located, does not exist or not accessible");
        System.exit(1);
      }
      sLogger.info("Home directory was set to '" + sHomeDirectory + "'");
      
      // Verifying we have key
      if (sKey == null)
      {
        sLogger.fatal("Key not specified");
        System.exit(1);
      }
      sLogger.info("Key was set to '" + sKey + "'");
     
      // Finally, report the user about unprocessed arguments
      for (int i = 0; i < sUnknownArguments.size(); ++i)
      {
        sLogger.warn("Unknown argument ignored: '" + sUnknownArguments.get(i) + "'");
      }
    }
    
    TimeStamp end = new TimeStamp();
    TimeStamp diff = new TimeStamp( end.diff(start) );
    sLogger.info("Startup took " + diff.toString());
  }
  
  /**
   * Process the argument.<br>
   * <br>
   * We check if the argument starts with {@code "kas.home"} and if so - we assume it's the specification of the home directory. If not, continue.
   * Finally, we add the argument to the list of unknown arguments.
   * 
   * @param param The argument from the list
   */
  static private void process(String param)
  {
    // process the parameter
    if (param.startsWith("kas.home="))
    {
      String home = param.substring(9);
      sHomeDirectory = getHomeDir(home);
      return;
    }
    
    if (param.startsWith("key="))
    {
      String value = param.substring(4);
      sKey = value;
      return;
    }
    
    // final entry
    // put the parameter in the list of unknown arguments
    sUnknownArguments.add(param);
  }
  
  /**
   * Extract and verify home directory as specified in the argument.<br>
   * <br>
   * If the value of {@code home} is not a valid home directory, it will be extracted from the system property.
   * 
   * @param home The home directory as specified in the argument
   * @return the KAS/MQ home directory
   */
  static private String getHomeDir(String home)
  {
    String result = null;
    File homeDir = new File(home);
    if ((homeDir.exists()) && (homeDir.canRead()) && (homeDir.isDirectory()))
    {
      result = homeDir.getAbsolutePath();
    }
    
    if (result == null)
      result = RunTimeUtils.getProductHomeDir();
    
    return result;
  }
}
