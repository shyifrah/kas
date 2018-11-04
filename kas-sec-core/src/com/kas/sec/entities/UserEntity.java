package com.kas.sec.entities;

import java.util.Enumeration;
import java.util.List;
import com.kas.infra.utils.Base64Utils;
import com.kas.infra.utils.StringUtils;
import com.kas.sec.access.AccessEntry;
import com.kas.sec.access.AccessLevel;
import com.kas.sec.resources.ResourceClass;

/**
 * A user entity
 * 
 * @author Pippo
 */
public class UserEntity extends Entity
{
  /**
   * The user's password, encrypted in BASE64
   */
  private byte [] mPassword;
  
  /**
   * List of group IDs the user is connected to
   */
  private List<Integer> mGroups;
  
  /**
   * Construct a new user entity with the specified parameters and
   * an empty list of groups attached.
   * 
   * @param id The entity ID
   * @param name The entity Name
   * @param desc The entity description
   * @param password The user's password
   * @param groups A list of group IDs
   */
  public UserEntity(int id, String name, String desc, String password, List<Integer> groups)
  {
    super(id, name, desc);
    mPassword = Base64Utils.encode(password.getBytes());
    mGroups = groups;
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
   * @return list of {@link GroupEntity}
   */
  public List<Integer> getGroups()
  {
    return mGroups;
  }
  
  /**
   * Test if specified password matches the one stored in data base
   * 
   * @param password Supplied password
   * @return {@code true} if match, {@code false} otherwise
   */
  public boolean isPasswordMatch(String password)
  {
    boolean result = false;
    if ((mPassword == null) && (password == null))
      result = true;
    else if ((mPassword == null) || (password == null))
      result = false;
    else
    {
      String actualPassword = StringUtils.asHexString(mPassword);
      result = actualPassword.equals(password);
    }
    return result;
  }
  
  /**
   * Test if this user is permitted to access {@code resName} of {@code resClass}.
   * 
   * @param resClass The class of the resource
   * @param resName The name of the resource
   * @return {@code true} if access is permitted, {@code false} if access is prohibited
   */
  public boolean isAccessPermitted(ResourceClass resClass, String resName)
  {
    return isAccessPermitted(resClass, resName, AccessLevel.READ_ACCESS);
  }
  
  /**
   * Test if this user is permitted to access {@code resName} of {@code resClass} class
   * with access level {@code level}.<br>
   * If {@code level} is not supported by this {@code resClass} an exception is thrown.
   * 
   * @param resClass The class of the resource
   * @param resName The name of the resource
   * @param level The requested access level
   * @return {@code true} if access is permitted, {@code false} if access is prohibited
   */
  public boolean isAccessPermitted(ResourceClass resClass, String resName, AccessLevel level)
  {
    if (resClass == null)
      throw new IllegalArgumentException("Null resource class");
    
    if (resName == null)
      throw new IllegalArgumentException("Null resource name");
    
    if (level == null)
      throw new IllegalArgumentException("Null access level");
    
    if (!resClass.getEnabledAccessLevels().isLevelEnabled(level.getAccessLevel()))
      throw new IllegalArgumentException("Access level " + level + " is not supported by resource class " + resClass.getName());
    
    //Enumeration<AccessEntry> aces = resClass.getAccessEntryFor(resName);
    
    return true; /// TODO: complete
  }
}
