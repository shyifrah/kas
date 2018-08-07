package com.kas.infra.base;

import java.util.Comparator;

/**
 * A counter used to count events.
 * 
 * @author Pippo
 */
public class Counter extends AKasObject
{
  static class CounterComparator implements Comparator<Counter>
  {
    public int compare(Counter o1, Counter o2)
    {
      return o1.mName.compareTo(o2.mName);
    }
  }
  
  /**
   * Counter name
   */
  private String mName;
  
  /**
   * Counter value
   */
  private int    mValue;
  
  /**
   * Construct a {@link Counter} object with the specified name with an initial value of 0.
   * 
   * @param name The name associated with this counter
   */
  public Counter(String name)
  {
    mName = name;
  }
  
  /**
   * Construct a {@link Counter} object with the specified name and initial value
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
    increment(1);
  }
  
  /**
   * Increment the counter value by the specified value;
   *
   * @param add The value by which to increment the counter
   * @return the counter value
   */
  public void increment(int add)
  {
    mValue += add;
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
   * Returns a replica of this {@link Counter}.
   * 
   * @return a replica of this {@link Counter}
   * 
   * @see com.kas.infra.base.IObject#replicate()
   */
  public Counter replicate()
  {
    return new Counter(mName, mValue);
  }
  
  /**
   * Get the object's detailed string representation. For {@link Counter}, this method returns the same 
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
