package com.kas.appl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import com.kas.infra.base.TimeStamp;
import com.kas.infra.utils.ConsoleUtils;
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
  
  /**
   * Main entry point for all projects
   * 
   * @param args
   *   Map of string arguments passed from outside
   */
  static public void main(String [] args)
  {
    Map<String, String> argsMap = getAndProcessStartupArguments(args);
    String kasAppClassName = argsMap.get(IAppConstants.cKasExecutableClass);
    if (kasAppClassName == null)
    {
      ConsoleUtils.writelnRed("No application was specified. exit");
      return;
    }
    
    String [] temp = kasAppClassName.split("\\.");
    String log4jLogFileName = temp[temp.length-1].toLowerCase();
    
    String kasHome = RunTimeUtils.getProperty(cKasHomeSystemProperty, ".");
    String log4jConfigFile = kasHome + "/conf/log4j2.xml";
    String log4jLogDirectory = kasHome + "/logs";
    
    RunTimeUtils.setProperty(IAppConstants.cLog4jConfigFileProperty, log4jConfigFile, true);
    RunTimeUtils.setProperty(IAppConstants.cLog4jLogDirectoryProperty, log4jLogDirectory, true);
    RunTimeUtils.setProperty(IAppConstants.cLog4jLogFileProperty, log4jLogFileName, true);
    
    AKasApp app = null;
    
    try
    {
      Class<?> kasAppClass = Class.forName(kasAppClassName);
      Constructor<?> ctor = kasAppClass.getConstructor(Map.class);
      app = (AKasApp)ctor.newInstance(argsMap);
    }
    catch (ClassNotFoundException e)
    {
      ConsoleUtils.writelnRed("Class not found: Class=[%s]. exit", kasAppClassName);
      return;
    }
    catch (NoSuchMethodException | ClassCastException e)
    {
      ConsoleUtils.writelnRed("Class [%s] not a valid KAS application. exit", kasAppClassName);
      return;
    }
    catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
    {
      ConsoleUtils.writelnRed("Failed to construct object of type class=[%s]. exit", kasAppClassName);
      return;
    }
    
    launch(app);
  }
  
  /**
   * Launch the application
   * 
   * @param app
   *   Application to launch
   */
  static public void launch(AKasApp app)
  {
    ConsoleUtils.writeln("Launching application " + app.getAppName());
    
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
      ConsoleUtils.writelnRed("Exception caught: ", e);
    }
    finally
    {
      if (!app.isTerminated())
        app.term();
      end = TimeStamp.now();
      ConsoleUtils.writeln("Application %s ended", app.getAppName());
      reportRunTime(start, end);
    }
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
      ConsoleUtils.writeln("No arguments passed to KAS launcher, continue...");
    }
    else
    {
      for (String arg : args)
      {
        ConsoleUtils.writeln("Processing argument: [%s]", arg);
        String [] keyValue = arg.split("=");
        if (keyValue.length > 2)
        {
          ConsoleUtils.writelnRed("Invalid argument: '%s'. Argument should be in key=value format. Ignoring...", arg);
        }
        else if (keyValue.length <= 1)
        {
          ConsoleUtils.writelnRed("Invalid argument: '%s'. Argument should be in key=value format. Ignoring...", arg);
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
    ConsoleUtils.writeln("Total run time: %s%s%d.%03d seconds", (hr > 0 ? hr + " hours, " : ""), (min > 0 ? min + " minutes, " : ""), sec, ms);
  }
}
