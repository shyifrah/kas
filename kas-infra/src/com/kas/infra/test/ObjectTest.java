package com.kas.infra.test;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.kas.infra.base.AStatsCollector;
import com.kas.infra.base.ConsoleLogger;
import com.kas.infra.base.Statistics;
import com.kas.infra.base.TimeStamp;
import com.kas.infra.logging.IBaseLogger;
import com.kas.infra.utils.StringUtils;

/**
 * An {@link ObjectTest} is a collection of tests that are executed against a specified object.<br>
 * Tests are scheduled for execution via the {@link #add(String, Object, Object...)} method, and are executed via the {@link #run()} method.
 */
public class ObjectTest extends AStatsCollector
{
  /**
   * Counters
   */
  static private final String cMethodTestAdded  = "Method test added";
  static private final String cMethodNotFound   = "Method not found";
  static private final String cMethodFound      = "Method found";
  
  static private final String cMethodExecuted      = "Method executed";
  static private final String cMethodExecFailed    = "Method execution failed";
  static private final String cMethodExecSucceeded = "Method execution succeeded";
  static private final String cMethodTestFailed    = "Method test failed";
  static private final String cMethodTestSucceeded = "Method test succeeded";
  
  /**
   * A Logger
   */
  private IBaseLogger mLogger;
  
  /**
   * Start timestamp. This is the time the {@link run()} method received control.
   */
  private TimeStamp mStartTimestamp = null;
  
  /**
   * End timestamp. This is the time the {@link run()} method ended tests execution.
   */
  private TimeStamp mEndTimestamp = null;
  
  /**
   * The object against which the tests are executed.
   */
  private Object mTestedObject;
  
  /**
   * A map of method executions vs the expected result
   */
  private Map<MethodExec, Object> mMethodTests = new ConcurrentHashMap<MethodExec, Object>();
  
  /**
   * Construct a {@link TestSet} ObjectTest.<br>
   * <br>
   * @param logger The {@link IBaseLogger} object to which messages are sent
   * @param object The {@link Object} against which tests will be executed
   */
  public ObjectTest(Object object)
  {
    mLogger = new ConsoleLogger(this.getClass().getSimpleName());
    mTestedObject = object;
    
    mStats = new Statistics();
    mStats.newCounter(cMethodTestAdded);
    mStats.newCounter(cMethodNotFound);
    mStats.newCounter(cMethodFound);
    mStats.newCounter(cMethodExecuted);
    mStats.newCounter(cMethodExecFailed);
    mStats.newCounter(cMethodExecSucceeded);
    mStats.newCounter(cMethodTestFailed);
    mStats.newCounter(cMethodTestSucceeded);
  }
  
  /**
   * Add a test.<br>
   * <br>
   * A test consists of a method to execute and an expected result the execution should yield. The {@code name} argument
   * designates the method to execute and the {@code args} argument is a variable list of {@link java.lang.Object}s to pass
   * as arguments to the method.<br>
   * <br>
   * When a test is added, it is actually stored in a list for later use.
   *  
   * @param name The name of the method to execute
   * @param exr The expected result the method should generate
   * @param args A variable list of arguments to pass to the method
   */
  public void add(String name, Object exr, Object ... args)
  {
    mLogger.info("Trying to add method: " + name);
    mStats.increment(cMethodTestAdded);
    
    Class<?> [] classes = null;
    if ((args != null) && (args.length > 0))
    {
      classes = new Class<?> [args.length];
      for (int i = 0; i < args.length; ++i)
      {
        // special handling for primitives: byte, short, int, long, float, double, boolean, char
        classes[i] = Object.class;
        if (args[i] != null)
        {
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
    }
    
    Method method = null;
    Class<?> objClass = mTestedObject.getClass();
    MethodExec exec = null;
    while ((method == null) && (objClass != null))
    {
      try
      {
        method = objClass.getDeclaredMethod(name, classes);
        exec = new MethodExec(method, mTestedObject, args);
      }
      catch (NoSuchMethodException e)
      {
        objClass = objClass.getSuperclass();
      }
    }
    
    if (method == null)
    {
      mLogger.warn("Method not found: " + name);
      mStats.increment(cMethodNotFound);
    }
    else
    {
      mMethodTests.put(exec, exr);
      mLogger.info("Method found and added: " + name);
      mStats.increment(cMethodFound);
    }
  }
  
  /**
   * Run tests.<br>
   * <br>
   * Scan the list of tests and execute each and every one of them.<br>
   * <br>
   * When execution ends, test statistics are printed.
   *  
   * @param name The name of the method to execute
   * @param exr The expected result the method should generate
   * @param args A variable list of arguments to pass to the method
   */
  public void run()
  {
    String msg = StringUtils.title("Testing object of class " + mTestedObject.getClass().getName());
    mLogger.trace('\n' + msg);
    
    mStartTimestamp = TimeStamp.now();
    
    for (Map.Entry<MethodExec, Object> entry : mMethodTests.entrySet())
    {
      MethodExec exec = entry.getKey();
      Object     exr  = entry.getValue();
      
      exec.run();
      mStats.increment(cMethodExecuted);
      if (!exec.isSuccessful())
      {
        mStats.increment(cMethodExecFailed);
        mLogger.trace("Method execution ended abnormally. Exception: ", exec.getException());
      }
      else
      {
        mStats.increment(cMethodExecSucceeded);
        Object res = exec.getResult();
        if ((exr == null) && (res == null))
        {
          mLogger.trace("Test succeeded: " + exec.toString());
          mStats.increment(cMethodTestSucceeded);
        }
        else if ((exr != null) && (exr.equals(res)))
        {
          mLogger.trace("Test succeeded: " + exec.toString());
          mStats.increment(cMethodTestSucceeded);
        }
        else
        {
          mLogger.trace("Test failed: " + exec.toString());
          mLogger.trace("Expected: [" + exr + "]; Actual: [" + res + "]");
          mStats.increment(cMethodTestFailed);
        }
      }
    }
    
    mEndTimestamp = TimeStamp.now();
    
    mLogger.trace("Collected statistics:");
    printStats();
  }
  
  /**
   * Print collected statistics.
   */
  public void printStats()
  {
    mLogger.trace("Object statistics for: " + mTestedObject.toString());
    mLogger.trace(StringUtils.trunc(cMethodTestAdded   , 45, '.') + ": " + mStats.getValue(cMethodTestAdded));
    mLogger.trace("   " + StringUtils.trunc(cMethodNotFound    , 42, '.') + ": " + mStats.getValue(cMethodNotFound));
    mLogger.trace("   " + StringUtils.trunc(cMethodFound, 42, '.') + ": " + mStats.getValue(cMethodFound));
    mLogger.trace(StringUtils.trunc(cMethodExecuted    , 45, '.') + ": " + mStats.getValue(cMethodExecuted));
    mLogger.trace("   " + StringUtils.trunc(cMethodExecFailed   , 42, '.') + ": " + mStats.getValue(cMethodExecFailed));
    mLogger.trace("   " + StringUtils.trunc(cMethodExecSucceeded, 42, '.') + ": " + mStats.getValue(cMethodExecSucceeded));
    mLogger.trace("   ");
    mLogger.trace(StringUtils.trunc(cMethodTestFailed   , 45, '.') + ": " + mStats.getValue(cMethodTestFailed));
    mLogger.trace(StringUtils.trunc(cMethodTestSucceeded, 45, '.') + ": " + mStats.getValue(cMethodTestSucceeded));
  }

  /**
   * Get the object's detailed string representation
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n")
      .append(pad).append("  Started=").append(mStartTimestamp.toString()).append("\n")
      .append(pad).append("  Ended=").append(mEndTimestamp.toString()).append("\n")
      .append(pad).append("  Tested Object=").append(mTestedObject.toString()).append("\n")
      .append(pad).append("  Method Tests=(\n")
      .append(StringUtils.asPrintableString(mMethodTests, level+2)).append("\n")
      .append(pad).append("  )\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
