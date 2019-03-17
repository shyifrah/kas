package com.kas.appl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kas.infra.base.TimeStamp;
import com.kas.infra.utils.RunTimeUtils;

/**
 * KAS application launcher.<br>
 * This class is used to launch a KAS application.
 * 
 * @author Pippo
 */
public class AppLauncher
{
  static private final String cKasHomeSystemProperty = RunTimeUtils.cProductHomeDirProperty;
  
  static private Logger sStartupLogger = LogManager.getLogger(AppLauncher.class);
  
  /**
   * Settings
   */
  private Map<String, String> mSettings;
  
  /**
   * Construct the application launcher
   * 
   * @param mainArgs
   *   An array of arguments passed to main function
   * @param defaultSettings
   *   A map of default settings
   */
  public AppLauncher(String [] mainArgs, Map<String, String> defaultSettings)
  {
    mSettings = defaultSettings;
    mSettings.putAll(getAndProcessStartupArguments(mainArgs));
    String kasHome = mSettings.get(cKasHomeSystemProperty);
    setHomeDirectory(kasHome);
  }
  
  /**
   * Launch the application
   * 
   * @param app
   *   Application to launch
   */
  public void launch(AKasApp app)
  {
    sStartupLogger.info("Launching application " + app.getAppName());
    
    TimeStamp start = null;
    TimeStamp end = null;
    
    try
    {
      start = TimeStamp.now();
      boolean init = app.init();
    
      if (init)
        app.run();
    }
    catch (Throwable e)
    {
      sStartupLogger.error("Exception caught: ", e);
    }
    finally
    {
      if (!app.isTerminated())
        app.term();
      end = TimeStamp.now();
      sStartupLogger.info("Application {} ended", app.getAppName());
      reportRunTime(start, end);
    }
  }
  
  /**
   * Get application's settings map
   * 
   * @return
   *   application's settings map
   */
  public Map<String, String> getSettings()
  {
    return mSettings;
  }
  
  /**
   * Check validity of startup arguments and return a map of arguments of keys and values
   * 
   * @param args
   *   An array of arguments passed to {@link #main(String[] main} function
   * @return
   *   a map of arguments
   */
  static private Map<String, String> getAndProcessStartupArguments(String [] args)
  {
    Map<String, String> pArgumentsMap = new HashMap<String, String>();
    if ((args == null) || (args.length == 0))
    {
      sStartupLogger.info("No arguments passed to KAS launcher, continue...");
    }
    else
    {
      for (String arg : args)
      {
        sStartupLogger.info("Processing argument: {}", arg);
        String [] keyValue = arg.split("=");
        if (keyValue.length > 2)
        {
          sStartupLogger.warn("Invalid argument: '{}'. Argument should be in key=value format. Ignoring...", arg);
        }
        else if (keyValue.length <= 1)
        {
          sStartupLogger.warn("Invalid argument: '{}'. Argument should be in key=value format. Ignoring...", arg);
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
   * @param kasHome
   *   the value of the "kas.home" argument passed to {@link #main(String[]) main} function
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
    
    RunTimeUtils.setProperty(cKasHomeSystemProperty, kasHome, true);
    sStartupLogger.info("KAS launcher set system property '{}' to '{}'", cKasHomeSystemProperty, kasHome);
  }
  
 /**
   * Report launch time
   * 
   * @param start
   *   The {@link TimeStamp timestamp} of the time the launcher started
   * @param end
   *   The {@link TimeStamp timestamp} of the time the launcher ended
   */
  static private void reportRunTime(TimeStamp start, TimeStamp end)
  {
    long diff = TimeStamp.diff(end, start);
    long ms = diff % 1000;
    diff = diff / 1000;
    long sec = diff % 60;
    diff = diff / 60;
    long min = diff % 60;
    diff = diff / 60;
    long hr = diff; 
    sStartupLogger.info("Total run time: {}{}{}", (hr > 0 ? hr + " hours, " : ""), (min > 0 ? min + " minutes, " : ""), String.format("%d.%03d seconds", sec, ms));
  }
}
