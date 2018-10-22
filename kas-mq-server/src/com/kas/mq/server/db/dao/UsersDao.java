package com.kas.mq.server.db.dao;

import java.util.List;
import com.kas.infra.base.IDao;
import com.kas.infra.base.UniqueId;
import com.kas.mq.server.db.DbConnectionPool;
import com.kas.mq.server.db.ServerDbController;
import com.kas.sec.entities.UserEntity;

/**
 * A class that simplifies access to users table
 * 
 * @author Pippo
 */
public class UsersDao // implements IDao<UserEntity>
{
  private static final String cKasUsersTable = "kas.users";
  private static final String cKasUsersToGroupsTable = "kas.users.to.groups";
  
  static public UserEntity getById(int id)
  {
    return null;
  }
  
  static public UserEntity getByName(String name)
  {
    return null;
  }
  
  public UserEntity get(UniqueId id)
  {
    return null;
  }

  public List<UserEntity> getAll()
  {
    return null;
  }

  public void save(UserEntity t)
  {
  }

  public void update(UserEntity t, String[] params)
  {
  }

  public void delete(UserEntity t)
  {
  }
  
  public String name()
  {
    return null;
  }

  public String toPrintableString(int level)
  {
    return null;
  }  
}
