package com.kas.infra.base;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.kas.infra.test.ConsoleLogger;

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
  private Map<String, Counter> mCounters = new ConcurrentHashMap<String, Counter>();
  
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
   * Add a set of counters hold by a {@code Statistics} object to this object.<br>
   * <br>
   * Note that if a counter with a specified name already exists in this object's map, it is replaced.
   * 
   * @param stats A {@code Statistics} object that contains zero or more counters
   */
  public void put(Statistics stats)
  {
    mCounters.putAll(stats.mCounters);
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
   * Print statistics.
   * @param name The name of the counter to increment
   * 
   * @throws RuntimeException if the name of the counter does not exist in the map.
   */
  public void print()
  {
    IBaseLogger logger = new ConsoleLogger(this.getClass().getSimpleName());
    StringBuilder sb = new StringBuilder("Collected statistics:\n").append(toString());
    logger.trace(sb.toString());
  }
  
  /**
   * Get the string representation of the counter.<br>
   * <br>
   * The string is in the format of A=B, where:<br>
   *   A - the name of the counter
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
