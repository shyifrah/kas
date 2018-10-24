package com.kas.mq.server.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.kas.mq.server.db.DbConnection;
import com.kas.mq.server.db.DbConnectionPool;
import com.kas.sec.entities.UserEntity;

/**
 * A class that simplifies access to users table
 * 
 * @author Pippo
 */
public class UsersDao // implements IDao<UserEntity>
{
  private static final String cKasUsersTable = "kas_mq_users";
  private static final String cKasUsersToGroupsTable = "kas_mq_users_to_groups";
  
  static public List<UserEntity> getAll()
  {
    List<UserEntity> list = new ArrayList<UserEntity>();
    String sql = "SELECT u.id as uid, u.name as user_name, u.description as udesc, u.password as pwd, g.name as group_name " +
      "FROM kas_mq_users u " +
      "LEFT JOIN kas_mq_users_to_groups ug ON u.id = ug.user_id " +
      "LEFT JOIN kas_mq_groups g ON g.id = ug.group_id " +
      "ORDER BY u.name;";
    
    DbConnectionPool pool = DbConnectionPool.getPool();
    DbConnection dbConn = pool.allocate();
    
    Connection conn = dbConn.getConnection();
    try
    {
      PreparedStatement ps = conn.prepareStatement(sql);
      ResultSet rs = ps.executeQuery();
      
      int last_uid = -1;
      UserEntity u = null;
      while (rs.next())
      {
        int uid = rs.getInt("uid");
        if (last_uid != uid)
        {
          last_uid = uid;
          String name = rs.getString("user_name");
          String desc = rs.getString("udesc");
          String pswd = rs.getString("pwd");
          u = new UserEntity(uid, name, desc, pswd);
          list.add(u);
        }
        else
        {
          
        }
      }
    }
    catch (SQLException e)
    {
      
    }
    
    pool.release(dbConn);
    return list;
  }
  
//  static public UserEntity getById(int id)
//  {
//    return null;
//  }
//  
//  static public UserEntity getByName(String name)
//  {
////    SELECT u.name as user_name, u.description as desc, u.password as pwd, g.name as group_name
////    FROM kas_mq_users u 
////    JOIN kas_mq_users_to_groups ug ON u.id = ug.user_id 
////    JOIN kas_mq_groups g ON g.id = ug.group_id
////    ORDER BY u.name;
//    
//    
//    
//    //SELECT u.name as user_name, u.description as descript, u.password as pwd, g.name as group_name
//    //FROM kas_mq_users u
//    //LEFT JOIN kas_mq_users_to_groups ug ON u.id = ug.user_id
//    //LEFT JOIN kas_mq_groups g ON g.id = ug.group_id
//    //ORDER BY u.name;
//    
//    return null;
//  }
//  
//  public UserEntity get(UniqueId id)
//  {
//    return null;
//  }
//
//  public List<UserEntity> getAll()
//  {
//    return null;
//  }
//
//  public void save(UserEntity t)
//  {
//  }
//
//  public void update(UserEntity t, String[] params)
//  {
//  }
//
//  public void delete(UserEntity t)
//  {
//  }
//  
//  public String name()
//  {
//    return null;
//  }
//
//  public String toPrintableString(int level)
//  {
//    return null;
//  }  
}
