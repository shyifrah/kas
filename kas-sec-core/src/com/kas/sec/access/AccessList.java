package com.kas.sec.access;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import com.kas.infra.base.AKasObject;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
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
  private ILogger mLogger;
  
  /**
   * The RegEx-to-AccessEntry map
   */
  private Map<Pattern, AccessEntry> mAccessEntriesMap;
  
  /**
   * The default {@link AccessLevel access level}
   */
  private AccessLevel mDefaultAccessLevel;
  
  /**
   * Construct an {@link AccessList}
   * 
   * @param defaultAccessLevel The default access level that will be granted via the default access entry
   */
  AccessList(AccessLevel defaultAccessLevel)
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mAccessEntriesMap = new ConcurrentHashMap<Pattern, AccessEntry>();
    mDefaultAccessLevel = defaultAccessLevel;
    // TODO: Need to load the map
  }
  
  /**
   * Get an enumeration of {@link AccessEntry access entries} that protect {@code resource}.
   * 
   * @param resource The resource checked
   * @return the matching {@link AccessEntry access entries} that protects the specified resource
   */
  public Enumeration<AccessEntry> getAccessEntries(String resource)
  {
    mLogger.debug("AccessList::getAccessEntry() - IN");
    
    Vector<AccessEntry> result = new Vector<AccessEntry>();
    for (Map.Entry<Pattern, AccessEntry> entry : mAccessEntriesMap.entrySet())
    {
      Pattern pat = entry.getKey();
      if (pat.matcher(resource).matches())
        result.add(entry.getValue());
    }
    
    mLogger.debug("AccessList::getAccessEntry() - OUT, Returns=" + result.size() + " ACEs");
    return result.elements();
  }
  
  /**
   * Get the default access level assigned
   * 
   * @return the default access level assigned
   */
  public AccessLevel getDefaultAccessLevel()
  {
    return mDefaultAccessLevel;
  }
  
  /**
   * Get the object's detailed string representation
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n")
      .append(pad).append("  DefaultAccess=").append(mDefaultAccessLevel.toPrintableString(level+1)).append("\n")
      //.append(pad).append("  List=(").append(StringUtils.asPrintableString(mAccessList, level+1)).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
