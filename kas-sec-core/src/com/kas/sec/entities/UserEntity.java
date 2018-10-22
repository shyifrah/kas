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
  UserEntity(String name, String password)
  {
    super(name);
    mPassword = Base64Utils.encode(password.getBytes());
    mGroups = new ArrayList<IGroupEntity>();
  }
  
  /**
   * Construct a user entity with the specified name
   * 
   * @param name The name of the entity
   */
  UserEntity(String name)
  {
    super(name);
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
