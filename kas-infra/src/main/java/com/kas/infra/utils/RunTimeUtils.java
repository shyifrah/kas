package com.kas.infra.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import com.kas.infra.utils.helpers.EOsType;

/**
 * A list of utility methods used to extract run-time information
 * 
 * @author Pippo
 */
public class RunTimeUtils
{
  static public  final String cProductHomeDirProperty = "kas.home";
  static public  final String cOsNameProperty         = "os.name";
  static private final String cUserNameProperty       = "user.name";
  
  /**
   * The product home directory
   */
  static private String sProductHomeDir = null;
  static private int    sProcessId = -1;
  
  /**
   * Get the current host name
   * We first try and obtain the host name by means of {@link InetAddress#getLocalHost()}. If that doesn't work
   * we execute the {@code hostname} system command and read its output. 
   * 
   * @return
   *   the host name
   */
  static public String getHostName()
  {
    String host = null;
    try
    {
      host = InetAddress.getLocalHost().getHostName();
    }
    catch (Throwable e) {}
    
    if (host == null)
    {
      try
      {
        Process p = Runtime.getRuntime().exec("hostname");
        if (p != null)
        {
          InputStream is = p.getInputStream();
          InputStreamReader isr = new InputStreamReader(is);
          BufferedReader reader = new BufferedReader(isr);
          String line = reader.readLine();
          if (line != null)
          {
            host = line;
          }
        }
      }
      catch (Throwable e) {}
    }
    return host;
  }
  
  /**
   * Get the process ID.<br>
   * <br>
   * This method is called upon static initialization to be used by {@link #getProcessId()}<br>
   * <br>
   * NOTE: Since there are about a million ways that the method might now work as expected,
   * the {@code name} variable is initialized to a string that will produce a process ID of 0. 
   * 
   * @return
   *   the ID of the current Process
   */
  static private int initProcessId()
  {
    RuntimeMXBean rtmx = ManagementFactory.getRuntimeMXBean();
    if (rtmx == null)
      return 0;
    
    String name = rtmx.getName();
    if (name == null)
      return 0;
    
    if (name.indexOf('@') == -1)
      return 0;
    
    String str = name.split("@")[0];
    int pid = 0;
    try
    {
      pid = Integer.valueOf(str);
    }
    catch (NumberFormatException e) {}
    
    return pid;
  }
  
  /**
   * Get the current Process ID
   * 
   * @return
   *   the ID of the current Process
   */
  static public int getProcessId()
  {
    if (sProcessId == -1)
    {
      sProcessId = initProcessId();
    }
    return sProcessId;
  }
  
  /**
   * Get the current Thread ID
   * 
   * @return
   *   the ID of the current Thread
   */
  static public long getThreadId()
  {
    return Thread.currentThread().getId();
  }
  
  /**
   * Get the current user name
   * 
   * @return
   *   the name of the current user
   */
  static public String getUserId()
  {
    return getProperty(cUserNameProperty);
  }
  
  /**
   * Get the type of the Operating System
   * 
   * @return
   *   one of the Enum values defined in {@link EOsType}
   */
  static public EOsType getOsType()
  {
    String osn = getOsName().toLowerCase();
    
    if (osn.indexOf("win") >= 0)
      return EOsType.cWindows;
    if (osn.indexOf("mac") >= 0)
      return EOsType.cMacOs;
    if (osn.indexOf("nix") >= 0 || osn.indexOf("nux") >= 0 || osn.indexOf("aix") >= 0)
      return EOsType.cUnix;
    if (osn.indexOf("sunos") >= 0)
      return EOsType.cSolaris;
    return EOsType.cUnknown;
  }
  
  /**
   * Get the Operating System name
   * 
   * @return
   *   the Operating System name
   */
  static public String getOsName()
  {
    return System.getProperty(cOsNameProperty);
  }
  
  /**
   * Get the value of a specified property or environment variable
   * 
   * @param name
   *   the system property or environment variable name
   * @return
   *   the value of the system property or environment variable, or {@code null}
   */
  static public String getProperty(String name)
  {
    return getProperty(name, null);
  }
  
  /**
   * Get the value of a specified property or environment variable
   * <br>
   * If a value was found, return it, otherwise, replace the period (".") chars
   * with underscores ("_") and try obtain the value of an environment variable
   * with the new name. If no value was obtained, the {@code defaultValue} is returned.
   * 
   * @param name
   *   The system property or environment variable name
   * @param defaultValue
   *   The default value to be returned if no value was obtained
   * @return
   *   the value of the {@code variable} property or {@code defaultValue}
   */
  static public String getProperty(String name, String defaultValue)
  {
    // try obtaining it from system variable ("-D")
    String result = System.getProperty(name);
    if ((result != null) && (result.trim().length() > 0))
    {
      return result;
    }
    
    // try obtaining it from environment variable
    String envvar = name.replace('.', '_');
    result = System.getenv(envvar);
    if ((result != null) && (result.trim().length() > 0))
    {
      return result;
    }
    
    return defaultValue;
  }
  
  /**
   * Sets a new system property<br>
   * <br>
   * If a variable named {@code name} already exists, then the value of {@code force}
   * determines how to process the request. If it's {@code true}, the new value will
   * override the old one. If it's {@code false}, the old value will be retained.
   * 
   * @param variable
   *   The system property name
   * @param value
   *   The value to assign to the property
   * @param force
   *   Indicator whether to override old value or retain it
   * @return
   *   the value of the system property {@code variable}
   */
  static public String setProperty(String variable, String value, boolean force)
  {
    // try obtaining it from system variable ("-D")
    String result = System.getProperty(variable);
    if ((result == null) || (result.trim().length() == 0))
    {
      String envvar = variable.replace('.', '_');
      result = System.getenv(envvar);
    }
    
    // property doesn't exist
    if ((result == null) || (result.trim().length() == 0) || (force))
    {
      result = System.setProperty(variable, value);
      if (result == null) result = value;
    }
    
    return result;
  }
  
  /**
   * Get the home directory from kas.home property and obtain its absolute path.<br>
   * <br>
   * This method is called upon static initialization to be used by {@link #getProductHomeDir()}
   * 
   * @return
   *   the KAS home directory
   */
  static private String initProductHomeDir()
  {
    String path = getProperty(cProductHomeDirProperty, ".");
    File currdir = new File(path);
    if ((currdir.exists()) && (currdir.isDirectory()) && (currdir.canRead()))
    {
      path = currdir.getAbsolutePath();
    }
    return path;
  }
  
  /**
   * Return the KAS home directory
   * 
   * @return
   *   the KAS home directory
   */
  static public String getProductHomeDir()
  {
    if (sProductHomeDir == null)
    {
      sProductHomeDir = initProductHomeDir();
    }
    return sProductHomeDir;
  }
  
  /**
   * Suspend current Thread execution for {@code seconds} seconds.<br>
   * If the number of seconds is lower or equal than 0, this method does nothing.
   * 
   * @param seconds
   *   The number of seconds to delay the current Thread
   */
  static public void sleepForSeconds(int seconds)
  {
    if (seconds > 0)
    {
      try
      {
        Thread.sleep((long)(seconds * 1000));
      }
      catch (Throwable e) {}
    }
  }
  
  /**
   * Suspend current Thread execution for {@code milliseconds} milliseconds.<br>
   * If the number of milliseconds is lower or equal than 0, this method does nothing.
   * 
   * @param milliseconds
   *   The number of milliseconds to delay the current Thread
   */
  static public void sleepForMilliSeconds(long milliseconds)
  {
    if (milliseconds > 0)
    {
      try
      {
        Thread.sleep(milliseconds);
      }
      catch (Throwable e) {}
    }
  }
}
