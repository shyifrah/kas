package com.kas.sec.access;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kas.db.DbConnection;
import com.kas.db.DbConnectionPool;
import com.kas.db.DbUtils;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.IObject;

/**
 * Object responsible for managing, storing, retrieving and updating
 * {@link AccessEntry} to the database.
 * 
 * @author Pippo
 */
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
  private Logger mLogger = LogManager.getLogger(getClass());
  
  /**
   * Name of table that holds ACEs data
   */
  private String mTableName;
  
  /**
   * Construct the the DAO object
   * 
   * @param resTypeNam
   *    Name of the resource
   */
  AccessEntryDao(String resTypeName)
  {
    mTableName = "kas_mq_" + resTypeName.toLowerCase() + "_permissions";
  }

  /**
   * Get a list of all {@link AccessEntry} objects
   * 
   * @return
   *   a list of all {@link AccessEntry} objects
   */
  public List<AccessEntry> getAll()
  {
    mLogger.trace("AccessEntryDao::getAll() - IN");
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
      mLogger.trace("AccessEntryDao::getAll() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    mLogger.trace("AccessEntryDao::getAll() - OUT, Returns=" + list.toString() + "; Size=" + list.size());
    return list;
  }

  /**
   * Create a ACE based on the output of a query
   * 
   * @param rs
   *   The output of a query
   * @param conn
   *   An open Connection to the DB for further queries
   * @return
   *   a {@link AccessEntry}
   * @throws SQLException
   *   if one is thrown by java.sql.* classes
   */
  private AccessEntry createAccessEntry(ResultSet rs, Connection conn) throws SQLException
  {
    mLogger.trace("AccessEntryDao::createAccessEntry() - IN");
    
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
    mLogger.trace("AccessEntryDao::createAccessEntry() - OUT, UserEntity=" + ace.toString());
    return ace;
  }
  
  /**
   * Returns the {@link IObject} string representation.
   * 
   * @param level
   *   The required padding level
   * @return
   *   the string representation with the specified level of padding
   */
  public String toPrintableString(int level)
  {
    return toString();
  }
}
