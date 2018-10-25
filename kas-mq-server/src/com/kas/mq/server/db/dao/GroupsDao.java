package com.kas.mq.server.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.kas.infra.base.IDao;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.server.db.DbConnection;
import com.kas.mq.server.db.DbConnectionPool;
import com.kas.sec.entities.GroupEntity;

public class GroupsDao
{
  private static final String cKasGroupsTableName = "kas_mq_groups";
  private static final Set<String> cTableStringColumns = new HashSet<String>();
  static
  {
    cTableStringColumns.add("name");
    cTableStringColumns.add("description");
  }
  
  /**
   * Logger
   */
  private ILogger mLogger = LoggerFactory.getLogger(this.getClass());
  
  /**
   * Get {@link GroupEntity} associated with the specific name
   * 
   * @param name The group's name
   * @return The {@link GroupEntity} that matches the query
   */
  public GroupEntity get(String name)
  {
    mLogger.debug("GroupsDao::get() - IN");
    GroupEntity ge = null;
    
    DbConnectionPool dbPool = DbConnectionPool.getPool();
    DbConnection dbConn = dbPool.allocate();
    
    Connection conn = dbConn.getConnection();
    try
    {
      String sql = "SELECT name, description FROM " + cKasGroupsTableName + " WHERE name = '" + name + "';";
      PreparedStatement ps = conn.prepareStatement(sql);
      ResultSet rs = ps.executeQuery();
      
      if (rs.next())
      {
        String desc = rs.getString("description");
        ge = new GroupEntity(name, desc);
      }
    }
    catch (SQLException e)
    {
      mLogger.debug("GroupsDao::get() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    mLogger.debug("GroupsDao::get() - OUT, Returns=" + ge.toString());
    return ge;
  }

  /**
   * Get a list of all {@link GroupEntity} objects
   * 
   * @return a list of all {@link GroupEntity} objects
   */
  public List<GroupEntity> getAll()
  {
    mLogger.debug("GroupsDao::getAll() - IN");
    List<GroupEntity> list = new ArrayList<GroupEntity>();
    
    DbConnectionPool dbPool = DbConnectionPool.getPool();
    DbConnection dbConn = dbPool.allocate();
    
    Connection conn = dbConn.getConnection();
    try
    {
      String sql = "SELECT name, description FROM " + cKasGroupsTableName + ';';
      PreparedStatement ps = conn.prepareStatement(sql);
      ResultSet rs = ps.executeQuery();
    
      GroupEntity ge = null;
      while (rs.next())
      {
        String name = rs.getString("name");
        String desc = rs.getString("description");
        ge = new GroupEntity(name, desc);
        list.add(ge);
      }
    }
    catch (SQLException e)
    {
      mLogger.debug("GroupsDao::getAll() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    mLogger.debug("GroupsDao::getAll() - OUT, Returns=" + list.toString() + "; Size=" + list.size());
    return list;
  }

  /**
   * Insert the {@link GroupEntity} to the table
   * 
   * @param t The {@link GroupEntity} to be inserted
   */
  public void insert(GroupEntity t)
  {
    mLogger.debug("GroupsDao::insert() - IN");
    
    DbConnectionPool dbPool = DbConnectionPool.getPool();
    DbConnection dbConn = dbPool.allocate();
    
    Connection conn = dbConn.getConnection();
    try
    {
      String sql = "INSERT INTO " + cKasGroupsTableName + " (name, description) VALUES('" + t.getName() + "', '" + t.getDescription() + "';";
      PreparedStatement ps = conn.prepareStatement(sql);
      ps.execute();
    }
    catch (SQLException e)
    {
      mLogger.debug("GroupsDao::insert() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    mLogger.debug("GroupsDao::insert() - OUT");
  }

  /**
   * Update values of {@code t} with the {@code map}
   * 
   * @param t The {@link GroupEntity} to update
   * @param map Map of key-value pairs that indicate which fields should be updated with their new values
   */
  public void update(GroupEntity t, Map<Object, Object> map)
  {
    mLogger.debug("GroupsDao::update() - IN");
    
    DbConnectionPool dbPool = DbConnectionPool.getPool();
    DbConnection dbConn = dbPool.allocate();
    
    Connection conn = dbConn.getConnection();
    try
    {
      StringBuilder sb = new StringBuilder();
      sb.append("UPDATE ")
        .append(cKasGroupsTableName)
        .append(" SET ");
      
      for (Map.Entry<Object, Object> entry : map.entrySet())
      {
        String col = entry.getKey().toString();
        String val = entry.getValue().toString();
        
        sb.append(col);
        if (cTableStringColumns.contains(col))
          sb.append("='").append(val).append("',");
        else
          sb.append("=").append(val).append(",");
      }
      
      String sql = sb.toString();
      sql = sql.substring(0, sql.length()-1);
      
      PreparedStatement ps = conn.prepareStatement(sql);
      ps.execute();
    }
    catch (SQLException e)
    {
      mLogger.debug("GroupsDao::update() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    mLogger.debug("GroupsDao::update() - OUT");
  }

  @Override
  public void delete(GroupEntity t)
  {
  }
  
  @Override
  public String name()
  {
    return null;
  }

  @Override
  public String toPrintableString(int level)
  {
    return null;
  }  
}
