package com.kas.sec;

import com.kas.infra.base.AKasObject;
import com.kas.sec.entities.UserEntityDao;
import com.kas.sec.entities.UserEntity;

/**
 * The protection manager is a single point of contact for all access and
 * authentication requests.
 * 
 * @author Pippo
 */
public class ProtectionManager extends AKasObject implements IProtectionManager
{
  /**
   * The singleton instance
   */
  static private ProtectionManager sInstance = new ProtectionManager();
  
  /**
   * Get the singleton instance
   * 
   * @return the singleton instance
   */
  static public ProtectionManager getInstance()
  {
    return sInstance;
  }

  /**
   * The users
   */
  private UserEntityDao mUsers;
  
  /**
   * Construct the Protection Manager
   */
  private ProtectionManager()
  {
    mUsers = new UserEntityDao();
  }
  
  /**
   * Get a user by its name
   * 
   * @param name The name of the user
   * @return the {@link UserEntity} representing the user
   */
  public UserEntity getUserByName(String name)
  {
    return mUsers.get(name);
  }

  /**
   * Get a user by its ID
   * 
   * @param id The ID of the user
   * @return the {@link UserEntity} representing the user
   */
  public UserEntity getUserById(int id)
  {
    return mUsers.get(id);
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
    return null;
  }
}
