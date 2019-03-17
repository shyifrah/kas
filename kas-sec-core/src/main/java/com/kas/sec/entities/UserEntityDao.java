package com.kas.sec.entities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kas.db.DbConnection;
import com.kas.db.DbConnectionPool;
import com.kas.db.DbUtils;
import com.kas.infra.typedef.StringList;

/**
 * An implementation layer for {@link UserEntity}
 * 
 * @author Pippo
 */
public class UserEntityDao
{
  /**
   * Table name
   */
  private static final String cKasTableName = "kas_mq_users";
  private static final String cKasUsersToGroupsTableName = "kas_mq_users_to_groups";
  
  /**
   * Table columns and types
   */
  private static final Map<String, Class<?>> cTableColumns = new HashMap<String, Class<?>>();
  static
  {
    cTableColumns.put("user_id", int.class);
    cTableColumns.put("user_name", String.class);
    cTableColumns.put("user_description", String.class);
    cTableColumns.put("user_password", String.class);
  }
  
  /**
   * Logger
   */
  private static Logger sLogger = LogManager.getLogger(UserEntityDao.class);
  
  /**
   * Get a {@link UserEntity} by its name
   * 
   * @param name
   *   The name of the {@link UserEntity}
   * @return
   *   the {@link UserEntity} with the specified name or {@code null} if not found
   */
  public static UserEntity getByName(String name)
  {
    sLogger.trace("UserEntityDao::getByName() - IN");
    UserEntity ue = null;
    
    DbConnectionPool dbPool = DbConnectionPool.getInstance();
    DbConnection dbConn = dbPool.allocate();
    Connection conn = dbConn.getConn();
    
    try
    {
      String sql = "SELECT user_id, user_name, user_description, user_password FROM " + cKasTableName + " WHERE user_name = '" + name + "';";
      ResultSet rs = DbUtils.execute(conn, sql);
      if (rs.next()) ue = createUserEntity(rs, conn);
      rs.close();
    }
    catch (SQLException e)
    {
      sLogger.trace("UserEntityDao::getByName() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    sLogger.trace("UserEntityDao::getByName() - OUT, Returns=" + ue);
    return ue;
  }
  
  /**
   * Get a list of {@link UserEntity}s by a pattern
   * 
   * @param pattern
   *   The pattern that should be matched
   * @return
   *   a list of all {@link UserEntity}s that their name matches {@code pattern}
   */
  public static List<UserEntity> getByPattern(String pattern)
  {
    sLogger.trace("UserEntityDao::getByPattern() - IN");
    List<UserEntity> list = new ArrayList<UserEntity>();
    
    DbConnectionPool dbPool = DbConnectionPool.getInstance();
    DbConnection dbConn = dbPool.allocate();
    Connection conn = dbConn.getConn();
    
    try
    {
      String sql = "SELECT user_id, user_name, user_description, user_password FROM " + cKasTableName + " WHERE user_name like '" + pattern + "%%';";
      ResultSet rs = DbUtils.execute(conn, sql);
      
      while (rs.next())
      {
        UserEntity ue = createUserEntity(rs, conn);
        list.add(ue);
      }
      rs.close();
    }
    catch (SQLException e)
    {
      sLogger.trace("UserEntityDao::getByPattern() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    sLogger.trace("UserEntityDao::getByPattern() - OUT, Returns=" + list.toString() + "; Size=" + list.size());
    return list;
  }
  
//  /**
//   * Get {@link UserEntity} associated with the specific name
//   * 
//   * @param id The ID of the {@link UserEntity}
//   * @return The {@link UserEntity} that matches the query
//   */
//  public static UserEntity get(int id)
//  {
//    sLogger.trace("UserEntityDao::get() - IN");
//    UserEntity ue = null;
//    
//    DbConnectionPool dbPool = DbConnectionPool.getInstance();
//    DbConnection dbConn = dbPool.allocate();
//    Connection conn = dbConn.getConn();
//    
//    try
//    {
//      String sql = "SELECT user_id, user_name, user_description, user_password FROM " + cKasTableName + " WHERE user_id = " + id + ";";
//      ResultSet rs = DbUtils.execute(conn, sql);
//      if (rs.next()) ue = createUserEntity(rs, conn);
//      rs.close();
//    }
//    catch (SQLException e)
//    {
//      sLogger.trace("UserEntityDao::get() - Exception caught: ", e);
//    }
//    
//    dbPool.release(dbConn);
//    sLogger.trace("UserEntityDao::get() - OUT, Returns=" + StringUtils.asString(ue));
//    return ue;
//  }
//
  /**
   * Get a list of all {@link UserEntity} objects
   * 
   * @return
   *   a list of all {@link UserEntity} objects
   */
  public static List<UserEntity> getAll()
  {
    sLogger.trace("UserEntityDao::getAll() - IN");
    List<UserEntity> list = new ArrayList<UserEntity>();
    
    DbConnectionPool dbPool = DbConnectionPool.getInstance();
    DbConnection dbConn = dbPool.allocate();
    
    Connection conn = dbConn.getConn();
    try
    {
      String sql = "SELECT user_id, user_name, user_description, user_password FROM " + cKasTableName + ';';
      ResultSet rs = DbUtils.execute(conn, sql);
      UserEntity ue = null;
      while (rs.next())
      {
        ue = createUserEntity(rs, conn);
        list.add(ue);
      }
      rs.close();
    }
    catch (SQLException e)
    {
      sLogger.trace("UserEntityDao::getAll() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    sLogger.trace("UserEntityDao::getAll() - OUT, Returns=" + list.toString() + "; Size=" + list.size());
    return list;
  }
  
//  /**
//   * Update values of {@code t} with the {@code map}
//   * 
//   * @param t The {@link UserEntity} to update
//   * @param map Map of key-value pairs that indicate which fields should be updated with their new values
//   */
//  public static void update(UserEntity t, Map<String, String> map)
//  {
//    sLogger.trace("UserEntityDao::update() - IN");
//    
//    DbConnectionPool dbPool = DbConnectionPool.getInstance();
//    DbConnection dbConn = dbPool.allocate();
//    
//    Connection conn = dbConn.getConn();
//    try
//    {
//      StringBuilder sb = new StringBuilder();
//      sb.append("UPDATE ")
//        .append(cKasTableName)
//        .append(" SET ");
//      
//      for (Map.Entry<String, String> entry : map.entrySet())
//      {
//        String col = entry.getKey().toString();
//        String val = entry.getValue().toString();
//        
//        sb.append(col);
//        Class<?> cls = cTableColumns.get(col);
//        if (String.class.equals(cls))
//          sb.append("='").append(val).append("',");
//        else
//          sb.append("=").append(val).append(",");
//      }
//      
//      String sql = sb.toString();
//      sql = sql.substring(0, sql.length()-1);
//      sLogger.trace("UserEntityDao::update() - Execute SQL: [" + sql + "]");
//      
//      PreparedStatement ps = conn.prepareStatement(sql);
//      ps.execute();
//    }
//    catch (SQLException e)
//    {
//      sLogger.trace("UserEntityDao::update() - Exception caught: ", e);
//    }
//    
//    dbPool.release(dbConn);
//    sLogger.trace("UserEntityDao::update() - OUT");
//  }
//
  
  /**
   * Create a {@link UserEntity} and store it
   * 
   * @param user
   *   The user name
   * @param pass
   *   The user's password
   * @param desc
   *   The user description
   * @param groups
   *   The list of groups the user needs to be a member of
   * @return
   *   the newly created user entity
   */
  public static UserEntity create(String user, String password, String desc, StringList groups)
  {
    sLogger.trace("UserEntityDao::create() - IN");
    
    DbConnectionPool dbPool = DbConnectionPool.getInstance();
    DbConnection dbConn = dbPool.allocate();
    
    UserEntity ue = null;
    
    Connection conn = dbConn.getConn();
    try
    {
      String sql = "INSERT INTO " + cKasTableName + " (user_name, user_password, user_description) " +
        "VALUES ('" + user + "', '" + password + "', '"+ desc + "');";
      sLogger.trace("UserEntityDao::create() - Execute SQL: [{}]", sql);
      
      PreparedStatement ps = conn.prepareStatement(sql);
      ps.execute();
      
      ue = getByName(user);
    }
    catch (SQLException e)
    {
      sLogger.trace("UserEntityDao::create() - Exception caught: ", e);
    }
    
    if (ue != null)
    {
      for (String group : groups)
      {
        GroupEntity ge = GroupEntityDao.getByName(group);
        if (ge != null)
        {
          try
          {
            String sql = "INSERT INTO " + cKasUsersToGroupsTableName + " (user_id, group_id) " +
              "VALUES (" + ue.getId() + ", " + ge.getId() + ");";
            sLogger.trace("UserEntityDao::create() - Execute SQL: [{}]", sql);
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.execute();
            
            ue = getByName(user);
          }
          catch (SQLException e)
          {
            sLogger.trace("UserEntityDao::create() - Exception caught: ", e);
          }
        }
      }
    }
    
    dbPool.release(dbConn);
    sLogger.trace("UserEntityDao::create() - OUT, Result={}", ue);
    return ue;
  }

  /**
   * Delete the specified {@link UserEntity}
   * 
   * @param user
   *   The user name
   * @return
   *   {@code true} if user was deleted, {@code false} otherwise
   */
  public static boolean delete(String user)
  {
    sLogger.trace("UserEntityDao::delete() - IN");
    
    boolean result = false;
    
    DbConnectionPool dbPool = DbConnectionPool.getInstance();
    DbConnection dbConn = dbPool.allocate();
    
    Connection conn = dbConn.getConn();
    try
    {
      String sql = "DELETE FROM " + cKasTableName + " WHERE user_name = '" + user + "';";
      sLogger.trace("UserEntityDao::delete() - Execute SQL: [" + sql + "]");
      
      PreparedStatement ps = conn.prepareStatement(sql);
      ps.execute();
      result = true;
    }
    catch (SQLException e)
    {
      sLogger.trace("UserEntityDao::delete() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    sLogger.trace("UserEntityDao::delete() - OUT, Returns={}", result);
    return result;
  }
  
  /**
   * Extract a list of groups in which a user participates
   * 
   * @param conn
   *   The connection to be used to query the DB
   * @param id
   *   The {@link UserEntity user entity's} ID
   * @return
   *   a list of group IDs 
   * @throws SQLException
   *   If one is thrown by java.sql.* classes
   */
  private static List<Integer> getUserGroups(Connection conn, int id) throws SQLException
  {
    sLogger.trace("UserEntityDao::getUserGroups() - IN");
    
    String sql = "SELECT group_id FROM " + cKasUsersToGroupsTableName + " WHERE user_id = " + id;
    ResultSet rs2 = DbUtils.execute(conn, sql);
    List<Integer> ugids = new ArrayList<Integer>();
    while (rs2.next())
    {
      int gid = rs2.getInt("group_id");
      ugids.add(gid);
    }
    rs2.close();
    
    sLogger.trace("UserEntityDao::getUserGroups() - OUT, List.size()={}", ugids.size());
    return ugids;
  }
  
  /**
   * Create a user entity based on the output of a query
   * 
   * @param rs
   *   The output of a query
   * @param conn
   *   An open Connection to the DB for further queries
   * @return
   *   a {@link UserEntity} 
   * @throws SQLException
   *   If one is thrown by java.sql.* classes
   */
  private static UserEntity createUserEntity(ResultSet rs, Connection conn) throws SQLException
  {
    sLogger.trace("UserEntityDao::createUserEntity() - IN");
    
    int uid = rs.getInt("user_id");
    String uname = rs.getString("user_name");
    String udesc = rs.getString("user_description");
    String upass = rs.getString("user_password");
    
    List<Integer> ugids = getUserGroups(conn, uid);
    
    UserEntity ue = new UserEntity(uid, uname, udesc, upass, ugids);
    sLogger.trace("UserEntityDao::createUserEntity() - OUT, UserEntity={}", ue);
    return ue;
  }
}
