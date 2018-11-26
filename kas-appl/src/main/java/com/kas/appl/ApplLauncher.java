package com.kas.appl;

import java.lang.reflect.Constructor;
import java.util.Map;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.ConsoleLogger;
import com.kas.infra.base.TimeStamp;
import com.kas.infra.logging.IBaseLogger;
import com.kas.infra.utils.ConsoleUtils;
import com.kas.infra.utils.RunTimeUtils;

/**
 * A launcher that uses to execute KAS applications
 * 
 * @author Pippo
 */
public class ApplLauncher extends AKasObject
{
  static private final String cKasHomeSystemProperty = "kas.home";
  
  static private IBaseLogger sLogger = new ConsoleLogger(KasApplLauncher.class.getName());
  
  /**
   * Application class name
   */
  private String mApplClass;
  
  /**
   * Application arguments
   */
  private Map<String, String> mApplArgs;
  
  /**
   * Construct the launcher
   * 
   * @param applClass The application class
   * @param applArgs The application arguments
   */
  public ApplLauncher(String applClass, Map<String, String> applArgs)
  {
    mApplClass = applClass;
    mApplArgs  = applArgs;
  }
  
  /**
   * Run the application
   */
  public void run()
  {
    TimeStamp start = TimeStamp.now();
    sLogger.info(ConsoleUtils.GREEN + "KAS launcher started at " + start.toString() + ConsoleUtils.RESET);
    
    String kasHome = mApplArgs.get(cKasHomeSystemProperty);
    RunTimeUtils.setProperty(cKasHomeSystemProperty, kasHome, true);
    sLogger.info("KAS launcher set system property '" + cKasHomeSystemProperty + "' to '" + kasHome + "'");
    
    launch();
    
    TimeStamp end = TimeStamp.now();
    sLogger.info(ConsoleUtils.GREEN + "KAS launcher ended at " + end.toString() + ConsoleUtils.RESET);
    
    reportRunTime(start, end);
  }
  
  /**
   * Launch the application
   * 
   * @param className The name of the class to be loaded
   */
  private void launch()
  {
    AKasAppl app = null;
    boolean init = false;
    try
    {
      Class<?> cls = Class.forName(mApplClass);
      Constructor<?> ctor = cls.getConstructor(Map.class);
      Object instance = ctor.newInstance(mApplArgs);
      if (!(instance instanceof AKasAppl))
      {
        sLogger.error("KAS application not an instance of basic application class: " + AKasAppl.class.getName());
      }
      else
      {
        app = (AKasAppl)instance;
        init = app.init();
        if (!init)
        {
          sLogger.error(ConsoleUtils.RED + "KAS application failed initialization. See previous error messages. Terminating..." + ConsoleUtils.RESET);
        }
        else
        {
          app.run();
        }
      }
    }
    catch (ClassNotFoundException e)
    {
      sLogger.fatal(ConsoleUtils.RED + "KAS launcher failed to locate application class: " + mApplClass + ConsoleUtils.RESET);
    }
    catch (NoSuchMethodException e)
    {
      sLogger.fatal(ConsoleUtils.RED + "KAS launcher failed to locate appropriate constructor for class: " + mApplClass + ConsoleUtils.RESET);
    }
    catch (Exception e)
    {
      sLogger.fatal(ConsoleUtils.RED + "Launch failed. Exception encountered: " + ConsoleUtils.RESET, e);
    }
    finally
    {
      try
      {
        app.term();
      }
      catch (Throwable e) {}
    }
  }
  
  /**
   * Report run time
   * 
   * @param start The {@link TimeStamp timestamp} of the time the launcher started
   * @param end The {@link TimeStamp timestamp} of the time the launcher ended
   */
  private void reportRunTime(TimeStamp start, TimeStamp end)
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
   * Get the object's detailed string representation
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n")
      .append(pad).append("  Class=").append(mApplClass).append("\n");
    sb.append(pad).append(")\n");
    return sb.toString();
  }
}
