package com.kas.sec.entities;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
 * An implementation layer for {@link GroupEntity}
 * 
 * @author Pippo
 */
public class GroupEntityDao
{
  /**
   * Table name
   */
  private static final String cKasTableName = "kas_mq_groups";
  
  /**
   * Table columns and types
   */
  private static final Map<String, Class<?>> cTableColumns = new HashMap<String, Class<?>>();
  static
  {
    cTableColumns.put("group_id", int.class);
    cTableColumns.put("group_name", String.class);
    cTableColumns.put("group_description", String.class);
  }
  
  /**
   * Logger
   */
  private static ILogger sLogger = LoggerFactory.getLogger(GroupEntityDao.class);
  
  /**
   * Get a {@link GroupEntity} by its name
   * 
   * @param name The name of the {@link GroupEntity}
   * @return the {@link GroupEntity} with the specified name or {@code null} if not found
   */
  public static GroupEntity getByName(String name)
  {
    sLogger.debug("GroupEntityDao::getByName() - IN");
    GroupEntity ge = null;
    
    DbConnectionPool dbPool = DbConnectionPool.getInstance();
    DbConnection dbConn = dbPool.allocate();
    
    Connection conn = dbConn.getConn();
    try
    {
      String sql = "SELECT group_id, group_name, group_description FROM " + cKasTableName + " WHERE group_name = '" + name + "';";
      ResultSet rs = DbUtils.execute(conn, sql);
      if (rs.next()) ge = createGroupEntity(rs, conn);
      rs.close();
    }
    catch (SQLException e)
    {
      sLogger.debug("GroupEntityDao::getByName() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    sLogger.debug("GroupEntityDao::getByName() - OUT, Returns=" + ge);
    return ge;
  }
  
  /**
   * Get {@link GroupEntity} associated with the specific name
   * 
   * @param id The ID of the {@link GroupEntity}
   * @return The {@link GroupEntity} that matches the query
   */
  public static GroupEntity getById(int id)
  {
    sLogger.debug("GroupEntityDao::get() - IN");
    GroupEntity ge = null;
    
    DbConnectionPool dbPool = DbConnectionPool.getInstance();
    DbConnection dbConn = dbPool.allocate();
    
    Connection conn = dbConn.getConn();
    try
    {
      String sql = "SELECT group_id, group_name, group_description FROM " + cKasTableName + " WHERE group_id = " + id + ";";
      ResultSet rs = DbUtils.execute(conn, sql);
      if (rs.next()) ge = createGroupEntity(rs, conn);
      rs.close();
    }
    catch (SQLException e)
    {
      sLogger.debug("GroupEntityDao::get() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    sLogger.debug("GroupEntityDao::get() - OUT, Returns=" + ge);
    return ge;
  }
  
  /**
   * Get a list of {@link GroupEntity}s by a pattern
   * 
   * @param pattern The pattern that should be matched
   * @return a list of all {@link GroupEntity}s that their name matches {@code pattern}
   */
  public static List<GroupEntity> getByPattern(String pattern)
  {
    sLogger.debug("GroupEntityDao::getByPattern() - IN");
    List<GroupEntity> list = new ArrayList<GroupEntity>();
    
    DbConnectionPool dbPool = DbConnectionPool.getInstance();
    DbConnection dbConn = dbPool.allocate();
    Connection conn = dbConn.getConn();
    
    try
    {
      String sql = "SELECT group_id, group_name, group_description FROM " + cKasTableName + " WHERE group_name like '" + pattern + "%%';";
      ResultSet rs = DbUtils.execute(conn, sql);
      
      while (rs.next())
      {
        GroupEntity ge = createGroupEntity(rs, conn);
        list.add(ge);
      }
      rs.close();
    }
    catch (SQLException e)
    {
      sLogger.debug("GroupEntityDao::getByPattern() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    sLogger.debug("GroupEntityDao::getByPattern() - OUT, Returns=" + list.toString() + "; Size=" + list.size());
    return list;
  }

  /**
   * Get a list of all {@link GroupEntity} objects
   * 
   * @return a list of all {@link GroupEntity} objects
   */
  public static List<GroupEntity> getAll()
  {
    sLogger.debug("GroupEntityDao::getAll() - IN");
    List<GroupEntity> list = new ArrayList<GroupEntity>();
    
    DbConnectionPool dbPool = DbConnectionPool.getInstance();
    DbConnection dbConn = dbPool.allocate();
    
    Connection conn = dbConn.getConn();
    try
    {
      String sql = "SELECT group_id, group_name, group_description FROM " + cKasTableName + ';';
      ResultSet rs = DbUtils.execute(conn, sql);
      GroupEntity ge = null;
      while (rs.next())
      {
        ge = createGroupEntity(rs, conn);
        list.add(ge);
      }
      rs.close();
    }
    catch (SQLException e)
    {
      sLogger.debug("GroupEntityDao::getAll() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    sLogger.debug("GroupEntityDao::getAll() - OUT, Returns=" + list.toString() + "; Size=" + list.size());
    return list;
  }

//  /**
//   * Update values of {@code t} with the {@code map}
//   * 
//   * @param t The {@link GroupEntity} to update
//   * @param map Map of key-value pairs that indicate which fields should be updated with their new values
//   */
//  public static void update(GroupEntity t, Map<String, String> map)
//  {
//    sLogger.debug("GroupEntityDao::update() - IN");
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
//      sLogger.debug("GroupEntityDao::update() - Execute SQL: [" + sql + "]");
//      
//      PreparedStatement ps = conn.prepareStatement(sql);
//      ps.execute();
//    }
//    catch (SQLException e)
//    {
//      sLogger.debug("GroupEntityDao::update() - Exception caught: ", e);
//    }
//    
//    dbPool.release(dbConn);
//    sLogger.debug("GroupEntityDao::update() - OUT");
//  }
//
  /**
   * Save specified {@link GroupEntity} to the data layer
   * 
   * @param group The group name
   * @param desc The group description
   * @return The newly created group entity
   */
  public static GroupEntity create(String group, String desc)
  {
    sLogger.debug("GroupEntityDao::save() - IN");
    
    DbConnectionPool dbPool = DbConnectionPool.getInstance();
    DbConnection dbConn = dbPool.allocate();
    
    GroupEntity ge = null;
    
    Connection conn = dbConn.getConn();
    try
    {
      String sql = "INSERT INTO " + cKasTableName + " (group_name, group_description) " +
        "VALUES ('" + group + "', '"+ desc + "');";
      sLogger.debug("GroupEntityDao::save() - Execute SQL: [" + sql + "]");
      
      PreparedStatement ps = conn.prepareStatement(sql);
      ps.execute();
      
      ge = getByName(group);
    }
    catch (SQLException e)
    {
      sLogger.debug("GroupEntityDao::save() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    sLogger.debug("GroupEntityDao::save() - OUT, Result=" + ge);
    return ge;
  }

//  /**
//   * Delete the specified {@link GroupEntity}
//   * 
//   * @param t The object to be deleted
//   */
//  public static void delete(GroupEntity t)
//  {
//    sLogger.debug("GroupEntityDao::delete() - IN");
//    
//    DbConnectionPool dbPool = DbConnectionPool.getInstance();
//    DbConnection dbConn = dbPool.allocate();
//    
//    Connection conn = dbConn.getConn();
//    try
//    {
//      String sql = "DELETE FROM " + cKasTableName + " WHERE group_id = " + t.getId() + ";";
//      sLogger.debug("GroupEntityDao::delete() - Execute SQL: [" + sql + "]");
//      
//      PreparedStatement ps = conn.prepareStatement(sql);
//      ps.execute();
//    }
//    catch (SQLException e)
//    {
//      sLogger.debug("GroupEntityDao::delete() - Exception caught: ", e);
//    }
//    
//    dbPool.release(dbConn);
//    sLogger.debug("GroupEntityDao::delete() - OUT");
//  }
  
  /**
   * Create a group entity based on the output of a query
   * 
   * @param rs The output of a query
   * @param conn An open Connection to the DB for further queries
   * @return a {@link GroupEntity} 
   * @throws SQLException
   */
  private static GroupEntity createGroupEntity(ResultSet rs, Connection conn) throws SQLException
  {
    sLogger.debug("GroupEntityDao::createGroupEntity() - IN");
    
    int gid = rs.getInt("group_id");
    String gname = rs.getString("group_name");
    String gdesc = rs.getString("group_description");
    
    GroupEntity ge = new GroupEntity(gid, gname, gdesc);
    sLogger.debug("GroupEntityDao::createGroupEntity() - OUT, UserEntity=" + ge);
    return ge;
  }
}
