package com.kas.sec.resources;

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
import com.kas.sec.entities.GroupEntity;

public class ResourceClassDao extends AKasObject implements IDao<ResourceClass>
{
  /**
   * Table name
   */
  private static final String cKasTableName = "kas_mq_resource_classes";
  
  /**
   * Table columns and types
   */
  private static final Map<String, Class<?>> cTableColumns = new HashMap<String, Class<?>>();
  static
  {
    cTableColumns.put("id", int.class);
    cTableColumns.put("name", String.class);
    cTableColumns.put("access_levels", int.class);
  }
  
  /**
   * Logger
   */
  private ILogger mLogger = LoggerFactory.getLogger(this.getClass());
  
  /**
   * Get a {@link IResourceClass} by its name
   * 
   * @param name The name of the {@link IResourceClass}
   * @return the {@link IResourceClass} with the specified name or {@code null} if not found
   */
  public ResourceClass get(String name)
  {
    mLogger.debug("ResourceClassDao::get() - IN");
    ResourceClass rc = null;
    
    DbConnectionPool dbPool = DbConnectionPool.getInstance();
    DbConnection dbConn = dbPool.allocate();
    
    Connection conn = dbConn.getConn();
    try
    {
      String sql = "SELECT id, name, access_levels FROM " + cKasTableName + " WHERE name = '" + name + "';";
      PreparedStatement ps = conn.prepareStatement(sql);
      
      mLogger.debug("ResourceClassDao::get() - Execute SQL: [" + sql + "]");
      ResultSet rs = ps.executeQuery();
      
      if (rs.next())
      {
        int id = rs.getInt("id");
        int accLevels = rs.getInt("access_levels");
        rc = new ResourceClass(id, name, accLevels);
      }
    }
    catch (SQLException e)
    {
      mLogger.debug("ResourceClassDao::get() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    mLogger.debug("ResourceClassDao::get() - OUT, Returns=" + rc.toString());
    return rc;
  }
  
  /**
   * Get {@link IResourceClass} associated with the specific ID
   * 
   * @param name The resource class name
   * @return The {@link IResourceClass} that matches the query
   */
  public ResourceClass get(int id)
  {
    mLogger.debug("ResourceClassDao::get() - IN");
    ResourceClass rc = null;
    
    DbConnectionPool dbPool = DbConnectionPool.getInstance();
    DbConnection dbConn = dbPool.allocate();
    
    Connection conn = dbConn.getConn();
    try
    {
      String sql = "SELECT id, name, access_levels FROM " + cKasTableName + " WHERE id = " + id + ";";
      PreparedStatement ps = conn.prepareStatement(sql);
      
      mLogger.debug("ResourceClassDao::get() - Execute SQL: [" + sql + "]");
      ResultSet rs = ps.executeQuery();
      
      if (rs.next())
      {
        String name = rs.getString("name");
        int accLevels = rs.getInt("access_levels");
        rc = new ResourceClass(id, name, accLevels);
      }
    }
    catch (SQLException e)
    {
      mLogger.debug("ResourceClassDao::get() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    mLogger.debug("ResourceClassDao::get() - OUT, Returns=" + rc.toString());
    return rc;
  }

  /**
   * Get a list of all {@link IResourceClass} objects
   * 
   * @return a list of all {@link IResourceClass} objects
   */
  public List<ResourceClass> getAll()
  {
    mLogger.debug("ResourceClassDao::getAll() - IN");
    List<ResourceClass> list = new ArrayList<ResourceClass>();
    
    DbConnectionPool dbPool = DbConnectionPool.getInstance();
    DbConnection dbConn = dbPool.allocate();
    
    Connection conn = dbConn.getConn();
    try
    {
      String sql = "SELECT id, name, access_levels FROM " + cKasTableName + ';';
      PreparedStatement ps = conn.prepareStatement(sql);
      
      mLogger.debug("ResourceClassDao::getAll() - Execute SQL: [" + sql + "]");
      ResultSet rs = ps.executeQuery();
    
      ResourceClass rc = null;
      while (rs.next())
      {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        int accLevels = rs.getInt("access_levels");
        rc = new ResourceClass(id, name, accLevels);
        list.add(rc);
      }
    }
    catch (SQLException e)
    {
      mLogger.debug("ResourceClassDao::getAll() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    mLogger.debug("ResourceClassDao::getAll() - OUT, Returns=" + list.toString() + "; Size=" + list.size());
    return list;
  }
  
  /**
   * Update values of {@code t} with the {@code map}
   * 
   * @param t The {@link IResourceClass} to update
   * @param map Map of key-value pairs that indicate which fields should be updated with their new values
   */
  public void update(ResourceClass t, Map<String, String> map)
  {
    mLogger.debug("ResourceClassDao::update() - IN");
    
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
      mLogger.debug("ResourceClassDao::update() - Execute SQL: [" + sql + "]");
      
      PreparedStatement ps = conn.prepareStatement(sql);
      ps.execute();
    }
    catch (SQLException e)
    {
      mLogger.debug("ResourceClassDao::update() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    mLogger.debug("ResourceClassDao::update() - OUT");
  }

  /**
   * Save specified {@link IResourceClass} to the data layer
   * 
   * @param t The object to be saved
   */
  public void save(ResourceClass t)
  {
    mLogger.debug("ResourceClassDao::save() - IN");
    
    DbConnectionPool dbPool = DbConnectionPool.getInstance();
    DbConnection dbConn = dbPool.allocate();
    
    Connection conn = dbConn.getConn();
    try
    {
      String sql = "INSERT INTO " + cKasTableName + " (name, access_levels) " +
        "VALUES ('" + t.getName() + "', "+ t.getEnabledAccessLevels() + ");";
      mLogger.debug("ResourceClassDao::save() - Execute SQL: [" + sql + "]");
      
      PreparedStatement ps = conn.prepareStatement(sql);
      ps.execute();
    }
    catch (SQLException e)
    {
      mLogger.debug("ResourceClassDao::save() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    mLogger.debug("ResourceClassDao::save() - OUT");
  }

  /**
   * Delete the specified {@link GroupEntity}
   * 
   * @param t The object to be deleted
   */
  public void delete(ResourceClass t)
  {
    mLogger.debug("ResourceClassDao::delete() - IN");
    
    DbConnectionPool dbPool = DbConnectionPool.getInstance();
    DbConnection dbConn = dbPool.allocate();
    
    Connection conn = dbConn.getConn();
    try
    {
      String sql = "DELETE FROM " + cKasTableName + " WHERE ID = " + t.getId() + ";";
      mLogger.debug("ResourceClassDao::delete() - Execute SQL: [" + sql + "]");
      
      PreparedStatement ps = conn.prepareStatement(sql);
      ps.execute();
    }
    catch (SQLException e)
    {
      mLogger.debug("ResourceClassDao::delete() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    mLogger.debug("ResourceClassDao::delete() - OUT");
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
