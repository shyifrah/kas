package com.kas.mq;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import com.kas.infra.base.ConsoleLogger;
import com.kas.infra.base.TimeStamp;
import com.kas.infra.logging.IBaseLogger;
import com.kas.infra.utils.RunTimeUtils;

/**
 * MQ launcher.<br>
 * <br>
 * This class has only static methods in order to launch KAS/MQ, server or client all the same.
 * It basically processes the arguments passed to the {@link #main(String[])} function and creates the object that should be executed:
 * KasMqServer for server applications, or KasMqClient for client applications
 * 
 * @author Pippo
 */
public class KasMqLauncher
{
  static private final String cKasHomeSystemProperty = RunTimeUtils.cProductHomeDirProperty;
  static private final String cAppClassSystemProperty = "kas.class";
  
  static private IBaseLogger sLogger = new ConsoleLogger(KasMqLauncher.class.getName());
  
  static public void main(String [] args)
  {
    TimeStamp start = TimeStamp.now();
    sLogger.info("KAS/MQ launcher started at " + start.toString());
    
    Map<String, String> pArgumentsMap = getAndProcessStartupArguments(args);
    
    String kasHome = pArgumentsMap.get(cKasHomeSystemProperty);
    setHomeDirectory(kasHome);
    
    TimeStamp end = TimeStamp.now();
    reportLaunchTime(start, end);
    
    
    String appClass = pArgumentsMap.get(cAppClassSystemProperty);
    if (appClass == null)
      sLogger.info("KAS/MQ launcher terminates. Could not determine which application should be launched...");
    else
      launchApplication(appClass, pArgumentsMap);
  }
  
  /**
   * Check validity of startup arguments and return a map of arguments of keys and values
   * 
   * @param args An array of arguments passed to {@link #main(String[] main} function
   * @return a map of arguments
   */
  static private Map<String, String> getAndProcessStartupArguments(String [] args)
  {
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
    return pArgumentsMap;
  }
  
  /**
   * Set the home directory in a system property
   * 
   * @param kasHome the value of the "kas.home" argument passed to {@link #main(String[]) main} function
   */
  static private void setHomeDirectory(String kasHome)
  {
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
    RunTimeUtils.setProperty(cKasHomeSystemProperty, kasHome, true);
    sLogger.info("KAS/MQ launcher set system property '" + cKasHomeSystemProperty + "' to '" + kasHome + "'");
  }
  
 /**
   * Report launch time
   * 
   * @param start The {@link TimeStamp timestamp} of the time the launcher started
   * @param end The {@link TimeStamp timestamp} of the time the launcher ended
   */
  static private void reportLaunchTime(TimeStamp start, TimeStamp end)
  {
    long diff = end.diff(start);
    long millis = diff % 1000;
    diff = diff / 1000;
    long seconds = diff % 60;
    diff = diff / 60;
    long minutes = diff % 60;
    diff = diff / 60;
    long hours = diff; 
    sLogger.info("KAS/MQ launcher ended at " + end.toString());
    sLogger.info("Total startup time took " + 
        (hours > 0 ? hours + " hours, " : "") + 
        (minutes > 0 ? minutes + " minutes, " : "") +
        String.format("%d.%03d seconds", seconds, millis));
  }
  
  /**
   * Launch the application
   * 
   * @param className The name of the class to be loaded
   */
  static private void launchApplication(String className, Map<String, String> args)
  {
    AKasMqAppl app = null;
    boolean init = false;
    try
    {
      Class<?> cls = Class.forName(className);
      Constructor<?> ctor = cls.getConstructor(Map.class);
      Object instance = ctor.newInstance(args);
      if (!(instance instanceof AKasMqAppl))
      {
        sLogger.error("KAS/MQ application not an instance of basic application class: " + AKasMqAppl.class.getName());
      }
      else
      {
        app = (AKasMqAppl)instance;
        init = app.init();
        if (!init)
        {
          sLogger.error("KAS/MQ application failed initialization. See previous error messages. Terminating...");
        }
        
        app.run();
        app.term();
      }
    }
    catch (ClassNotFoundException e)
    {
      sLogger.fatal("KAS/MQ launcher failed to locate application class: " + className);
    }
    catch (NoSuchMethodException e)
    {
      sLogger.fatal("KAS/MQ launcher failed to locate appropriate constructor for class: " + className);
    }
    catch (Exception e)
    {
      sLogger.fatal("KAS/MQ launcher failed. Exception encountered: ", e);
    }
    finally
    {
      if (init) app.term();
    }
  }
}
