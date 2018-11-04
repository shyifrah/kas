package com.kas.sec.access;

import java.util.List;
import java.util.Map;
import com.kas.infra.base.AKasObject;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.sec.entities.UserEntity;

/**
 * An access entry is a an entry in an access list that grants certain permissions to
 * the resource (designated by the regular expression) to a certain entity.<br>
 * <br>
 * The resources protected by this entry are designated by the regular expression {@code mResourceRegEx}
 * and the permitted access level by the {@code mPermittedAccess}.
 * 
 * @author Pippo
 */
public class AccessEntry extends AKasObject
{
  /**
   * Logger
   */
  private ILogger mLogger = LoggerFactory.getLogger(this.getClass());
  
  /**
   * The list of entity IDs that are permitted to the resource(s) designated by the regular expression
   */
  private Map<Integer, AccessLevel> mPermittedEntities;
  
  /**
   * Construct an access-entry
   * 
   * @param map A map of group IDs to their allowed access level
   */
  AccessEntry(Map<Integer, AccessLevel> map)
  {
    mPermittedEntities = map;
  }
  
  /**
   * Get the access level allowed for {@code user}
   * 
   * @param user The user entity
   * @return The first {@link AccessLevel} that maps to one of the user's groups,
   * or {@code null} if none found
   */
  public AccessLevel getAccessLevelFor(UserEntity user)
  {
    mLogger.debug("AccessEntry::getAccessLevelFor() - IN, UE=" + user);
    
    AccessLevel level = null;
    List<Integer> gids = user.getGroups();
    for (int i = 0; (level == null) && (i < gids.size()); ++i)
    {
      Integer groupId = gids.get(i);
      level = mPermittedEntities.get(groupId);
    }
    
    mLogger.debug("AccessEntry::getAccessLevelFor() - OUT, Level=" + level);
    return level;
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
      .append(pad).append("  Access=").append(StringUtils.asPrintableString(mPermittedEntities, level+2)).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
