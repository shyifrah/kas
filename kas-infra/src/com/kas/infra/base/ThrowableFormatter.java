package com.kas.infra.base;

public class ThrowableFormatter
{
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private Throwable mThrowable;
  private String    mFormattedString = null;
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public ThrowableFormatter(Throwable t)
  {
    mThrowable = t;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String toString()
  {
    if (mFormattedString == null)
      format();
    
    return mFormattedString;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private void format()
  {
    StringBuilder sb = new StringBuilder();
    format(sb, mThrowable);
    mFormattedString = sb.toString();
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
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
