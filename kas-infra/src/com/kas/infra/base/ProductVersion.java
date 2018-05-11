package com.kas.infra.base;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class ProductVersion extends AKasObject implements Serializable
{
  /***************************************************************************************************************
   *  
   */
  private static final long serialVersionUID = 1L;
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private int    mMajorVersion;
  private int    mMinorVersion;
  private int    mModification;
  private int    mBuildNumber;
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public ProductVersion(Class<?> cls)
  {
    init();
    initVersionFromClass(cls);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private void init()
  {
	  mMajorVersion = 0;
	  mMinorVersion = 0;
	  mModification = 0;
	  mBuildNumber  = 0;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
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
    
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public int getMajorVersion()
  {
    return mMajorVersion;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public int getMinorVersion()
  {
    return mMinorVersion;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public int getModification()
  {
    return mModification;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public int getBuildNumber()
  {
    return mBuildNumber;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String toString()
  {
    return String.format("%d-%d-%d-%d", mMajorVersion, mMinorVersion, mModification, mBuildNumber);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String toPrintableString(int level)
  {
    return toString();
  }
}
