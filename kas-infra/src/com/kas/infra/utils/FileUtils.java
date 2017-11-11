package com.kas.infra.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils
{
  //----------------------------------------------------------------------------------------------------
  //
  //----------------------------------------------------------------------------------------------------
  public static List<String> load(File file)
  {
    return load(file, '#');
  }
  
  //----------------------------------------------------------------------------------------------------
  //
  //----------------------------------------------------------------------------------------------------
  public static List<String> load(File file, char comment)
  {
    List<String> result = new ArrayList<String>();
    try
    {
      BufferedReader br = new BufferedReader(new FileReader(file));
      
      try
      {
        String line = null;
        while (( line = br.readLine()) != null)
        {
          line = line.trim();
          if ((line.length() > 0) && (line.charAt(0) != comment))
            result.add(line);
        }
      }
      finally
      {
        br.close();
      }
    }
    catch (IOException e) {}
    
    return result;
  }
  
  //----------------------------------------------------------------------------------------------------
  //
  //----------------------------------------------------------------------------------------------------
  public static boolean verifyExists(File file)
  {
    boolean exists = false;
    String path = file.getAbsolutePath();
    System.out.println("File full path: " + path);
    if (file.exists())
    {
      exists = true;
    }
    else
    {
      String dirPath = path.substring(0, path.lastIndexOf(File.separator));
      File dir = new File(dirPath);
      if (!dir.exists())
      {
        dir.mkdirs();
      }
      
      try
      {
        file.createNewFile();
        exists = true;
      }
      catch (Throwable e) {}
    }
    return exists;
  }
}
