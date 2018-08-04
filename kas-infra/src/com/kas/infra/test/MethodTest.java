package  com.kas.infra.test;

import com.kas.infra.base.AStatsCollector;
import com.kas.infra.base.IBaseLogger;
import com.kas.infra.base.Statistics;
import com.kas.infra.utils.StringUtils;

/**
 * A method test.
 * 
 * @see com.kas.infra.test.ObjectTest
 * @author Pippo
 */
public class MethodTest extends AStatsCollector
{
  static private final String cMethodTotalExecutions     = "MethodTest - method total executions";
  static private final String cMethodFailedExecutions    = "MethodTest - method failed executions";
  static private final String cMethodSucceededExecutions = "MethodTest - method succeeded executions";
  static private final String cMethodFailedTests         = "MethodTest - method failed tests";
  static private final String cMethodSucceededTests      = "MethodTest - method succeeded tests";
  
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
  MethodTest(Object exr, MethodRun run)
  {
    mMethodRun      = run;
    mExpectedResult = exr;
    mLogger         = new ConsoleLogger(this.getClass().getSimpleName());
    
    mStats = new Statistics();
    mStats.newCounter(cMethodTotalExecutions);
    mStats.newCounter(cMethodFailedExecutions);
    mStats.newCounter(cMethodSucceededExecutions);
    mStats.newCounter(cMethodFailedTests);
    mStats.newCounter(cMethodSucceededTests);
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
    mStats.increment(cMethodTotalExecutions);
    if (!mMethodRun.isSuccessful())
    {
      mStats.increment(cMethodFailedExecutions);
      mLogger.trace("Method execution ended abnormally. Exception: ", mMethodRun.getException());
    }
    else
    {
      mStats.increment(cMethodSucceededExecutions);
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
    boolean success;
    
    if ((mExpectedResult == null) && (result == null))
      success = true;
    else if ((mExpectedResult != null) && (mExpectedResult.equals(result)))
      success = true;
    else
      success = false;
    
    if (success)
      mStats.increment(cMethodSucceededTests);
    else
      mStats.increment(cMethodFailedTests);
    
    return success;
  }
  
  /**
   * Print collected statistics.
   */
  public void printStats()
  {
    mLogger.trace("   Method statistics for: [" + mMethodRun.toString() + "]");
    mLogger.trace("   " + StringUtils.trunc(cMethodTotalExecutions, 50, '.') + ':' + mStats.getValue(cMethodTotalExecutions));
    mLogger.trace("   " + StringUtils.trunc(cMethodSucceededExecutions, 50, '.') + ':' + mStats.getValue(cMethodSucceededExecutions));
    mLogger.trace("   " + StringUtils.trunc(cMethodFailedExecutions, 50, '.') + ':' + mStats.getValue(cMethodFailedExecutions));
    mLogger.trace("   " + StringUtils.trunc(cMethodSucceededTests, 50, '.') + ':' + mStats.getValue(cMethodSucceededTests));
    mLogger.trace("   " + StringUtils.trunc(cMethodFailedTests, 50, '.') + ':' + mStats.getValue(cMethodFailedTests)); 
  }
  
  /**
   * Get the object's detailed string representation.
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   * @see #toString()
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuilder sb = new StringBuilder();
    
    sb.append(name()).append("(\n")
      .append(pad).append("  ").append("Method=[").append(mMethodRun.toString()).append("]\n")
      .append(pad).append("  ").append("Expected=[").append(mExpectedResult.toString()).append("]\n")
      .append(pad).append(")");
    
    return sb.toString();
  }
}
