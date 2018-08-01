package com.kas.infra.test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import com.kas.infra.base.AStatsCollector;
import com.kas.infra.base.IBaseLogger;
import com.kas.infra.base.Statistics;
import com.kas.infra.base.TimeStamp;
import com.kas.infra.utils.StringUtils;

/**
 * An {@code ObjectTest} is a collection of tests that are executed against a specified object.<br>
 * Tests are scheduled for execution via the {@link #add(String, Object, Object...)} method, and are executed via the {@link #run()} method.
 */
public class ObjectTest extends AStatsCollector
{
  static private final String cMethodTestAdded     = "Object Test - method tests added";
  static private final String cMethodsNotFound     = "Object Test - methods not found";
  static private final String cMethodsInaccessible = "Object Test - methods inaccessible";
  
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
   * The set of tests to be executed.
   */
  private List<MethodTest> mMethodTests = new ArrayList<MethodTest>();
  
  /**
   * Construct a {@code TestSet} object.<br>
   * <br>
   * @param logger The {@code IBaseLogger} object to which messages are sent
   * @param object The {@code Object} against which tests will be executed
   */
  public ObjectTest(Object object)
  {
    mLogger = new ConsoleLogger(this.getClass().getSimpleName());
    mTestedObject = object;
    
    mStats = new Statistics();
    mStats.newCounter(cMethodTestAdded);
    mStats.newCounter(cMethodsNotFound);
    mStats.newCounter(cMethodsInaccessible);
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
    Class<?> [] classes = null;
    if ((args != null) && (args.length > 0))
    {
      classes = new Class<?> [args.length];
      for (int i = 0; i < args.length; ++i)
      {
        // special handling for primitives: byte, short, int, long, float, double, boolean, char
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
    
    Method method;
    try
    {
      method = mTestedObject.getClass().getDeclaredMethod(name, classes);
      MethodTest test = new MethodTest(exr, new MethodRun(method, mTestedObject, args));
      mMethodTests.add(test);
      mStats.increment(cMethodTestAdded);
    }
    catch (NoSuchMethodException e)
    {
      mStats.increment(cMethodsNotFound);
    }
    catch (SecurityException e)
    {
      mStats.increment(cMethodsInaccessible);
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
    
    mStartTimestamp = new TimeStamp();
    
    for (MethodTest test : mMethodTests)
    {
      test.test();
      mStats.put(test.getStats());
    }
    
    mEndTimestamp = new TimeStamp();
    
    mLogger.trace("Collected statistics:");
    printStats();
  }
  
  /**
   * Print collected statistics.
   */
  public void printStats()
  {
    mLogger.trace(cMethodTestAdded + "...: " + mStats.getValue(cMethodTestAdded));
    mLogger.trace(cMethodsNotFound + "...: " + mStats.getValue(cMethodsNotFound));
    mLogger.trace(cMethodsInaccessible + "...: " + mStats.getValue(cMethodsInaccessible));
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
