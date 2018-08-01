package com.kas.infra.base;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * A statistics object is a collection of counters
 * 
 * @author Pippo
 */
public class Statistics extends AKasObject
{
  /**
   * A map of name to {@code Counter}.
   */
  private Map<String, Counter> mCounters = new ConcurrentSkipListMap<String, Counter>();
  
  /**
   * Add a new counter to the map.
   * 
   * @param name The name of the new counter to add
   * @return the counter associated with the name. This can be a newly created one or the existing one
   */
  public Counter newCounter(String name)
  {
    Counter counter = mCounters.get(name);
    if (counter == null)
    {
      counter = new Counter(name);
      mCounters.put(name, counter);
    }
    return counter;
  }
  
  /**
   * Get counter value by its {@code name}.
   * 
   * @param name The name of the counter
   * @return the value of the counter associated with the name.
   * 
   * @throws RuntimeException in case {@code name} is not associated with any {@code Counter} in the map.
   */
  public int getValue(String name)
  {
    Counter counter = mCounters.get(name);
    if (counter == null)
      throw new RuntimeException("Counter '" + name + "' not found");
    
    return counter.getValue();
  }
  
  /**
   * Add a set of counters hold by a {@code Statistics} object to this object.<br>
   * <br>
   * If a counter with a specified name already exists in this object's map, their values are summed.
   * Otherwise, the counter is simply added to this {@code Statistics} object.
   * 
   * @param stats A {@code Statistics} object that contains zero or more counters
   */
  public void put(Statistics other)
  {
    for (Counter counterInOtherMap : other.mCounters.values())
    {
      String name = counterInOtherMap.getName();
      Counter counterInThisMap = mCounters.get(name);
      if (counterInThisMap == null)
        mCounters.put(name, counterInOtherMap);
      else
        counterInThisMap.increment(counterInOtherMap.getValue());
    }
  }
  
  /**
   * Increment a specific counter.
   * 
   * @param name The name of the counter to increment
   * 
   * @throws RuntimeException if the name of the counter does not exist in the map.
   */
  public void increment(String name)
  {
    Counter counter = mCounters.get(name);
    if (counter == null)
      throw new RuntimeException("Counter " + name + " does not exist");
    else
      counter.increment();
  }
  
  /**
   * Get the string representation of the counter.<br>
   * <br>
   * The string is in the format of A=B, where:<br>
   *   A - the name of the counter<br>
   *   B - the counter value
   * 
   * @return a line per counter in the format of name=[value].
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    if (mCounters.size() > 0)
    {
      for (Counter counter : mCounters.values())
      {
        sb.append(counter.toString()).append('\n');
      }
    }
    return sb.toString();
  }
  
  /**
   * Get the object's detailed string representation. For {@code Statistics}, this method returns the same 
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
