package com.kas.sec.access;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.UniqueId;
import com.kas.infra.utils.StringUtils;
import com.kas.sec.SecurityController;
import com.kas.sec.entities.IGroupEntity;
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
   * Resource regular expression
   */
  private Pattern mResourceRegEx;
  
  /**
   * The list of entity IDs that are permitted to the resource(s) designated by the regular expression
   */
  private Map<UniqueId, AccessLevel> mPermittedEntities;
  
  /**
   * Construct an access-entry
   * 
   * @param resource A regular expression that identifies the resources protected by this entry
   * @param accessLevel The permitted level of access
   */
  AccessEntry(String resource)
  {
    mResourceRegEx = Pattern.compile(resource);
    mPermittedEntities = new ConcurrentHashMap<UniqueId, AccessLevel>();
  }
  
  /**
   * Test if {@code resource} is protected by this {@link AccessEntry}.
   * 
   * @param resource The resource tested
   * @return {@code true} if this {@link AccessEntry} protects {@code resource}.
   */
  public boolean isMatched(String resource)
  {
    return mResourceRegEx.matcher(resource).matches();
  }
  
  /**
   * Get the access level allowed for the specified entityId
   * 
   * @param entityId a {@link UniqueId} representing the entity
   * @return the allowed {@link AccessLevel}
   */
  public AccessLevel getAllowedAccessLevel(UniqueId entityId)
  {
    AccessLevel result = AccessLevel.NONE_ACCESS;
    
    // if user has an entry of its own in the map -- that's the access level he will get
    AccessLevel level = mPermittedEntities.get(entityId);
    if (level != null)
    {
      result = level;
    }
    else
    {
      UserEntity user = SecurityController.getInstance().getUserEntity(entityId);
      for (IGroupEntity group : user.getGroups())
      {
        level = mPermittedEntities.get(group.getId());
        if (level != null)
        {
          result = level;
          break;
        }
      } 
    }
    
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
      .append(pad).append("  RegEx=").append(mResourceRegEx.toString()).append("\n")
      .append(pad).append("  Access=").append(StringUtils.asPrintableString(mPermittedEntities, level+2)).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
