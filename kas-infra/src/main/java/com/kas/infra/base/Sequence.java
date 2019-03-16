package com.kas.infra.base;

/**
 * A Sequence is a class that holds an integer value and increments it
 * whenever requested to
 *  
 * @author Pippo
 */
public class Sequence extends AKasObject
{
  /**
   * A lock object
   */
  private Object mLock = new Object();
  
  /**
   * The integer value
   */
  private int mValue;
  
  /**
   * Initializing the sequence with a value of 0
   */
  public Sequence()
  {
    mValue = 0;
  }
  
  /**
   * Initializing the sequence with the specified {@code initialValue}
   * 
   * @param initialValue
   *   The initial value
   */
  public Sequence(int initialValue)
  {
    mValue = initialValue;
  }
  
  /**
   * Get the sequence current value
   * 
   * @return
   *   the sequence value
   */
  public int get()
  {
    int val;
    synchronized (mLock)
    {
      val = mValue;
    }
    return val;
  }
  
  /**
   * Increment the sequence value
   */
  public void increment()
  {
    synchronized (mLock)
    {
      ++mValue;
    }
  }
  
  /**
   * Get the sequence value and then increment it by 1
   * 
   * @return
   *   the sequence value prior to increment
   */
  public int getAndIncrement()
  {
    int val;
    synchronized (mLock)
    {
      val = mValue;
      ++mValue;
    }
    return val;
  }
  
  /**
   * Get the object's detailed string representation.
   * 
   * @param level
   *   The string padding level
   * @return
   *   the string representation with the specified level of padding
   */
  public String toPrintableString(int level)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("Value=").append(mValue);
    return sb.toString();
  }
}
