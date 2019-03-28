package com.kas.config.impl;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kas.infra.base.IObject;
import com.kas.infra.base.Properties;
import com.kas.infra.base.PropertyResolver;
import com.kas.infra.typedef.StringList;
import com.kas.infra.utils.FileUtils;
import com.kas.infra.utils.StringUtils;

/**
 * KAS {@link ConfigProperties} class has the ability to load from a file and interpret
 * "include" statements. An "include" statement is one that tells the object to read
 * additional properties from another file.<br>
 * <br>
 * The list of files composing this {@link Properties} object is held in {@code mConfigFiles}
 * 
 * @author Pippo
 */
public class ConfigProperties extends Properties
{
  static public  final String cIncludeKey      = "kas.include";
  static private final long   serialVersionUID = 1L;
  
  static private Logger sLogger = LogManager.getLogger(ConfigProperties.class);
  
  /**
   * The list of fully-pathed file names that compose this set of {@link ConfigProperties} 
   */
  private StringList mConfigFiles = new StringList();
  
  /**
   * Construct an empty set of {@link ConfigProperties}
   */
  public ConfigProperties()
  {
    super();
  }
  
  /**
   * Construct a set of {@link ConfigProperties} from a map.<br>
   * <br>
   * After construction, this {@link ConfigProperties} object will have the same entries as {@code map}.
   * 
   * @param map
   *   The map.
   */
  public ConfigProperties(Map<?, ?> other)
  {
    super(other);
  }
  
  /**
   * Constructs a set of {@link ConfigProperties} object from {@link ObjectInputStream}
   * 
   * @param istream
   *   The {@link ObjectInputStream}
   * @throws IOException
   *   If I/O error occurs
   */
  public ConfigProperties(ObjectInputStream istream) throws IOException
  {
    super(istream);
  }
  
  /**
   * Get the list of config files
   * 
   * @return
   *   the list of config files
   */
  public StringList getConfigFiles()
  {
    return mConfigFiles;
  }

  /**
   * Load the properties defined in file {@code fileName} into this {@link ConfigProperties} object.<br>
   * <br>
   * This method is used by outside callers to allow properties refresh.  
   * 
   * @param fileName
   *   The fully-pathed name of the file to be loaded
   */
  public void load(String fileName)
  {
    load(fileName, this);
  }
  
  /**
   * Load the properties defined in file {@code fileName} into {@code properties}.<br>
   * <br>
   * Lines in {@code fileName} are processed one at a time. Comments are ignored and lines are checked
   * to have valid syntax. That is, non-comment lines should have a format of key=value.<br>
   * When {@code include} statement is encountered, it actually points to a new file that should be loaded as well.  
   * 
   * @param fileName
   *   The fully-pathed name of the file to be loaded
   * @param properties
   *   The {@link ConfigProperties} object into which keys will be mapped to values.
   */
  private void load(String fileName, ConfigProperties properties)
  {
    sLogger.trace("ConfigProperties::load() - IN, File={}", fileName);
    
    // now try to load the properties inside this file
    File file = new File(fileName);
    List<String> input = null;
    if ((file.exists()) && (file.isFile()) && (file.canRead()))
    {
      input = FileUtils.load(file);
      
      for (int i = 0; i < input.size(); ++i)
      {
        String [] parsedLine = input.get(i).split("=");
        
        if (parsedLine.length == 2)
        {
          String key = parsedLine[0].trim();
          String val = parsedLine[1].trim();
          String actualVal = PropertyResolver.resolve(val, this);

          if (key.equalsIgnoreCase(cIncludeKey))           // if we encounter an "include" statement - load the new file
          {
            load(actualVal, properties);
          }
          else
          {
            properties.put(key, actualVal);
          }
        }
      }
      
      mConfigFiles.add(fileName);
    }
    
    sLogger.trace("ConfigProperties::load() - OUT");
  }
  
  /**
   * Returns the {@link IObject} simple class name enclosed with chevrons.
   * 
   * @return
   *   class name enclosed with chevrons.
   */
  public String name()
  {
    return StringUtils.getClassName(getClass());
  }
  
  /**
   * Returns the {@link IObject} string representation.
   * 
   * @param level
   *   The required padding level
   * @return
   *   the string representation with the specified level of padding
   */
  public String toPrintableString(int level)
  {
    String pad = StringUtils.getPadding(level);
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n")
      .append(StringUtils.asPrintableString(this, level + 1)).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
