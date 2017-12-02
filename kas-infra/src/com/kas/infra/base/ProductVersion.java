package com.kas.infra.base;

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class ProductVersion extends AKasObject
{
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private int    mMajorVersion = 0;
  private int    mMinorVersion = 0;
  private int    mModification = 0;
  private int    mBuildNumber  = 0;
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public ProductVersion(Class<?> cls)
  {
    initVersionFromClass(cls);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private void initVersionFromClass(Class<?> cls)
  {
    InputStream is = null;
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
            initVersionFromString(version);
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
  }
    
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private void initVersionFromString(String version) throws IllegalArgumentException
  {
    String [] versionSegments = version.split("-");
    try
    {
      mMajorVersion = Integer.valueOf(versionSegments[0]);
      mMinorVersion = Integer.valueOf(versionSegments[1]);
      mModification = Integer.valueOf(versionSegments[2]);
      mBuildNumber  = Integer.valueOf(versionSegments[3]);
    }
    catch (NumberFormatException e)
    {
      throw new IllegalArgumentException(e);
    }
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
