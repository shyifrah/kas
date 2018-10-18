package com.kas.sec;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.UniqueId;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.sec.access.AccessList;
import com.kas.sec.entities.Entity;
import com.kas.sec.entities.GroupEntity;
import com.kas.sec.entities.UserEntity;
import com.kas.sec.resources.ResourceClass;

/**
 * An {@link SecurityController} is the main object of KAS/SEC.
 * It is responsible  for managing all entities, resource classes and permissions.
 * A schematic figure of the relationships between object in this project:
 * 
 *  +----------------------+                  +----------+----------------+
 *  |                      |                  | <uuid_1> | <UserEntity_1> |
 *  | Security Controller  |                  | <uuid_2> | <UserEntity_2> |
 *  |                      |    +--------->>> | <uuid_3> | <UserEntity_3> |
 *  +----------------------+    |             | :        | :              |
 *  | mUsers   >>>-------- | ---+             | <uuid_n> | <UserEntity_n> |
 *  | mGroups              |                  +----------+----------------+
 *  | mResourcesAccessList |
 *  +----------------------+
 *  
 * 
 * @author Pippo
 */
public class SecurityController extends AKasObject
{
  /**
   * The singleton instance
   */
  static private SecurityController sInstance = new SecurityController();
  
  /**
   * Get the singleton instance
   * 
   * @return the singleton instance
   */
  static public SecurityController getInstance()
  {
    return sInstance;
  }
  
  /**
   * Logger
   */
  private ILogger mLogger;
  
  /**
   * A map of resource classes to their associated access list
   */
  private Map<ResourceClass, AccessList> mResourcesAccessList;
  
  /**
   * A map of all user entities
   */
  private Map<UniqueId, UserEntity> mUsers;
  
  /**
   * A map of all group entities
   */
  private Map<UniqueId, GroupEntity> mGroups;
  
  /**
   * Construct an entity using the specified name
   * 
   * @param name The name of the entity
   */
  private SecurityController()
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mResourcesAccessList = new ConcurrentHashMap<ResourceClass, AccessList>();
    mUsers = new ConcurrentHashMap<UniqueId, UserEntity>();
    mGroups = new ConcurrentHashMap<UniqueId, GroupEntity>();
  }
  
  /**
   * Get a {@link UserEntity} by its {@link UniqueId}
   * 
   * @param id The {@link UserEntity}'s ID
   * @return the {@link UserEntity}
   */
  public UserEntity getUserEntity(UniqueId id)
  {
    return mUsers.get(id);
  }
  
  /**
   * Get a {@link GroupEntity} by its {@link UniqueId}
   * 
   * @param id The {@link GroupEntity}'s ID
   * @return the {@link GroupEntity}
   */
  public GroupEntity getGroupEntity(UniqueId id)
  {
    return mGroups.get(id);
  }
  
  /**
   * Get a {@link Entity} by its {@link UniqueId}
   * 
   * @param id The {@link Entity}'s ID
   * @return the {@link Entity}
   */
  public Entity getEntity(UniqueId id)
  {
    Entity entity = getUserEntity(id);
    if (entity == null)
      entity = getGroupEntity(id);
    return entity;
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
      //.append(pad).append("  Name=").append(mName).append("\n")
      //.append(pad).append("  Id=").append(mEntityId.toString()).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
