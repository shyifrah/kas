package com.kas.infra;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunner
{
  private String mProjectPath;
  private List<String> mClassesList = new ArrayList<String>();
  private Set<String> mRootSubDirList = new HashSet<String>();
  
  static public void main(String[] args)
  {
    if (args.length == 0)
    {
      System.out.println("Missing path");
      return;
    }
    
    if (args.length > 1)
    {
      System.out.println("Excessive argument: " + args[1]);
      return;
    }
    
    TestRunner runner = new TestRunner(args[0]);
    ClassLoader loader = runner.load();
    System.out.println("A total of " + runner.getClassCount() + " classes found in " + runner.getSubDirsCount() + " sub directories of " + runner.getProjectPath());
    
    runner.execute(loader);
  }
  
  private TestRunner(String path)
  {
    File f = new File(path);
    mProjectPath = f.getAbsolutePath();
  }
  
  private int getClassCount()
  {
    return mClassesList.size();
  }
  
  private int getSubDirsCount()
  {
    return mRootSubDirList.size();
  }
  
  private String getProjectPath()
  {
    return mProjectPath;
  }
  
  private ClassLoader load()
  {
    load(mProjectPath, mClassesList, mRootSubDirList);
    
    List<URL> urlList = new ArrayList<URL>();
    for (String subdir : mRootSubDirList)
    {
      File f = new File(subdir);
      try
      {
        URL url = f.toURI().toURL();
        urlList.add(url);
      }
      catch (MalformedURLException e) {}
    }
    URL [] urls = urlList.toArray(new URL[0]);
    return new URLClassLoader(urls);
  }

  private void load(String path, List<String> classList, Set<String> subdirList)
  {
    File directory = new File(path);
    File [] files = directory.listFiles();
    if (files != null)
    {
      for (File file : files)
      {
        if (file.isFile())
        {
          String name = file.getAbsolutePath();
          if (name.endsWith(".class"))
          {
            String rootSubDir =  getRootSubDir(name);
            subdirList.add(rootSubDir);
            
            String className = name.substring(rootSubDir.length()+1, name.lastIndexOf('.'));
            className = className.replace('\\', '.');
            classList.add(className);
          }
        }
        else if (file.isDirectory())
        {
          load(file.getAbsolutePath(), classList, subdirList);
        }
      }
    }
  }
  
  private String getRootSubDir(String path)
  {
    int startIndex = mProjectPath.length() + 1;
    int endIndex = path.indexOf(File.separator, startIndex);
    if (endIndex == -1)
      return null;
    
    String subdir = path.substring(startIndex, endIndex);
    return mProjectPath + File.separator + subdir;
  }
  
  private void execute(ClassLoader loader)
  {
    for (String clsname : mClassesList)
    {
      try
      {
        Class<?> cls = loader.loadClass(clsname);
        System.out.println("Loaded...:  [" + cls.getName() + "]");
        
        boolean shouldRunTest = false;
        Method [] declaredMethods = cls.getDeclaredMethods();
        for (Method mtd : declaredMethods)
        {
          if (mtd.isAnnotationPresent(Test.class))
          {
            shouldRunTest = true;
            break;
          }
        }
        
        if (shouldRunTest)
        {
          Result result = JUnitCore.runClasses(cls);
          
          for (Failure failure : result.getFailures())
             System.out.println("   " + failure.toString());
        
          System.out.println("   " + result.wasSuccessful() + "\n ");
        }
      }
      catch (ClassNotFoundException e)
      {
        System.out.println("Failed to load class [" + clsname + "]");
      }
    }
  }
}
