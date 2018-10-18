package com.kas.sec.entities;

import java.util.ArrayList;
import java.util.List;

public class UserEntity extends Entity implements IUserEntity
{
  private List<IGroupEntity> mGroups;
  
  UserEntity(String name)
  {
    super(name);
    mGroups = new ArrayList<IGroupEntity>();
  }
  
  public List<IGroupEntity> getGroups()
  {
    return mGroups;
  }
}
