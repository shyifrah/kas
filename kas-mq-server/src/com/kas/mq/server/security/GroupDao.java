package com.kas.mq.server.security;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.kas.infra.base.IObject;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.server.db.DbConnection;
import com.kas.mq.server.db.DbConnectionPool;

/**
 * An implementation layer for {@link GroupEntity}
 * 
 * @author Pippo
 */
public class GroupDao implements IGroupDao
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
    cTableColumns.put("id", int.class);
    cTableColumns.put("name", String.class);
    cTableColumns.put("description", String.class);
  }
  
  /**
   * Logger
   */
  private ILogger mLogger = LoggerFactory.getLogger(this.getClass());
  
  /**
   * Get a {@link IGroupEntity} by its name
   * 
   * @param name The name of the {@link IGroupEntity}
   * @return the {@link IGroupEntity} with the specified name or {@code null} if not found
   */
  public IGroupEntity get(String name)
  {
    mLogger.debug("GroupDao::get() - IN");
    GroupEntity ge = null;
    
    DbConnectionPool dbPool = DbConnectionPool.getInstance();
    DbConnection dbConn = dbPool.allocate();
    
    Connection conn = dbConn.getConn();
    try
    {
      String sql = "SELECT id, name, description FROM " + cKasTableName + " WHERE name = '" + name + "';";
      PreparedStatement ps = conn.prepareStatement(sql);
      
      mLogger.debug("GroupDao::get() - Execute SQL: [" + sql + "]");
      ResultSet rs = ps.executeQuery();
      
      if (rs.next())
      {
        int id = rs.getInt("id");
        String desc = rs.getString("description");
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
   * Get {@link IGroupEntity} associated with the specific name
   * 
   * @param name The group's name
   * @return The {@link IGroupEntity} that matches the query
   */
  public IGroupEntity get(int id)
  {
    mLogger.debug("GroupDao::get() - IN");
    GroupEntity ge = null;
    
    DbConnectionPool dbPool = DbConnectionPool.getInstance();
    DbConnection dbConn = dbPool.allocate();
    
    Connection conn = dbConn.getConn();
    try
    {
      String sql = "SELECT id, name, description FROM " + cKasTableName + " WHERE id = " + id + ";";
      PreparedStatement ps = conn.prepareStatement(sql);
      
      mLogger.debug("GroupDao::get() - Execute SQL: [" + sql + "]");
      ResultSet rs = ps.executeQuery();
      
      if (rs.next())
      {
        String name = rs.getString("name");
        String desc = rs.getString("description");
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
   * Get a list of all {@link IGroupEntity} objects
   * 
   * @return a list of all {@link IGroupEntity} objects
   */
  public List<IGroupEntity> getAll()
  {
    mLogger.debug("GroupDao::getAll() - IN");
    List<IGroupEntity> list = new ArrayList<IGroupEntity>();
    
    DbConnectionPool dbPool = DbConnectionPool.getInstance();
    DbConnection dbConn = dbPool.allocate();
    
    Connection conn = dbConn.getConn();
    try
    {
      String sql = "SELECT id, name, description FROM " + cKasTableName + ';';
      PreparedStatement ps = conn.prepareStatement(sql);
      
      mLogger.debug("GroupDao::getAll() - Execute SQL: [" + sql + "]");
      ResultSet rs = ps.executeQuery();
    
      GroupEntity ge = null;
      while (rs.next())
      {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String desc = rs.getString("description");
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
  public void update(IGroupEntity t, Map<String, String> map)
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
  public void save(IGroupEntity t)
  {
    mLogger.debug("GroupDao::save() - IN");
    
    DbConnectionPool dbPool = DbConnectionPool.getInstance();
    DbConnection dbConn = dbPool.allocate();
    
    Connection conn = dbConn.getConn();
    try
    {
      String sql = "INSERT INTO " + cKasTableName + " (id, name, description) " +
        "VALUES (" + t.getId() + ", '" + t.getName() + "', '"+ t.getDescription() + "');";
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
  public void delete(IGroupEntity t)
  {
    mLogger.debug("GroupDao::delete() - IN");
    
    DbConnectionPool dbPool = DbConnectionPool.getInstance();
    DbConnection dbConn = dbPool.allocate();
    
    Connection conn = dbConn.getConn();
    try
    {
      String sql = "DELETE FROM " + cKasTableName + " WHERE ID = " + t.getId() + ";";
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
   * Returns the {@link IObject} simple class name enclosed with chevrons.
   * 
   * @return class name enclosed with chevrons.
   * 
   * @see com.kas.infra.base.IObject#name()
   */
  public String name()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("<")
      .append(this.getClass().getSimpleName())
      .append(">");
    return sb.toString();
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
