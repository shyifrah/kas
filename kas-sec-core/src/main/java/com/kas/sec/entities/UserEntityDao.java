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
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.IDao;
import com.kas.infra.utils.Base64Utils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;

/**
 * An implementation layer for {@link UserEntity}
 * 
 * @author Pippo
 */
public class UserEntityDao extends AKasObject implements IDao<UserEntity>
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
   * Get a {@link UserEntity} by its name
   * 
   * @param name The name of the {@link UserEntity}
   * @return the {@link UserEntity} with the specified name or {@code null} if not found
   */
  public UserEntity get(String name)
  {
    mLogger.debug("UserDao::get() - IN");
    UserEntity ue = null;
    
    DbConnectionPool dbPool = DbConnectionPool.getInstance();
    DbConnection dbConn = dbPool.allocate();
    Connection conn = dbConn.getConn();
    
    try
    {
      String sql = "SELECT id, name, description, password FROM " + cKasTableName + " WHERE name = '" + name + "';";
      ResultSet rs = DbUtils.execute(conn, sql);
      if (rs.next()) ue = createUserEntity(rs, conn);
      rs.close();
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
   * Get {@link UserEntity} associated with the specific name
   * 
   * @param id The ID of the {@link UserEntity}
   * @return The {@link UserEntity} that matches the query
   */
  public UserEntity get(int id)
  {
    mLogger.debug("UserDao::get() - IN");
    UserEntity ue = null;
    
    DbConnectionPool dbPool = DbConnectionPool.getInstance();
    DbConnection dbConn = dbPool.allocate();
    Connection conn = dbConn.getConn();
    
    try
    {
      String sql = "SELECT id, name, description, password FROM " + cKasTableName + " WHERE id = " + id + ";";
      ResultSet rs = DbUtils.execute(conn, sql);
      if (rs.next()) ue = createUserEntity(rs, conn);
      rs.close();
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
   * Get a list of all {@link UserEntity} objects
   * 
   * @return a list of all {@link UserEntity} objects
   */
  public List<UserEntity> getAll()
  {
    mLogger.debug("UserDao::getAll() - IN");
    List<UserEntity> list = new ArrayList<UserEntity>();
    
    DbConnectionPool dbPool = DbConnectionPool.getInstance();
    DbConnection dbConn = dbPool.allocate();
    
    Connection conn = dbConn.getConn();
    try
    {
      String sql = "SELECT id, name, description, password FROM " + cKasTableName + ';';
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
      mLogger.debug("UserDao::getAll() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    mLogger.debug("UserDao::getAll() - OUT, Returns=" + list.toString() + "; Size=" + list.size());
    return list;
  }
  
  /**
   * Update values of {@code t} with the {@code map}
   * 
   * @param t The {@link UserEntity} to update
   * @param map Map of key-value pairs that indicate which fields should be updated with their new values
   */
  public void update(UserEntity t, Map<String, String> map)
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
   * Save specified {@link UserEntity} to the data layer
   * 
   * @param t The object to be saved
   */
  public void save(UserEntity t)
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
   * Delete the specified {@link UserEntity}
   * 
   * @param t The object to be deleted
   */
  public void delete(UserEntity t)
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
   * Extract a list of groups in which a user participates
   * 
   * @param conn The connection to be used to query the DB
   * @param id The {@link UserEntity user entity's} ID
   * @return a list of group IDs 
   * @throws SQLException
   */
  private List<Integer> getUserGroups(Connection conn, int id) throws SQLException
  {
    mLogger.debug("UserDao::getUserGroups() - IN");
    
    String sql = "SELECT group_id FROM " + cKasUsersToGroupsTableName + " WHERE user_id = " + id;
    ResultSet rs2 = DbUtils.execute(conn, sql);
    List<Integer> ugids = new ArrayList<Integer>();
    while (rs2.next())
    {
      int gid = rs2.getInt("group_id");
      ugids.add(gid);
    }
    rs2.close();
    
    mLogger.debug("UserDao::getUserGroups() - OUT, List.size()=" + ugids.size());
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
  private UserEntity createUserEntity(ResultSet rs, Connection conn) throws SQLException
  {
    mLogger.debug("UserDao::createUserEntity() - IN");
    
    int uid = rs.getInt("id");
    String uname = rs.getString("name");
    String udesc = rs.getString("description");
    String upass = rs.getString("password");
    
    List<Integer> ugids = getUserGroups(conn, uid);
    
    UserEntity ue = new UserEntity(uid, uname, udesc, upass, ugids);
    mLogger.debug("UserDao::createUserEntity() - OUT, UserEntity=" + ue.toString());
    return ue;
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
