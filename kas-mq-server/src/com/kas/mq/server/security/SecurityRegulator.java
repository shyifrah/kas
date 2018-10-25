package com.kas.mq.server.security;

import com.kas.infra.base.AKasObject;
import com.kas.infra.base.UniqueId;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.server.db.dao.GroupsDao;
import com.kas.mq.server.db.dao.UsersDao;
import com.kas.sec.IRegulator;
import com.kas.sec.entities.Entity;
import com.kas.sec.entities.GroupEntity;
import com.kas.sec.entities.UserEntity;

/**
 * An {@link SecurityRegulator} is the main object of KAS/SEC.
 * It is responsible for managing all entities, resource classes and permissions.
 * A schematic figure of the relationships between object in this project:
 * <code><br>
 *  +----------------------------+                             +------------+------------------+<br>
 *  |                            |    +-------------------->>> | <userid_1> | <user-entity_1>  |<br>
 *  |    Security Regulator      |    |                        | <userid_2> | <user-entity_2>  |<br>
 *  |                            |    |                        | <userid_3> | <user-entity_3>  |<br>
 *  +----------------------------+    |                        | :          | :                |<br>
 *  | mUsers               >>>---|----+                        | <userid_n> | <user-entity_n>  |<br>
 *  | mGroups              >>>---|--------------------+        +------------+------------------+<br>
 *  | mResourcesAccessList >>>---|----+               |                                         <br>
 *  +----------------------------+    |               |        +------------+------------------+<br>
 *                                    |               +---->>> | <grpid_1>  | <group-entity_1> |<br>
 *                                    |                        | <grpid_2>  | <group-entity_2> |<br>
 *                                    |                        | <grpid_3>  | <group-entity_3> |<br>
 *                                    |                        | :          | :                |<br>
 *                                    |                        | <grpid_n>  | <group-entity_n> |<br>
 *                                    |                        +------------+------------------+<br>
 *                                    |                                                          <br>
 *                                    |                        +--------------+-----------------+<br>
 *                                    +-------------------->>> | <resclass_1> | <access-list_1> |<br>
 *                                                             | <resclass_2> | <access-list_2> |<br>
 *                                                             | <resclass_3> | <access-list_3> |<br>
 *                                                             | :            | :               |<br>
 *                                                             | <resclass_n> | <access-list_n> |<br>
 *                                                             +--------------+-----------------+<br>
 *                   
 * </code>
 * 
 * @author Pippo
 */
public class SecurityRegulator extends AKasObject implements IRegulator
{
  /**
   * The singleton instance
   */
  static private SecurityRegulator sInstance = new SecurityRegulator();
  
  /**
   * Get the singleton instance
   * 
   * @return the singleton instance
   */
  static public SecurityRegulator getInstance()
  {
    return sInstance;
  }
  
  /**
   * Logger
   */
  private ILogger mLogger;
  
  private UsersDao mUsersDao;
  private GroupsDao mGroupsDao;
  
  /**
   * Construct an entity using the specified name
   * 
   * @param name The name of the entity
   */
  private SecurityRegulator()
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
  }
  
  public void init()
  {
    
  }
  
  public UsersDao getUsersDao()
  {
    return mUsersDao;
  }
  
  public GroupsDao getGroupsDao()
  {
    return mGroupsDao;
  }
  
//  /**
//   * Get a {@link UserEntity} by its {@link UniqueId}
//   * 
//   * @param id The {@link UserEntity}'s ID
//   * @return the {@link UserEntity}
//   */
//  public UserEntity getUserEntity(int id)
//  {
//    UserEntity ue = null;
//    return ue;
//  }
//  
//  /**
//   * Get a {@link GroupEntity} by its {@link UniqueId}
//   * 
//   * @param id The {@link GroupEntity}'s ID
//   * @return the {@link GroupEntity}
//   */
//  public GroupEntity getGroupEntity(int id)
//  {
//    GroupEntity ge = null;
//    return ge;
//  }
//  
//  /**
//   * Get a {@link Entity} by its {@link UniqueId}
//   * 
//   * @param id The {@link Entity}'s ID
//   * @return the {@link Entity}
//   */
//  public Entity getEntity(int  id)
//  {
//    mLogger.debug("SecurityController::getEntity() - IN");
//    
//    Entity entity = getUserEntity(id);
//    if (entity == null)
//      entity = getGroupEntity(id);
//    
//    mLogger.debug("SecurityController::getEntity() - OUT, Returns=" + entity.toString());
//    return entity;
//  }
//  
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
      .append(pad).append(")");
    return sb.toString();
  }
}
