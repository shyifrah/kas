package com.kas.infra.test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import com.kas.infra.base.IBaseLogger;
import com.kas.infra.base.TimeStamp;
import com.kas.infra.utils.StringUtils;

public class TestSet
{
  private IBaseLogger mLogger;
  private String      mTitle;
  private TimeStamp   mStartTimestamp = null;
  private TimeStamp   mEndTimestamp = null;
  private Object      mTestedObject;
  private List<MethodTest> mTests = new ArrayList<MethodTest>();
  
  public TestSet(IBaseLogger logger, String title, Object object)
  {
    mLogger = logger;
    mTitle  = title;
    mTestedObject = object;
  }
  
  public void add(String name, Object exr, Object ... args)
  {
    Class<?> [] classes = null;
    if ((args != null) && (args.length > 0))
    {
      classes = new Class<?> [args.length];
      for (int i = 0; i < args.length; ++i)
      {
        /* 
         * special handling for primitives: byte, short, int, long, float, double, boolean, char
         */
        classes[i] = args[i].getClass();
        if (classes[i].equals(java.lang.Byte.class))
          classes[i] = byte.class;
        else if (classes[i].equals(java.lang.Short.class))
          classes[i] = short.class;
        else if (classes[i].equals(java.lang.Integer.class))
          classes[i] = int.class;
        else if (classes[i].equals(java.lang.Long.class))
          classes[i] = long.class;
        else if (classes[i].equals(java.lang.Float.class))
          classes[i] = float.class;
        else if (classes[i].equals(java.lang.Double.class))
          classes[i] = double.class;
        else if (classes[i].equals(java.lang.Boolean.class))
          classes[i] = boolean.class;
        else if (classes[i].equals(java.lang.Character.class))
          classes[i] = char.class;
      }
    }
    
    try
    {
      Method method = mTestedObject.getClass().getDeclaredMethod(name, classes);
      MethodTest test = new MethodTest(mLogger, exr, new MethodTestRun(method, mTestedObject, args));
      mTests.add(test);
      mLogger.trace("Added test for method '" + name + "' with specified arguments");
    }
    catch (Exception e)
    {
      mLogger.trace("Failed to retrieve method '" + name + "' with specified arguments. Exception: " + e.getMessage());
    }
  }
  
  public void run()
  {
    start();
    for (MethodTest test : mTests)
    {
      test.test();
    }
    end();
  }
  
  private void start()
  {
    mStartTimestamp = new TimeStamp();
    String msg = StringUtils.title("Test set " + mTitle);
    mLogger.trace('\n' + msg);
  }
  
  private void end()
  {
    mEndTimestamp = new TimeStamp();
  }
}
