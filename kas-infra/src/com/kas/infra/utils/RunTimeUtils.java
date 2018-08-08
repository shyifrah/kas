package com.kas.infra.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;

/**
 * Utility functions
 * 
 * @author Pippo
 */
public class RunTimeUtils
{
  static public  final String cProductHomeDirProperty = "kas.home";
  static private final String cUserNameProperty       = "user.name";
  
  /**
   * The product home directory
   */
  static private String sProductHomeDir = initProductHomeDir();
  
  /**
   * Get the current host name
   * We first try and obtain the host name by means of {@link InetAddress#getLocalHost()}. If that doesn't work
   * we execute the {@code hostname} system command and read its output. 
   * 
   * @return the host name
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
   * Get the current Process ID
   * 
   * @return the ID of the current Process
   */
  static public int getProcessId()
  {
    // may not work on all JVMs
    String name = ManagementFactory.getRuntimeMXBean().getName(); //742912@localhost
    String [] tokens = name.split("@");
    return Integer.valueOf(tokens[0]);
  }
  
  /**
   * Get the current Thread ID
   * 
   * @return the ID of the current Thread
   */
  static public long getThreadId()
  {
    return Thread.currentThread().getId();
  }
  
  /**
   * Get the current user name
   * 
   * @return the name of the current user
   */
  static public String getUserId()
  {
    return getProperty(cUserNameProperty);
  }
  
  /**
   * Get the value of the property from System properties.
   * 
   * @param variable the system property name
   * @return the value of the {@code variable} property or {@code defaultValue}
   * 
   * @see #getProperty(String, String)
   */
  static public String getProperty(String variable)
  {
    return getProperty(variable, null);
  }
  
  /**
   * Get the value of the property from System properties.<br>
   * <br>
   * If a value was found, return it, otherwise, replace the period (".") chars with underscores ("_") and
   * try obtain the value of an environment variable with the new name. If no value was obtained, the 
   * {@code defaultValue} is returned
   * 
   * @param variable The system property name
   * @param defaultValue The default value to be returned if no value was obtained
   * @return the value of the {@code variable} property or {@code defaultValue}
   */
  static public String getProperty(String variable, String defaultValue)
  {
    // try obtaining it from system variable ("-D")
    String result = System.getProperty(variable);
    if ((result != null) && (result.trim().length() > 0))
    {
      return result;
    }
    
    // try obtaining it from environment variable
    String envvar = variable.replace('.', '_');
    result = System.getenv(envvar);
    if ((result != null) && (result.trim().length() > 0))
    {
      return result;
    }
    
    return defaultValue;
  }
  
  /**
   * Set the value {@code value} of the property named {@code variable} to System properties.<br>
   * <br>
   * If a variable with the name {@code variable} already exists, then the value of {@code force} determines
   * how to process the request. If it's {@code true}, the new value will override the old one. If it's
   * {@code false}, the old value will be retained.
   * 
   * @param variable The system property name
   * @param value The value to assign to the property
   * @param force Indicator whether to override old value or retain it
   * @return the value of the system property {@code variable}
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
   * @return the KAS home directory
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
   * @return the KAS home directory
   */
  static public String getProductHomeDir()
  {
    return sProductHomeDir;
  }
  
  /**
   * Suspend current Thread execution for {@code seconds} seconds.<br>
   * If the number of seconds is lower or equal than 0, this method does nothing.
   * 
   * @param seconds The number of seconds to delay the current Thread
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
   * @param milliseconds The number of milliseconds to delay the current Thread
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
