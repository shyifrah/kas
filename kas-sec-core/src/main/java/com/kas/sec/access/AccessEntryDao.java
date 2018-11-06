package com.kas.sec.access;

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
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.IDao;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.sec.entities.UserEntity;

public class AccessEntryDao extends AKasObject implements IDao<AccessEntry>
{
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
  
  private String mTableName;
  
  AccessEntryDao(String resTypeName)
  {
    mTableName = "kas_mq_" + resTypeName + "_permissions";
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
    throw new RuntimeException("AccessEntry.get(String) not supported");
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
    throw new RuntimeException("AccessEntry.get(int) not supported");
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
      String sql = "SELECT pattern, group_id, access_level FROM " + mTableName + ';';
      ResultSet rs = DbUtils.execute(conn, sql);
      AccessEntry ace = null;
      while (rs.next())
      {
        ace = createAccessEntry(rs, conn);
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

  @Override
  public void save(AccessEntry t)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void update(AccessEntry t, Map<String, String> params)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void delete(AccessEntry t)
  {
    // TODO Auto-generated method stub

  }

  /**
   * Create a user entity based on the output of a query
   * 
   * @param rs The output of a query
   * @param conn An open Connection to the DB for further queries
   * @return a {@link UserEntity} 
   * @throws SQLException
   */
  private AccessEntry createAccessEntry(ResultSet rs, Connection conn) throws SQLException
  {
    mLogger.debug("AccessEntryDao::createAccessEntry() - IN");
    
    // TODO: COMPLETE
    
    //int uid = rs.getInt("id");
    //String uname = rs.getString("name");
    //String udesc = rs.getString("description");
    //String upass = rs.getString("password");
    
    Map<Integer, AccessLevel> map = null;
    
    //List<Integer> ugids = getUserGroups(conn, uid);
    
    AccessEntry ace = new AccessEntry(map);
    mLogger.debug("AccessEntryDao::createAccessEntry() - OUT, UserEntity=" + ace.toString());
    return ace;
  }
  
  @Override
  public String toPrintableString(int level)
  {
    // TODO Auto-generated method stub
    return null;
  }
}
