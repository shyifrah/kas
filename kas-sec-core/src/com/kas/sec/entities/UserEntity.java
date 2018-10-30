package com.kas.sec.entities;

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
   * The user's password, encrypted in BASE64
   */
  private byte [] mPassword;
  
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
  }

  /**
   * Get the user's password
   * 
   * @return the user's password
   */
  public byte[] getPassword()
  {
    return mPassword;
  }
  
  /**
   * Get the groups in which a user is member
   * 
   * @return list of {@link IGroupEntity}
   */
  public List<IGroupEntity> getGroups()
  {
    return null;
  }
}
