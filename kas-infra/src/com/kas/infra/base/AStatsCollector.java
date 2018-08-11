package com.kas.infra.base;

/**
 * A Stats-Collector class is one that has a {@link Statistics} member that is used to collect
 * statistics on system operation. In addition, the {@link AStatsCollector} class need to implement
 * the {@link #printStats()} method which prints the contents of the collected statistics.
 * 
 * @author Pippo
 */
public abstract class AStatsCollector extends AKasObject
{
  /**
   * The statistics collected by this object.
   */
  protected Statistics mStats = new Statistics();
  
  /**
   * Get the statistics
   * 
   * @return the statistics
   */
  public Statistics getStats()
  {
    return mStats;
  }
  
  /**
   * Print collected statistics
   */
  public abstract void printStats();
  
  /**
   * Get the object's detailed string representation. 
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public abstract String toPrintableString(int level);
}
