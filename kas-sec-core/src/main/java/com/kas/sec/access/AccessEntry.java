package com.kas.sec.access;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.IObject;
import com.kas.infra.utils.StringUtils;
import com.kas.sec.entities.UserEntity;

/**
 * An access entry is a an entry in an access list that grants certain permissions to
 * the resource (designated by the regular expression) to a certain entity.<br>
 * The resources protected by this entry are designated by the regular expression
 * {@code mResourceRegEx} and the permitted access level by the {@code mPermittedAccess}.
 * 
 * @author Pippo
 */
public class AccessEntry extends AKasObject
{
  /**
   * Logger
   */
  private Logger mLogger = LogManager.getLogger(getClass());
  
  /**
   * The regular expression that matches the resources controlled by this ACE
   */
  private String mResourceRegEx;
  
  /**
   * The {@link Pattern} object compiled from the regular expression
   */
  private Pattern mCompiledPattern = null;
  
  /**
   * The list of entity IDs that are permitted to the resource(s) designated by the regular expression
   */
  private Map<Integer, AccessLevel> mPermittedEntities;
  
  /**
   * Construct an access-entry
   * 
   * @param map
   *   A map of group IDs to their allowed access level
   */
  AccessEntry(String regex, Map<Integer, AccessLevel> map)
  {
    mResourceRegEx = regex;
    mPermittedEntities = map;
  }
  
  /**
   * Get the regular expression
   * 
   * @return
   *   the regular expression
   */
  public String getResourceRegEx()
  {
    return mResourceRegEx;
  }
  
  /**
   * Test if a specific resource is protected by this {@link AccessEntry} 
   * 
   * @return
   *   {@code true} if the resource name matches the regular expression, {@code false} otherwise
   */
  public boolean isResourceMatch(String resource)
  {
    if (mCompiledPattern == null)
      mCompiledPattern = Pattern.compile(mResourceRegEx);
    return mCompiledPattern.matcher(resource).matches();
  }
  
  /**
   * Get the access level allowed for {@code user}
   * 
   * @param user
   *   The user entity
   * @return
   *   the first {@link AccessLevel} that maps to one of the user's groups,
   *   or {@code null} if none found
   */
  public AccessLevel getAccessLevelFor(UserEntity user)
  {
    mLogger.trace("AccessEntry::getAccessLevelFor() - IN, UE={}", user);
    
    AccessLevel level = null;
    List<Integer> gids = user.getGroups();
    for (int i = 0; (level == null) && (i < gids.size()); ++i)
    {
      Integer groupId = gids.get(i);
      level = mPermittedEntities.get(groupId);
    }
    
    mLogger.trace("AccessEntry::getAccessLevelFor() - OUT, Level={}", level);
    return level;
  }
  
  /**
   * Get the string representation
   * 
   * @return
   *   the string representation
   */
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("RegEx=").append(mResourceRegEx);
    return sb.toString();
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
      .append(pad).append("  PermittedEntries=(").append(StringUtils.asPrintableString(mPermittedEntities, level+2)).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
