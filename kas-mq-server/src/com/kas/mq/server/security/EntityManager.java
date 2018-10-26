package com.kas.mq.server.security;

import com.kas.infra.base.AKasObject;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.sec.IRegulator;

/**
 * An {@link EntityManager} is the main object of KAS/SEC.
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
public class EntityManager extends AKasObject implements IRegulator
{
  /**
   * Logger
   */
  private ILogger mLogger;
  
  /**
   * Entity DAOs
   */
  private UserDao mUsers;
  private GroupDao mGroups;
  
  /**
   * Construct an entity using the specified name
   * 
   * @param name The name of the entity
   */
  public EntityManager()
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mUsers = new UserDao();
    mGroups = new GroupDao();
  }
  
  /**
   * Get user by its name
   * 
   * @return the {@link IUserEntity}
   */
  public IUserEntity getUserByName(String name)
  {
    mLogger.debug("EntityManager::getUserByName() - IN");
    
    IUserEntity result = null;
    try
    {
      result = mUsers.get(name);
    }
    catch (Throwable e) {}
    
    mLogger.debug("EntityManager::getUserByName() - OUT, Returns=" + result);
    return result;
  }
  
  /**
   * Get the {@link UserDao}
   * 
   * @return the {@link UserDao}
   */
  public UserDao getUserDao()
  {
    return mUsers;
  }
  
  /**
   * Get the {@link GroupDao}
   * 
   * @return the {@link GroupDao}
   */
  public GroupDao getGroupDao()
  {
    return mGroups;
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
      .append(pad).append(")");
    return sb.toString();
  }
}
