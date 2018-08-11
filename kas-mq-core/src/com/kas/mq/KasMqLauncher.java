package com.kas.mq;

import java.io.File;
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
  static private final String cAppTypeSystemProperty = "kas.appl";
  
  static private IBaseLogger sLogger = new ConsoleLogger(KasMqLauncher.class.getName());
  
  static public void main(String [] args)
  {
    TimeStamp start = new TimeStamp();
    sLogger.info("KAS/MQ launcher started at " + start.toString());
    
    Map<String, String> pArgumentsMap = getAndProcessStartupArguments(args);
    
    String kasHome = pArgumentsMap.get(cKasHomeSystemProperty);
    setHomeDirectory(kasHome);
    
    String appType = pArgumentsMap.get(cAppTypeSystemProperty);
    String className = getApplicationClass(appType);
    
    TimeStamp end = new TimeStamp();
    reportLaunchTime(start, end);
    
    
    if (className == null)
      sLogger.info("KAS/MQ launcher terminates. Could not determine which application should be launched...");
    else
      launchApplication(className);
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
   * Get the application class name
   * 
   * @param appType the value of the "kas.appl" argument passed to {@link #main(String[]) main} function
   * @return the name of the class to be loaded and executed
   */
  static private String getApplicationClass(String appType)
  {
    String className = null;
    if (appType == null)
    {
      sLogger.fatal("Missing application type. '" + cAppTypeSystemProperty + "' should be set to either 'client' or 'server'");
    }
    else if ("client".equalsIgnoreCase(appType))
    {
      className = "com.kas.mq.client.KasMqClient";
      RunTimeUtils.setProperty(cAppTypeSystemProperty, "client", true);
      sLogger.info("KAS/MQ launcher set system property '" + cAppTypeSystemProperty + "' to 'client'");
    }
    else if ("server".equalsIgnoreCase(appType))
    {
      className = "com.kas.mq.server.KasMqServer";
      RunTimeUtils.setProperty(cAppTypeSystemProperty, "server", true);
      sLogger.info("KAS/MQ launcher set system property '" + cAppTypeSystemProperty + "' to 'server'");
    }
    else if ("admin".equalsIgnoreCase(appType))
    {
      className = "com.kas.mq.appl.KasMqAdmin";
      RunTimeUtils.setProperty(cAppTypeSystemProperty, "admin", true);
      sLogger.info("KAS/MQ launcher set system property '" + cAppTypeSystemProperty + "' to 'admin'");
    }
    else
    {
      sLogger.fatal("Invalid application type. '" + cAppTypeSystemProperty + "' should be set to either 'client' or 'server'");
    }
    return className;
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
  static private void launchApplication(String className)
  {
    try
    {
      Class<?> cls = Class.forName(className);
      Object instance = cls.newInstance();
      if (!(instance instanceof AKasMqAppl))
      {
        sLogger.error("KAS/MQ application not an instance of basic application class: " + AKasMqAppl.class.getName());
      }
      else
      {
        AKasMqAppl app = (AKasMqAppl)instance;
        boolean init = app.init();
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
    catch (Exception e)
    {
      sLogger.fatal("KAS/MQ launcher failed to instantiate application class: " + className);
    }
  }
}
