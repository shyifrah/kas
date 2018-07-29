package com.kas.infra.test;

import java.lang.reflect.Method;

public class MethodTestRun implements Runnable
{
  private Object    mObject;
  private Method    mMethod;
  private Object [] mArguments;
  private Object    mResult;
  private Exception mException = null;
  
  public MethodTestRun(Method method, Object object, Object ... args)
  {
    mMethod = method;
    mObject = object;
    mArguments = args;
  }
  
  public void run()
  {
    try
    {
      boolean accessible = mMethod.isAccessible();
      mMethod.setAccessible(true);
      mResult = mMethod.invoke(mObject, mArguments);
      mMethod.setAccessible(accessible);
    }
    catch (Exception e)
    {
      mException = e;
    }
  }
  
  public boolean isSuccessful()
  {
    return mException == null;
  }
  
  public Exception getException()
  {
    return mException;
  }
  
  public Object getResult()
  {
    return mResult;
  }
  
  public String toString()
  {
    return mMethod.toString();
  }
}
