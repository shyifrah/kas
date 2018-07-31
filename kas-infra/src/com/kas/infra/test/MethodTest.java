package  com.kas.infra.test;

import com.kas.infra.base.IBaseLogger;

/**
 * A method test.
 * 
 * @see com.kas.infra.test.ObjectTest
 * @author Pippo
 *
 */
public class MethodTest
{
  /**
   * A Logger
   */
  private IBaseLogger mLogger;
  
  /**
   * Method execution object
   */
  private MethodRun mMethodRun;
  
  /**
   * The expected result from the method execution
   */
  private Object mExpectedResult;
  
  /**
   * Create a method test.<br>
   * 
   * @param exr The expected result. A value of {@code null} indicates either the method returns a return value 
   * of type {@code void} or the expected value is {@code null}.
   * @param run The method execution object.
   */
  public MethodTest(Object exr, MethodRun run)
  {
    mMethodRun      = run;
    mExpectedResult = exr;
    mLogger         = new ConsoleLogger(this.getClass().getSimpleName());
  }
  
  /**
   * Test the result of the method execution.<br>
   * <br>
   * First we check if the method execution ended normally (that is, without an exception) and then we check
   * to see if the result the method produced is as expected.
   * 
   * @return {@code true} if method produced the expected result, {@code false} otherwise.
   */
  public boolean test()
  {
    mLogger.trace("Starting test for: " + mMethodRun.toString());
    
    boolean succeeded = false;
    
    mMethodRun.run();
    if (!mMethodRun.isSuccessful())
    {
      mLogger.trace("Method execution ended abnormally. Exception: ", mMethodRun.getException());
    }
    else
    {
      Object result = mMethodRun.getResult();
      succeeded = analyze(result);
    }
    return succeeded;
  }
  
  /**
   * Analyze the result the method produced.<br>
   * <br>
   * If both expected result and actual result are {@code null}, then the method execution ended successfully.<br>
   * If the expected result is non-{@code null}, and it equals to the actual result, then the method execution ended successfully.<br>
   * In all other cases, the method execution is considered to fail execution.<br>
   * 
   * @return {@code true} if method produced the expected result, {@code false} otherwise.
   */
  private boolean analyze(Object result)
  {
    mLogger.trace("Method execution ended normally. Analyzing: ");
    mLogger.trace("    Actual result......: " + (result == null ? "null" : result.toString()));
    mLogger.trace("    Expected result....: " + (mExpectedResult == null ? "null" : mExpectedResult.toString()));
    
    boolean success;
    
    if ((mExpectedResult == null) && (result == null))
      success = true;
    else if ((mExpectedResult != null) && (mExpectedResult.equals(result)))
      success = true;
    else
      success = false;
    
    mLogger.trace("    Expected and Actual results " + (success ? "match" : "do not match"));
    return success;
  }
}
