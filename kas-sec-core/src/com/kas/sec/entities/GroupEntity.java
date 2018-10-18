package com.kas.sec.entities;

import java.util.ArrayList;
import java.util.List;

public class GroupEntity extends Entity implements IGroupEntity
{
  private List<IUserEntity> mMembers;
  
  GroupEntity(String name)
  {
    super(name);
    mMembers = new ArrayList<IUserEntity>();
  }
  
  public List<IUserEntity> getMembers()
  {
    return mMembers;
  }
}
