package com.kas.infra.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import com.kas.infra.base.Constants;

public class RunTimeUtils
{ 
  private static String sProductHomeDir = initProductHomeDir();
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public static String getHostName()
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
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public static int getProcessId()
  {
    // may not work on all JVMs
    String name = ManagementFactory.getRuntimeMXBean().getName(); //742912@localhost
    String [] tokens = name.split("@");
    return Integer.valueOf(tokens[0]);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public static long getThreadId()
  {
    return Thread.currentThread().getId();
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public static String getUserId()
  {
    return getProperty(Constants.cUserNameProperty);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public static String getProperty(String variable)
  {
    return getProperty(variable, null);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public static String getProperty(String variable, String defaultValue)
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
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private static String initProductHomeDir()
  {
    String path = getProperty(Constants.cProductHomeDirProperty, ".");
    
    // last resort - assuming current directory
    File currdir = new File(path);
    path = currdir.getAbsolutePath();
    return path;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public static String getProductHomeDir()
  {
    return sProductHomeDir;
  }
}
