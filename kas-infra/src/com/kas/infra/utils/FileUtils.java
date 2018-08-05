package com.kas.infra.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility functions
 * 
 * @author Pippo
 */
public class FileUtils
{
  /**
   * Read file contents and return its lines as a {@link List} of {@link String}s.<br>
   * 
   * @param file The file to read
   * @return A {@link List} of strings. Each entry is a non-comment, non-blank line in the file
   * 
   * @see #load(File, char)
   */
  static public List<String> load(File file)
  {
    return load(file, '#');
  }
  
  /**
   * Read file contents and return its lines as a {@link List} of {@link String}s.<br>
   * <br>
   * Lines are first trimmed to omit all lines that consist of only blank spaces. Then, all lines
   * that begin with {@code comment} char are ignored.
   * 
   * @param file The file to read
   * @param comment The character that designates commented lines
   * @return A {@link List} of strings. Each entry is a non-comment, non-blank line in the file
   */
  static public List<String> load(File file, char comment)
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
  

  /**
   * Verify a file exists.<br>
   * <br>
   * If the file exists, {@code true} is returned.<br>
   * If the file doesn't exist, the method makes sure the directory exists - creating it if necessary,
   * then creates the file.
   * 
   * @param file The file to verify its existence
   * @return {@code true} if the file exists, {@code false} otherwise
   */
  static public boolean verifyExists(File file)
  {
    String path = file.getAbsolutePath();
    if (file.exists()) return true;
    
    String dirPath = path.substring(0, path.lastIndexOf(File.separator));
    File dir = new File(dirPath);
    if (!dir.exists())
    {
      dir.mkdirs();
    }
    
    boolean exists = false;
    try
    {
      file.createNewFile();
      exists = true;
    }
    catch (Throwable e) {}
    
    return exists;
  }
}
