package com.kas.infra.base;

/**
 * A counter used to count events.
 * 
 * @author Pippo
 *
 */
public class Counter extends AKasObject
{
  /**
   * Counter name
   */
  private String mName;
  
  /**
   * Counter value
   */
  private int    mValue;
  
  /**
   * Construct a {@code Counter} object with the specified name with an initial value of 0.
   * 
   * @param name The name associated with this counter
   */
  public Counter(String name)
  {
    mName = name;
  }
  
  /**
   * Construct a {@code Counter} object with the specified name and initial value
   * 
   * @param name The name associated with this counter
   * @param value The initial value assigned to this counter
   */
  public Counter(String name, int value)
  {
    mName = name;
    mValue = value;
  }
  
  /**
   * Get the counter name
   * 
   * @return the counter name
   */
  public String getName()
  {
    return mName;
  }
  
  /**
   * Get the counter value
   * 
   * @return the counter value
   */
  public int getValue()
  {
    return mValue;
  }
  
  /**
   * Increment the counter value by 1
   * 
   * @return the counter value
   */
  public void increment()
  {
    ++mValue;
  }
  
  /**
   * Get the string representation of the counter.<br>
   * <br>
   * The string is in the format of A=B, where:<br>
   *   A - the name of the counter
   *   B - the counter value
   * 
   * @return the counter
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append(mName).append("=[").append(mValue).append(']');
    return sb.toString();
  }
  
  /**
   * Get the object's detailed string representation. For {@code Counter}, this method returns the same 
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
