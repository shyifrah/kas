package com.kas.infra.utils.helpers;

/**
 * A utility class for formatting exceptions
 * 
 * @author Pippo
 */
public final class ThrowableFormatter
{
  /**
   * The exception to be formatted
   */
  private Throwable mThrowable;
  
  /**
   * The resulted string
   */
  private String mFormattedString = null;
  
  /**
   * Construct the object
   * 
   * @param t
   *   The {@link Throwable} to be formatted
   */
  public ThrowableFormatter(Throwable t)
  {
    mThrowable = t;
  }
  
  /**
   * Return the formatted string
   * 
   * @return
   *   the formatted exception
   */
  public String toString()
  {
    if (mFormattedString == null)
      format();
    
    return mFormattedString;
  }
  
  /**
   * Format the exception
   */
  private void format()
  {
    StringBuilder sb = new StringBuilder();
    format(sb, mThrowable);
    mFormattedString = sb.toString();
  }
  
  /**
   * Format the exception.<br>
   * <br>
   * This method is invoked recursively (in case there's a causer exception), so it receives the buffer
   * and not just returns it.
   * 
   * @param buffer
   *   The buffer (a {@link StringBuilder} object) which will receive the formatted string
   * @param e
   *   The exception to format
   */
  private void format(StringBuilder buffer, Throwable e)
  {
    if (e == null) return;
    
    StackTraceElement [] stElements = e.getStackTrace();
    if ((stElements == null) || (stElements.length == 0)) return;
    
    // class name
    buffer.append(e.getClass().getName()).append(": \n");
    
    // message, if available
    String msg = e.getMessage();
    if (msg != null) buffer.append(msg.trim()).append('\n');
    
    // stack trace elements
    for (StackTraceElement ele : stElements)
    {
      buffer.append("    ")
        .append(ele.toString())
        .append('\n');
    }
    
    // causer
    Throwable causer = e.getCause();
    if (causer == null) return;
    
    buffer.append("Caused by: ");
    format(buffer, causer);
  }
}
