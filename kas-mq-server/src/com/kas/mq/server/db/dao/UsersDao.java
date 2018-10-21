package com.kas.mq.server.db.dao;

import java.util.List;
import com.kas.infra.base.IDao;
import com.kas.infra.base.UniqueId;
import com.kas.mq.server.db.ServerDbController;
import com.kas.sec.entities.UserEntity;

public class UsersDao implements IDao<UserEntity>
{
  private static final String cKasUsersTableName = "kas.users";
  
  private ServerDbController mController;
  
  public UsersDao(ServerDbController controller)
  {
    mController = controller;
  }
  
  @Override
  public UserEntity get(UniqueId id)
  {
    return null;
  }

  @Override
  public List<UserEntity> getAll()
  {
    return null;
  }

  @Override
  public void save(UserEntity t)
  {
  }

  @Override
  public void update(UserEntity t, String[] params)
  {
  }

  @Override
  public void delete(UserEntity t)
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
