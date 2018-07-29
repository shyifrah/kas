package com.kas.infra.test;

import com.kas.infra.base.IBaseLogger;

public class MethodTest
{
  private IBaseLogger   mLogger;
  private MethodTestRun mMethodRun;
  private Object        mExpectedResult;
  
  public MethodTest(IBaseLogger logger, Object exr, MethodTestRun run)
  {
    mLogger         = logger;
    mMethodRun      = run;
    mExpectedResult = exr;
  }
  
  public boolean test()
  {
    boolean succeeded = false;
    
    mLogger.trace("Testing: " + mMethodRun.toString());
    mMethodRun.run();
    if (!mMethodRun.isSuccessful())
    {
      mLogger.trace("Execution of method ended with exception: ", mMethodRun.getException());
    }
    else
    {
      Object result = mMethodRun.getResult();
      mLogger.trace("Execution of method ended successfully. Result: " + result);
      
      if ((mExpectedResult == null) && (result == null))
        succeeded = true;
      else if (mExpectedResult == null)
        succeeded = false;
      else if (mExpectedResult.equals(result))
        succeeded = true;
      else
        succeeded = false;
      
      if (succeeded)
        mLogger.trace("Method run ended with a value identical to the expected result. Test succeeded");
      else
        mLogger.trace("Method run ended with a value different from the expected result. Test failed");
    }
    return succeeded;
  }
}
