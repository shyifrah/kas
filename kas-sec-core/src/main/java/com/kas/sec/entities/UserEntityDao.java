package com.kas.sec.entities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.kas.db.DbConnection;
import com.kas.db.DbConnectionPool;
import com.kas.db.DbUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;

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
  private static ILogger sLogger = LoggerFactory.getLogger(UserEntityDao.class);
  
  /**
   * Get a {@link UserEntity} by its name
   * 
   * @param name The name of the {@link UserEntity}
   * @return the {@link UserEntity} with the specified name or {@code null} if not found
   */
  public static UserEntity getByName(String name)
  {
    sLogger.debug("UserEntityDao::getByName() - IN");
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
      sLogger.debug("UserEntityDao::getByName() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    sLogger.debug("UserEntityDao::getByName() - OUT, Returns=" + ue);
    return ue;
  }
  
  /**
   * Get a list of {@link UserEntity}s by a pattern
   * 
   * @param pattern The pattern that should be matched
   * @return a list of all {@link UserEntity}s that their name matches {@code pattern}
   */
  public static List<UserEntity> getByPattern(String pattern)
  {
    sLogger.debug("UserEntityDao::getByPattern() - IN");
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
      sLogger.debug("UserEntityDao::getByPattern() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    sLogger.debug("UserEntityDao::getByPattern() - OUT, Returns=" + list.toString() + "; Size=" + list.size());
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
//    sLogger.debug("UserEntityDao::get() - IN");
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
//      sLogger.debug("UserEntityDao::get() - Exception caught: ", e);
//    }
//    
//    dbPool.release(dbConn);
//    sLogger.debug("UserEntityDao::get() - OUT, Returns=" + StringUtils.asString(ue));
//    return ue;
//  }
//
  /**
   * Get a list of all {@link UserEntity} objects
   * 
   * @return a list of all {@link UserEntity} objects
   */
  public static List<UserEntity> getAll()
  {
    sLogger.debug("UserEntityDao::getAll() - IN");
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
      sLogger.debug("UserEntityDao::getAll() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    sLogger.debug("UserEntityDao::getAll() - OUT, Returns=" + list.toString() + "; Size=" + list.size());
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
//    sLogger.debug("UserEntityDao::update() - IN");
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
//      sLogger.debug("UserEntityDao::update() - Execute SQL: [" + sql + "]");
//      
//      PreparedStatement ps = conn.prepareStatement(sql);
//      ps.execute();
//    }
//    catch (SQLException e)
//    {
//      sLogger.debug("UserEntityDao::update() - Exception caught: ", e);
//    }
//    
//    dbPool.release(dbConn);
//    sLogger.debug("UserEntityDao::update() - OUT");
//  }
//
//  /**
//   * Save specified {@link UserEntity} to the data layer
//   * 
//   * @param t The object to be saved
//   */
//  public static void save(UserEntity t)
//  {
//    sLogger.debug("UserEntityDao::save() - IN");
//    
//    DbConnectionPool dbPool = DbConnectionPool.getInstance();
//    DbConnection dbConn = dbPool.allocate();
//    
//    Connection conn = dbConn.getConn();
//    try
//    {
//      String pswd = new String(Base64Utils.decode(t.getPassword()));
//      String sql = "INSERT INTO " + cKasTableName + " (user_name, user_description, user_password) " +
//        "VALUES ('" + t.getName() + "', '"+ t.getDescription() + "', '" + pswd + "');";
//      sLogger.debug("UserEntityDao::save() - Execute SQL: [" + sql + "]");
//      
//      PreparedStatement ps = conn.prepareStatement(sql);
//      ps.execute();
//    }
//    catch (SQLException e)
//    {
//      sLogger.debug("UserEntityDao::save() - Exception caught: ", e);
//    }
//    
//    dbPool.release(dbConn);
//    sLogger.debug("UserEntityDao::save() - OUT");
//  }
//
//  /**
//   * Delete the specified {@link UserEntity}
//   * 
//   * @param t The object to be deleted
//   */
//  public static void delete(UserEntity t)
//  {
//    sLogger.debug("UserEntityDao::delete() - IN");
//    
//    DbConnectionPool dbPool = DbConnectionPool.getInstance();
//    DbConnection dbConn = dbPool.allocate();
//    
//    Connection conn = dbConn.getConn();
//    try
//    {
//      String sql = "DELETE FROM " + cKasTableName + " WHERE user_id = " + t.getId() + ";";
//      sLogger.debug("UserEntityDao::delete() - Execute SQL: [" + sql + "]");
//      
//      PreparedStatement ps = conn.prepareStatement(sql);
//      ps.execute();
//    }
//    catch (SQLException e)
//    {
//      sLogger.debug("UserEntityDao::delete() - Exception caught: ", e);
//    }
//    
//    dbPool.release(dbConn);
//    sLogger.debug("UserEntityDao::delete() - OUT");
//  }
  
  /**
   * Extract a list of groups in which a user participates
   * 
   * @param conn The connection to be used to query the DB
   * @param id The {@link UserEntity user entity's} ID
   * @return a list of group IDs 
   * @throws SQLException
   */
  private static List<Integer> getUserGroups(Connection conn, int id) throws SQLException
  {
    sLogger.debug("UserEntityDao::getUserGroups() - IN");
    
    String sql = "SELECT group_id FROM " + cKasUsersToGroupsTableName + " WHERE user_id = " + id;
    ResultSet rs2 = DbUtils.execute(conn, sql);
    List<Integer> ugids = new ArrayList<Integer>();
    while (rs2.next())
    {
      int gid = rs2.getInt("group_id");
      ugids.add(gid);
    }
    rs2.close();
    
    sLogger.debug("UserEntityDao::getUserGroups() - OUT, List.size()=" + ugids.size());
    return ugids;
  }
  
  /**
   * Create a user entity based on the output of a query
   * 
   * @param rs The output of a query
   * @param conn An open Connection to the DB for further queries
   * @return a {@link UserEntity} 
   * @throws SQLException
   */
  private static UserEntity createUserEntity(ResultSet rs, Connection conn) throws SQLException
  {
    sLogger.debug("UserEntityDao::createUserEntity() - IN");
    
    int uid = rs.getInt("user_id");
    String uname = rs.getString("user_name");
    String udesc = rs.getString("user_description");
    String upass = rs.getString("user_password");
    
    List<Integer> ugids = getUserGroups(conn, uid);
    
    UserEntity ue = new UserEntity(uid, uname, udesc, upass, ugids);
    sLogger.debug("UserEntityDao::createUserEntity() - OUT, UserEntity=" + ue);
    return ue;
  }
}
