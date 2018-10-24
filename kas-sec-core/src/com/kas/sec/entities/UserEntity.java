package com.kas.sec.entities;

import java.util.ArrayList;
import java.util.List;
import com.kas.infra.utils.Base64Utils;

/**
 * A user entity
 * 
 * @author Pippo
 */
public class UserEntity extends Entity implements IUserEntity
{
  /**
   * List of groups the user is attached to
   */
  private List<IGroupEntity> mGroups;
  
  /**
   * The user's password, encrypted in BASE64
   */
  private byte [] mPassword;
  
  /**
   * Construct a brand new user entity with the specified name and password
   * and an empty list of groups. 
   * 
   * @param name The name of the entity
   */
  
  /**
   * Construct a new user entity with the specified parameters and
   * an empty list of groups attached.
   * 
   * @param id The entity ID
   * @param name The entity Name
   * @param desc The entity description
   * @param password The user's password
   */
  UserEntity(int id, String name, String desc, String password)
  {
    super(id, name, desc);
    mPassword = Base64Utils.encode(password.getBytes());
    mGroups = new ArrayList<IGroupEntity>();
  }
  
  /**
   * Get the list of groups this user is member of
   * 
   * @return the list of groups this user is member of
   */
  public List<IGroupEntity> getGroups()
  {
    return mGroups;
  }
}
