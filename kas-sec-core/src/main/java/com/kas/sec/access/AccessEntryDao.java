package com.kas.sec.access;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.kas.db.DbConnection;
import com.kas.db.DbConnectionPool;
import com.kas.db.DbUtils;
import com.kas.infra.base.AKasObject;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;

public class AccessEntryDao extends AKasObject
{
  /**
   * Table columns and types
   */
  private static final Map<String, Class<?>> cTableColumns = new HashMap<String, Class<?>>();
  static
  {
    cTableColumns.put("pattern", String.class);
    cTableColumns.put("group_id", int.class);
    cTableColumns.put("access_level", int.class);
  }
  
  /**
   * Logger
   */
  private ILogger mLogger = LoggerFactory.getLogger(this.getClass());
  
  /**
   * Name of table that holds ACEs data
   */
  private String mTableName;
  
  /**
   * Construct the the DAO object
   * 
   * @param resTypeName Name of the resource
   */
  AccessEntryDao(String resTypeName)
  {
    mTableName = "kas_mq_" + resTypeName.toLowerCase() + "_permissions";
  }
  
  /**
   * Get a {@link AccessEntry} by its name
   * 
   * @param name The name of the {@link AccessEntry}
   * @return the {@link AccessEntry} with the specified name or {@code null} if not found
   */
  public AccessEntry get(String name)
  {
    mLogger.debug("AccessEntryDao::get() - IN/OUT");
    throw new RuntimeException("AccessEntryDao.get(String) not supported");
  }
  
  /**
   * Get {@link AccessEntry} associated with the specific name
   * 
   * @param id The ID of the {@link AccessEntry}
   * @return The {@link AccessEntry} that matches the query
   */
  public AccessEntry get(int id)
  {
    mLogger.debug("AccessEntryDao::get() - IN/OUT");
    throw new RuntimeException("AccessEntryDao.get(int) not supported");
  }

  /**
   * Get a list of all {@link AccessEntry} objects
   * 
   * @return a list of all {@link AccessEntry} objects
   */
  public List<AccessEntry> getAll()
  {
    mLogger.debug("AccessEntryDao::getAll() - IN");
    List<AccessEntry> list = new ArrayList<AccessEntry>();
    
    DbConnectionPool dbPool = DbConnectionPool.getInstance();
    DbConnection dbConn = dbPool.allocate();
    
    Connection conn = dbConn.getConn();
    try
    {
      String sql = "SELECT DISTINCT pattern FROM " + mTableName + ";";
      ResultSet rs = DbUtils.execute(conn, sql);
      while (rs.next())
      {
        AccessEntry ace = createAccessEntry(rs, conn);
        list.add(ace);
      }
      rs.close();
    }
    catch (SQLException e)
    {
      mLogger.debug("AccessEntryDao::getAll() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    mLogger.debug("AccessEntryDao::getAll() - OUT, Returns=" + list.toString() + "; Size=" + list.size());
    return list;
  }

  public void save(AccessEntry t)
  {
    mLogger.debug("AccessEntryDao::save() - IN/OUT");
    throw new RuntimeException("AccessEntryDao.save(AccessEntry) not supported");
  }

  public void update(AccessEntry t, Map<String, String> params)
  {
    mLogger.debug("AccessEntryDao::update() - IN/OUT");
    throw new RuntimeException("AccessEntryDao.update(AccessEntry, Map) not supported");
  }

  public void delete(AccessEntry t)
  {
    mLogger.debug("AccessEntryDao::delete() - IN/OUT");
    throw new RuntimeException("AccessEntryDao.delete(AccessEntry) not supported");
  }

  /**
   * Create a ACE based on the output of a query
   * 
   * @param rs The output of a query
   * @param conn An open Connection to the DB for further queries
   * @return a {@link AccessEntry}
   * @throws SQLException
   */
  private AccessEntry createAccessEntry(ResultSet rs, Connection conn) throws SQLException
  {
    mLogger.debug("AccessEntryDao::createAccessEntry() - IN");
    
    String pat = rs.getString("pattern");
    String sql = "SELECT group_id, access_level FROM " + mTableName + " WHERE pattern = '" + pat + "';";
    ResultSet rs2 = DbUtils.execute(conn, sql);
    Map<Integer, AccessLevel> permissions = new ConcurrentHashMap<Integer, AccessLevel>();
    while (rs2.next())
    {
      int gid = rs2.getInt("group_id");
      int lev = rs2.getInt("access_level");
      permissions.put(gid, new AccessLevel(lev));
    }
    rs2.close();
    
    AccessEntry ace = new AccessEntry(pat, permissions);
    mLogger.debug("AccessEntryDao::createAccessEntry() - OUT, UserEntity=" + ace.toString());
    return ace;
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
