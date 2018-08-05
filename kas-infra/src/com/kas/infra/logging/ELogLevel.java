package com.kas.infra.logging;

import com.kas.infra.base.IObject;

/**
 * The various log-levels allowed by the {@link IBaseLogger}
 * 
 * @author Pippo
 */
public enum ELogLevel implements IObject
{

  /**
   * FATAL messages are issued when a severe error occurs and the system cannot continue to function
   */
  FATAL,
  
  /**
   * ERROR messages indicates on erroneous conditions in system operation
   */
  ERROR,
  
  /**
   * WARN messages indicates a problem mild problem occurred, but the system can continue to function properly 
   */
  WARN,
  
  /**
   * INFO messages provides information on the system
   */
  INFO,
  
  /**
   * TRACE messages shows some details about system operation.
   */
  TRACE,
  
  /**
   * DEBUG messages are very similar to {@link TRACE} messages, but they are a bit more detailing
   */
  DEBUG,
  
  /**
   * DIAG messages are very informative messages that actually log all entrances and exits to and from methods
   */
  DIAG;
  
  /**
   * The enum name
   */
  private String mName = null;
  
  /**
   * Is one log level greater or equal to an other
   * 
   * @param other A second {@code ELogLevel} to compare
   * 
   * @return {@code true} if this {@code ELogLevel} is greater or equal to {@code other}
   */
  public boolean isGreaterOrEqual(ELogLevel other)
  {
    return this.compareTo(other) >= 0;
  }
  
  /**
   * Return a formatted version of the enum name
   * 
   * @return a five-character length log-level, padded with blanks to the right, if necessary
   */
  public String toString()
  {
    if (mName == null)
    {
      String str = name() + "     ";
      mName = str.substring(0,5);
    }
    return mName;
  }

  /**
   * Returns a replica of this {@link #ELogLevel}.
   * 
   * @return a replica of this {@link #ELogLevel}
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public ELogLevel replicate()
  {
    ELogLevel other = this;
    return other;
  }

  /**
   * Get the enum detailed string representation. For {@code ELogLevel} this is the same as calling
   * {@link #toString()} method.
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public String toPrintableString(int level)
  {
    return toString();
  }
}