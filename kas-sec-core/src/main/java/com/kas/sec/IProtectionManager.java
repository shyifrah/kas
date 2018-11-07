package com.kas.sec;

import com.kas.sec.entities.UserEntity;

/**
 * An {@link IProtectionManager} is the main object of KAS/SEC.
 * It is responsible for managing all entities, resource classes and permissions.
 * A schematic figure of the relationships between object in this project:
 * <code><br>
 *  +----------------------------+                      +-------------+------------------+<br>
 *  |                            |    +------------->>> | <userid_1>  | <user-entity_1>  |<br>
 *  |    IProtectionManager      |    |                 | <userid_2>  | <user-entity_2>  |<br>
 *  |                            |    |                 | <userid_3>  | <user-entity_3>  |<br>
 *  +----------------------------+    |                 | :           | :                |<br>
 *  | mUsers               >>>---|----+                 | <userid_n>  | <user-entity_n>  |<br>
 *  | mGroups              >>>---|--------------+       +-------------+------------------+<br>
 *  | mResourcesAccessList >>>---|----+         |                                         <br>
 *  +----------------------------+    |         |       +-------------+------------------+<br>
 *                                    |         +--->>> |  <grpid_1>  | <group-entity_1> |<br>
 *                                    |                 |  <grpid_2>  | <group-entity_2> |<br>
 *                                    |                 |  <grpid_3>  | <group-entity_3> |<br>
 *                                    |                 |  :          | :                |<br>
 *                                    |                 |  <grpid_n>  | <group-entity_n> |<br>
 *                                    |                 +-------------+------------------+<br>
 *                                    |                                                   <br>
 *                                    |                 +--------------+-----------------+<br>
 *                                    +------------->>> | <resclass_1> | <access-list_1> |<br>
 *                                                      | <resclass_2> | <access-list_2> |<br>
 *                                                      | <resclass_3> | <access-list_3> |<br>
 *                                                      | :            | :               |<br>
 *                                                      | <resclass_n> | <access-list_n> | >>>---+ <br>
 *                                                      +--------------+-----------------+       | <br>
 *                                                                                               | <br>
 *                                                                                               | <br>
 *                                                                                               | <br>
 *       +---------------------------------------------------------------------------------------+ <br>
 *       |                                                                                         <br>
 *       |                                                                                         <br>
 *       |            +-------------+------------------+                                           <br>
 *       +-------->>> | <pattern_1> | <access-entry_1> |                                           <br>
 *                    | <pattern_2> | <access-entry_2> |                                           <br>
 *                    | <pattern_3> | <access-entry_3> |                                           <br>
 *                    | :           | :                |                                           <br>
 *                    | <pattern_n> | <access-entry_n> | >>>---+
 *                    +-------------+------------------+       |
 *                                                             |
 *                                                             |
 *                                                             |
 *                   
 * </code>
 * 
 * @author Pippo
 */
public interface IProtectionManager
{
  /**
   * Get user by its name
   * 
   * @return the {@link UserEntity}
   */
  public abstract UserEntity getUserByName(String name);
  
  /**
   * Get user by its ID
   * 
   * @return the {@link UserEntity}
   */
  public abstract UserEntity getUserById(int id);
}