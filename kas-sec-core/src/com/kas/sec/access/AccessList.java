package com.kas.sec.access;

import java.util.ArrayList;
import java.util.List;
import com.kas.infra.base.AKasObject;
import com.kas.infra.utils.StringUtils;
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
   * The list of {@link AccessEntry access entries}
   */
  private List<AccessEntry> mAccessList;
  
  /**
   * The default {@link AccessEntry access entry}
   */
  private AccessEntry mDefaultAccessEntry;
  
  /**
   * Construct an {@link AccessList}
   * 
   * @param defaultAccessLevel The default access level that will be granted via the default access entry
   */
  AccessList(AccessLevel defaultAccessLevel)
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mAccessList = new ArrayList<AccessEntry>();
    mDefaultAccessEntry = new AccessEntry(".*");
  }
  
  /**
   * Get the {@link AccessEntry} that will be used to check what level of permissions
   * will be granted when accessing the specified {@code resource}.
   * 
   * @param resource The resource checked
   * @return the matching {@link AccessEntry} that protects the specified resource
   */
  public AccessEntry getAccessEntry(String resource)
  {
    mLogger.debug("AccessList::getAccessEntry() - IN");
    
    AccessEntry result = mDefaultAccessEntry;
    boolean found = false;
    for (int i = 0; !found && i < mAccessList.size(); ++i)
    {
      AccessEntry ace = mAccessList.get(i);
      if (ace.isMatched(resource))
      {
        found = true;
        result = ace;
      }
    }
    
    mLogger.debug("AccessList::getAccessEntry() - OUT, Returns=" + result.toString());
    return result;
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
      .append(pad).append("  DefaultAccess=").append(mDefaultAccessEntry.toPrintableString(level+1)).append("\n")
      .append(pad).append("  List=(").append(StringUtils.asPrintableString(mAccessList, level+1)).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
