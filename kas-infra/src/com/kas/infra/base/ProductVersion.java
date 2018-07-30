package com.kas.infra.base;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Product Version.
 * 
 * @author Pippo
 */
public class ProductVersion extends AKasObject implements Serializable
{
  private static final long serialVersionUID = 1L;
  
  /**
   * The version consists of four integer values in the format: major-minor-modification-build 
   */
  private int    mMajorVersion;
  private int    mMinorVersion;
  private int    mModification;
  private int    mBuildNumber;
  
  /**
   * Construct a version based on a class file residing in KAS JAR file.
   * 
   * @param cls The class name to locate
   */
  public ProductVersion(Class<?> cls)
  {
    init();
    initVersionFromClass(cls);
  }
  
  /**
   * Initialize the version to zeroes
   * 
   */
  private void init()
  {
	  mMajorVersion = 0;
	  mMinorVersion = 0;
	  mModification = 0;
	  mBuildNumber  = 0;
  }
  
  /**
   * Initialize the version from the class file
   * 
   * @param cls The class name to locate
   */
  private void initVersionFromClass(Class<?> cls)
  {
    InputStream is = null;
    boolean success = false;
    try
    {
      Enumeration<URL> resEnum = cls.getClassLoader().getResources(JarFile.MANIFEST_NAME);
      while (resEnum.hasMoreElements())
      {
        URL url = resEnum.nextElement();
        is = url.openStream();
        if (is != null)
        {
          Manifest manifest = new Manifest(is);
          Attributes mainAttribs = manifest.getMainAttributes();
          String version = mainAttribs.getValue("Kas-Version");
          if (version != null)
          {
            String [] versionSegments = version.split("-");
            try
            {
              mMajorVersion = Integer.valueOf(versionSegments[0]);
              mMinorVersion = Integer.valueOf(versionSegments[1]);
              mModification = Integer.valueOf(versionSegments[2]);
              mBuildNumber  = Integer.valueOf(versionSegments[3]);
              success = true;
            }
            catch (NumberFormatException e) {}
            break;
          }
        }
      }
    }
    catch (Throwable e) {}
    finally
    {
      try
      {
        if (is != null) is.close();
      }
      catch (Throwable e) {}
    }
    
    if (!success) init();
  }
    
  /**
   * Get the major version
   * 
   * @return the major version
   */
  public int getMajorVersion()
  {
    return mMajorVersion;
  }
  
  /**
   * Get the minor version
   * 
   * @return the minor version
   */
  public int getMinorVersion()
  {
    return mMinorVersion;
  }
  
  /**
   * Get the modification level
   * 
   * @return the modification level
   */
  public int getModification()
  {
    return mModification;
  }
  
  /**
   * Get the build number
   * 
   * @return the build number
   */
  public int getBuildNumber()
  {
    return mBuildNumber;
  }
  
  /**
   * Get the string representation of the version.<br>
   * <br>
   * The string version is a version in the format of A-B-C-D, where:<br>
   *   A - the major version number
   *   B - the minor version number
   *   C - the modification level
   *   D - the build number
   * 
   * @return the version
   */
  public String toString()
  {
    return String.format("%d-%d-%d-%d", mMajorVersion, mMinorVersion, mModification, mBuildNumber);
  }
  
  /**
   * Get the object's detailed string representation. For {@code ProductVersion}, this method returns the same 
   * result as {@link #toString()}.
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   * @see #toString()
   */
  public String toPrintableString(int level)
  {
    return toString();
  }
}
