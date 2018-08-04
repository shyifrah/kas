package  com.kas.infra.test;

import java.lang.reflect.Method;
import com.kas.infra.base.AKasObject;

/**
 * A method execution.
 * 
 * @see com.kas.infra.test.MethodTest
 * @author Pippo
 */
public class MethodRun extends AKasObject implements Runnable
{
  /**
   * Object against which the method is executed
   */
  private Object mObject;
  
  /**
   * Method to invoke
   */
  private Method mMethod;
  
  /**
   * List of arguments to pass the method
   */
  private Object [] mArguments;
  
  /**
   * The return value resulted by the method invocation.<br>
   * A {@code null} value can represent both a {@code null} returned by the method, and {@code void} returned value.
   */
  private Object mResult = null;
  
  /**
   * The exception thrown by the method invocation
   */
  private Exception mException = null;
  
  /**
   * Create a {@code MethodRun} object.<br>
   * <br>
   * The run consisting of a {@link java.lang.reflect.Method} object, the {@code object} on which it is invoked, and a list
   * of arguments to pass the method.
   * 
   * @param method A method object to invoke
   * @param object An object on which the method is invoked
   * @param args A variable list of arguments to pass the method
   */
  MethodRun(Method method, Object object, Object ... args)
  {
    mMethod = method;
    mObject = object;
    mArguments = args;
  }
  
  /**
   * Run the method.<br>
   * <br>
   * First we make sure the method is accessible (because it is possible that the {@code MethodRun} object was created
   * for a private method. Then we invoke the method via reflection mechanism. Finally, we save the result and the
   * exception, if one was thrown, that the invocation generated.
   */
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
  
  /**
   * Return an indication whether method execution completed normally or abnormally (exception thrown).
   * 
   * @return {@code false} if exception was thrown, {@code true} otherwise 
   */
  public boolean isSuccessful()
  {
    return mException == null;
  }
  
  /**
   * Get the exception thrown by the method execution.
   * 
   * @return the exception thrown
   */
  public Exception getException()
  {
    return mException;
  }
  
  /**
   * Get the return value from method execution.
   * 
   * @return the return value
   */
  public Object getResult()
  {
    return mResult;
  }
  
  /**
   * Get the method name
   * 
   * @return the method name
   */
  public String getMethodName()
  {
    return mMethod.getName();
  }
  
  /**
   * Get the method signature.
   * 
   * @return the method signature
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    
    String methodName = mMethod.getName();
    String returnType = mMethod.getReturnType().getSimpleName();
    
    sb.append(returnType)
      .append(' ')
      .append(methodName)
      .append('(');
    
    Class<?> [] argTypes = mMethod.getParameterTypes();
    if ((argTypes != null) && (argTypes.length >= 1))
    {
      for (int i = 0; i < argTypes.length; ++i)
      {
        if (i > 0) sb.append(", ");
        Class<?> argType = argTypes[i];
        sb.append(argType.getSimpleName());
      }
    }
    
    sb.append(')');
    return sb.toString();
  }
  
  /**
   * Get the object's detailed string representation. For {@code MethodRun}, this method returns the same 
   * result as {@link #toString()}.
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   * @see #toString()
   */
  public String toPrintableString(int level)
  {
    return toString();
  }
}
