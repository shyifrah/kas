package com.kas.mq.server.security;

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
import com.kas.infra.utils.Base64Utils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.sec.entities.IUserDao;
import com.kas.sec.entities.IUserEntity;
import com.kas.sec.entities.UserEntity;

/**
 * An implementation layer for {@link UserEntity}
 * 
 * @author Pippo
 */
public class UserDao extends AKasObject implements IUserDao
{
  /**
   * Table name
   */
  private static final String cKasTableName = "kas_mq_users";
  
  /**
   * Table columns and types
   */
  private static final Map<String, Class<?>> cTableColumns = new HashMap<String, Class<?>>();
  static
  {
    cTableColumns.put("id", int.class);
    cTableColumns.put("name", String.class);
    cTableColumns.put("description", String.class);
    cTableColumns.put("password", String.class);
  }
  
  /**
   * Logger
   */
  private ILogger mLogger = LoggerFactory.getLogger(this.getClass());
  
  /**
   * Get a {@link IUserEntity} by its name
   * 
   * @param name The name of the {@link IUserEntity}
   * @return the {@link IUserEntity} with the specified name or {@code null} if not found
   */
  public IUserEntity get(String name)
  {
    mLogger.debug("UserDao::get() - IN");
    IUserEntity ue = null;
    
    DbConnectionPool dbPool = DbConnectionPool.getInstance();
    DbConnection dbConn = dbPool.allocate();
    
    Connection conn = dbConn.getConn();
    try
    {
      String sql = "SELECT id, name, description, password FROM " + cKasTableName + " WHERE name = '" + name + "';";
      PreparedStatement ps = conn.prepareStatement(sql);
      
      mLogger.debug("UserDao::get() - Execute SQL: [" + sql + "]");
      ResultSet rs = ps.executeQuery();
      
      if (rs.next())
      {
        int id = rs.getInt("id");
        String desc = rs.getString("description");
        String pswd = rs.getString("password");
        ue = new UserEntity(id, name, desc, pswd);
      }
    }
    catch (SQLException e)
    {
      mLogger.debug("UserDao::get() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    mLogger.debug("UserDao::get() - OUT, Returns=" + ue.toString());
    return ue;
  }
  
  /**
   * Get {@link IUserEntity} associated with the specific name
   * 
   * @param id The ID of the {@link IUserEntity}
   * @return The {@link IUserEntity} that matches the query
   */
  public IUserEntity get(int id)
  {
    mLogger.debug("UserDao::get() - IN");
    IUserEntity ue = null;
    
    DbConnectionPool dbPool = DbConnectionPool.getInstance();
    DbConnection dbConn = dbPool.allocate();
    
    Connection conn = dbConn.getConn();
    try
    {
      String sql = "SELECT id, name, description, password FROM " + cKasTableName + " WHERE id = " + id + ";";
      PreparedStatement ps = conn.prepareStatement(sql);
      
      mLogger.debug("UserDao::get() - Execute SQL: [" + sql + "]");
      ResultSet rs = ps.executeQuery();
      
      if (rs.next())
      {
        String name = rs.getString("name");
        String desc = rs.getString("description");
        String pswd = rs.getString("password");
        ue = new UserEntity(id, name, desc, pswd);
      }
    }
    catch (SQLException e)
    {
      mLogger.debug("UserDao::get() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    mLogger.debug("UserDao::get() - OUT, Returns=" + ue.toString());
    return ue;
  }

  /**
   * Get a list of all {@link IUserEntity} objects
   * 
   * @return a list of all {@link IUserEntity} objects
   */
  public List<IUserEntity> getAll()
  {
    mLogger.debug("UserDao::getAll() - IN");
    List<IUserEntity> list = new ArrayList<IUserEntity>();
    
    DbConnectionPool dbPool = DbConnectionPool.getInstance();
    DbConnection dbConn = dbPool.allocate();
    
    Connection conn = dbConn.getConn();
    try
    {
      String sql = "SELECT id, name, description, password FROM " + cKasTableName + ';';
      PreparedStatement ps = conn.prepareStatement(sql);
      
      mLogger.debug("UserDao::getAll() - Execute SQL: [" + sql + "]");
      ResultSet rs = ps.executeQuery();
    
      IUserEntity ue = null;
      while (rs.next())
      {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String desc = rs.getString("description");
        String pswd = rs.getString("password");
        ue = new UserEntity(id, name, desc, pswd);
        list.add(ue);
      }
    }
    catch (SQLException e)
    {
      mLogger.debug("UserDao::getAll() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    mLogger.debug("UserDao::getAll() - OUT, Returns=" + list.toString() + "; Size=" + list.size());
    return list;
  }
  
  /**
   * Update values of {@code t} with the {@code map}
   * 
   * @param t The {@link IUserEntity} to update
   * @param map Map of key-value pairs that indicate which fields should be updated with their new values
   */
  public void update(IUserEntity t, Map<String, String> map)
  {
    mLogger.debug("UserDao::update() - IN");
    
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
      mLogger.debug("UserDao::update() - Execute SQL: [" + sql + "]");
      
      PreparedStatement ps = conn.prepareStatement(sql);
      ps.execute();
    }
    catch (SQLException e)
    {
      mLogger.debug("UserDao::update() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    mLogger.debug("UserDao::update() - OUT");
  }

  /**
   * Save specified {@link IUserEntity} to the data layer
   * 
   * @param t The object to be saved
   */
  public void save(IUserEntity t)
  {
    mLogger.debug("UserDao::save() - IN");
    
    DbConnectionPool dbPool = DbConnectionPool.getInstance();
    DbConnection dbConn = dbPool.allocate();
    
    Connection conn = dbConn.getConn();
    try
    {
      String pswd = new String(Base64Utils.decode(t.getPassword()));
      String sql = "INSERT INTO " + cKasTableName + " (name, description, password) " +
        "VALUES ('" + t.getName() + "', '"+ t.getDescription() + "', '" + pswd + "');";
      mLogger.debug("UserDao::save() - Execute SQL: [" + sql + "]");
      
      PreparedStatement ps = conn.prepareStatement(sql);
      ps.execute();
    }
    catch (SQLException e)
    {
      mLogger.debug("UserDao::save() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    mLogger.debug("UserDao::save() - OUT");
  }

  /**
   * Delete the specified {@link IUserEntity}
   * 
   * @param t The object to be deleted
   */
  public void delete(IUserEntity t)
  {
    mLogger.debug("UserDao::delete() - IN");
    
    DbConnectionPool dbPool = DbConnectionPool.getInstance();
    DbConnection dbConn = dbPool.allocate();
    
    Connection conn = dbConn.getConn();
    try
    {
      String sql = "DELETE FROM " + cKasTableName + " WHERE ID = " + t.getId() + ";";
      mLogger.debug("UserDao::delete() - Execute SQL: [" + sql + "]");
      
      PreparedStatement ps = conn.prepareStatement(sql);
      ps.execute();
    }
    catch (SQLException e)
    {
      mLogger.debug("UserDao::delete() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    mLogger.debug("UserDao::delete() - OUT");
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
