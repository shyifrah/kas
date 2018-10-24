package com.kas.mq.server.db.dao;

import java.util.List;
import com.kas.infra.base.IDao;
import com.kas.infra.base.UniqueId;
import com.kas.sec.entities.GroupEntity;

public class GroupsDao implements IDao<GroupEntity>
{
  private static final String cKasGroupsTableName = "kas_mq_groups";
  
  @Override
  public GroupEntity get(UniqueId id)
  {
    return null;
  }

  @Override
  public List<GroupEntity> getAll()
  {
    return null;
  }

  @Override
  public void save(GroupEntity t)
  {
  }

  @Override
  public void update(GroupEntity t, String[] params)
  {
  }

  @Override
  public void delete(GroupEntity t)
  {
  }
  
  @Override
  public String name()
  {
    return null;
  }

  @Override
  public String toPrintableString(int level)
  {
    return null;
  }  
}
