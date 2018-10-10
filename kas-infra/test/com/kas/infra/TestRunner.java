package com.kas.infra;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class TestRunner
{
  private String mPath;
  private List<String> mClassFilesList;
  private List<String> mClassesList;
  
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
    runner.load();
    System.out.println("A total of " + runner.getCount() + " classes found: ");
    runner.print();
    
    runner.execute();
  }
  
  private TestRunner(String path)
  {
    mPath = path;
  }
  
  private int getCount()
  {
    return mClassFilesList.size();
  }
  
  private void load() throws MalformedURLException
  {
    mClassFilesList = new ArrayList<String>();
    mClassesList = new ArrayList<String>();
    String classdir;
    File f;
    List<URL> urlList = new ArrayList<URL>();
    
    classdir = mPath + File.separator + "bin";
    loadClassFilesList(classdir, mClassFilesList);
    loadClassesList(classdir);
    f = new File(classdir);
    urlList.add(f.toURI().toURL());
    
    classdir = mPath + File.separator + "tbin";
    loadClassFilesList(classdir, mClassFilesList);
    loadClassesList(classdir);
    f = new File(classdir);
    urlList.add(f.toURI().toURL());
    
    URL [] urls = urlList.toArray(new URL[0]);
    URLClassLoader cl = new URLClassLoader(urls);
  }
  
  private void loadClassFilesList(String path, List<String> classes)
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
            classes.add(name);
        }
        else if (file.isDirectory())
        {
          loadClassFilesList(file.getAbsolutePath(), classes);
        }
      }
    }
  }
  
  private void loadClassesList(String classdir)
  {
    for (String cf : mClassFilesList)
    {
      if (cf.startsWith(classdir))
      {
        String fcn = cf.substring(classdir.length()+1, cf.lastIndexOf('.'));
        fcn = fcn.replace('\\', '.');
        mClassesList.add(fcn);
      }
    }
  }
  
  private void print()
  {
    for (int i = 0; i < mClassFilesList.size(); ++i)
    {
      String classFile = mClassFilesList.get(i);
      String className = mClassesList.get(i);
      System.out.println("    " + classFile);
      System.out.println("      " + className);
    }
  }
  
  private Class<?> getClass(String file)
  {
    Class<?> result = null;
    String classFullPath = file.substring(0, file.lastIndexOf('.'));
    
    try
    {
      result = Class.forName(classFullPath);
    }
    catch (ClassNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (Throwable e)
    {
      e.printStackTrace();
    }
    
    return result;
  }
  
  private void execute()
  {
    for (String str : mClassFilesList)
    {
      Class<?> cls = getClass(str);
      System.out.println("        " + cls.getSimpleName()); 
    }
  }
}
