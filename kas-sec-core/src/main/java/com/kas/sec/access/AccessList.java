package com.kas.sec.access;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.IObject;
import com.kas.sec.resources.ResourceClass;

/**
 * An access-list is an object that holds a {@link List} of {@link AccessEntry access entries}.<br>
 * <br>
 * Each {@link ResourceClass} is associated with one access list and a default access entry
 * which is used to decide whether access to a resource is permitted in case no suitable entry was found.
 * 
 * @author Pippo
 */
public class AccessList extends AKasObject
{
  /**
   * Logger
   */
  private Logger mLogger;
  
  /**
   * Resource class name
   */
  private String mResourceClassName;

  /**
   * Access Entry DAO
   */
  private AccessEntryDao mEntries;
  
  /**
   * Construct an {@link AccessList}
   * 
   * @param name
   *   The name of the resource class on which this access list controls
   */
  public AccessList(String name)
  {
    mLogger = LogManager.getLogger(getClass());
    mEntries = new AccessEntryDao(name);
  }
  
  /**
   * Get an enumeration of {@link AccessEntry access entries} that protect {@code resource}.
   * 
   * @param resource
   *   The resource checked
   * @return
   *   the matching {@link AccessEntry access entries} that protects the specified resource
   */
  public Enumeration<AccessEntry> getAccessEntries(String resource)
  {
    mLogger.trace("AccessList::getAccessEntry() - IN");
    
    List<AccessEntry> entries = mEntries.getAll();
    Vector<AccessEntry> result = new Vector<AccessEntry>();
    for (AccessEntry ace : entries)
    {
      if (ace.isResourceMatch(resource))
        result.add(ace);
    }
    
    mLogger.trace("AccessList::getAccessEntry() - OUT, Returns={} ACEs", result.size());
    return result.elements();
  }
  
  /**
   * Returns the {@link IObject} string representation.
   * 
   * @param level
   *   The required padding level
   * @return
   *   the string representation with the specified level of padding
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n")
      .append(pad).append("  ForResourceClass=").append(mResourceClassName).append('\n')
      .append(pad).append("  Entries=").append(mEntries.toPrintableString(level+1)).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
