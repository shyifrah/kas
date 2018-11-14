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
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.IDao;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;

/**
 * An implementation layer for {@link GroupEntity}
 * 
 * @author Pippo
 */
public class GroupEntityDao extends AKasObject implements IDao<GroupEntity>
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
  private ILogger mLogger = LoggerFactory.getLogger(this.getClass());
  
  /**
   * Get a {@link GroupEntity} by its name
   * 
   * @param name The name of the {@link GroupEntity}
   * @return the {@link GroupEntity} with the specified name or {@code null} if not found
   */
  public GroupEntity get(String name)
  {
    mLogger.debug("GroupDao::get() - IN");
    GroupEntity ge = null;
    
    DbConnectionPool dbPool = DbConnectionPool.getInstance();
    DbConnection dbConn = dbPool.allocate();
    
    Connection conn = dbConn.getConn();
    try
    {
      String sql = "SELECT group_id, group_name, group_description FROM " + cKasTableName + " WHERE group_name = '" + name + "';";
      PreparedStatement ps = conn.prepareStatement(sql);
      
      mLogger.debug("GroupDao::get() - Execute SQL: [" + sql + "]");
      ResultSet rs = ps.executeQuery();
      
      if (rs.next())
      {
        int id = rs.getInt("group_id");
        String desc = rs.getString("group_description");
        ge = new GroupEntity(id, name, desc);
      }
    }
    catch (SQLException e)
    {
      mLogger.debug("GroupDao::get() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    mLogger.debug("GroupDao::get() - OUT, Returns=" + ge.toString());
    return ge;
  }
  
  /**
   * Get {@link GroupEntity} associated with the specific name
   * 
   * @param id The ID of the {@link GroupEntity}
   * @return The {@link GroupEntity} that matches the query
   */
  public GroupEntity get(int id)
  {
    mLogger.debug("GroupDao::get() - IN");
    GroupEntity ge = null;
    
    DbConnectionPool dbPool = DbConnectionPool.getInstance();
    DbConnection dbConn = dbPool.allocate();
    
    Connection conn = dbConn.getConn();
    try
    {
      String sql = "SELECT group_id, group_name, group_description FROM " + cKasTableName + " WHERE group_id = " + id + ";";
      PreparedStatement ps = conn.prepareStatement(sql);
      
      mLogger.debug("GroupDao::get() - Execute SQL: [" + sql + "]");
      ResultSet rs = ps.executeQuery();
      
      if (rs.next())
      {
        String name = rs.getString("group_name");
        String desc = rs.getString("group_description");
        ge = new GroupEntity(id, name, desc);
      }
    }
    catch (SQLException e)
    {
      mLogger.debug("GroupDao::get() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    mLogger.debug("GroupDao::get() - OUT, Returns=" + ge.toString());
    return ge;
  }

  /**
   * Get a list of all {@link GroupEntity} objects
   * 
   * @return a list of all {@link GroupEntity} objects
   */
  public List<GroupEntity> getAll()
  {
    mLogger.debug("GroupDao::getAll() - IN");
    List<GroupEntity> list = new ArrayList<GroupEntity>();
    
    DbConnectionPool dbPool = DbConnectionPool.getInstance();
    DbConnection dbConn = dbPool.allocate();
    
    Connection conn = dbConn.getConn();
    try
    {
      String sql = "SELECT group_id, group_name, group_description FROM " + cKasTableName + ';';
      PreparedStatement ps = conn.prepareStatement(sql);
      
      mLogger.debug("GroupDao::getAll() - Execute SQL: [" + sql + "]");
      ResultSet rs = ps.executeQuery();
    
      GroupEntity ge = null;
      while (rs.next())
      {
        int id = rs.getInt("group_id");
        String name = rs.getString("group_name");
        String desc = rs.getString("group_description");
        ge = new GroupEntity(id, name, desc);
        list.add(ge);
      }
    }
    catch (SQLException e)
    {
      mLogger.debug("GroupDao::getAll() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    mLogger.debug("GroupDao::getAll() - OUT, Returns=" + list.toString() + "; Size=" + list.size());
    return list;
  }

  /**
   * Update values of {@code t} with the {@code map}
   * 
   * @param t The {@link GroupEntity} to update
   * @param map Map of key-value pairs that indicate which fields should be updated with their new values
   */
  public void update(GroupEntity t, Map<String, String> map)
  {
    mLogger.debug("GroupDao::update() - IN");
    
    DbConnectionPool dbPool = DbConnectionPool.getInstance();
    DbConnection dbConn = dbPool.allocate();
    
    Connection conn = dbConn.getConn();
    try
    {
      StringBuilder sb = new StringBuilder();
      sb.append("UPDATE ")
        .append(cKasTableName)
        .append(" SET ");
      
      for (Map.Entry<String, String> entry : map.entrySet())
      {
        String col = entry.getKey().toString();
        String val = entry.getValue().toString();
        
        sb.append(col);
        Class<?> cls = cTableColumns.get(col);
        if (String.class.equals(cls))
          sb.append("='").append(val).append("',");
        else
          sb.append("=").append(val).append(",");
      }
      
      String sql = sb.toString();
      sql = sql.substring(0, sql.length()-1);
      mLogger.debug("GroupDao::update() - Execute SQL: [" + sql + "]");
      
      PreparedStatement ps = conn.prepareStatement(sql);
      ps.execute();
    }
    catch (SQLException e)
    {
      mLogger.debug("GroupDao::update() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    mLogger.debug("GroupDao::update() - OUT");
  }

  /**
   * Save specified {@link GroupEntity} to the data layer
   * 
   * @param t The object to be saved
   */
  public void save(GroupEntity t)
  {
    mLogger.debug("GroupDao::save() - IN");
    
    DbConnectionPool dbPool = DbConnectionPool.getInstance();
    DbConnection dbConn = dbPool.allocate();
    
    Connection conn = dbConn.getConn();
    try
    {
      String sql = "INSERT INTO " + cKasTableName + " (group_name, group_description) " +
        "VALUES ('" + t.getName() + "', '"+ t.getDescription() + "');";
      mLogger.debug("GroupDao::save() - Execute SQL: [" + sql + "]");
      
      PreparedStatement ps = conn.prepareStatement(sql);
      ps.execute();
    }
    catch (SQLException e)
    {
      mLogger.debug("GroupDao::save() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    mLogger.debug("GroupDao::save() - OUT");
  }

  /**
   * Delete the specified {@link GroupEntity}
   * 
   * @param t The object to be deleted
   */
  public void delete(GroupEntity t)
  {
    mLogger.debug("GroupDao::delete() - IN");
    
    DbConnectionPool dbPool = DbConnectionPool.getInstance();
    DbConnection dbConn = dbPool.allocate();
    
    Connection conn = dbConn.getConn();
    try
    {
      String sql = "DELETE FROM " + cKasTableName + " WHERE group_id = " + t.getId() + ";";
      mLogger.debug("GroupDao::delete() - Execute SQL: [" + sql + "]");
      
      PreparedStatement ps = conn.prepareStatement(sql);
      ps.execute();
    }
    catch (SQLException e)
    {
      mLogger.debug("GroupDao::delete() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    mLogger.debug("GroupDao::delete() - OUT");
  }
  
  /**
   * Get the object's detailed string representation
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public String toPrintableString(int level)
  {
    return toString();
  }
}
