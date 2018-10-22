package com.kas.mq.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;

public class DbInitializer
{
  static private ILogger sLogger = LoggerFactory.getLogger(DbInitializer.class);
  
  /**
   * Execute CREATE/UPDATE/DELETE SQL statements
   * 
   * @param format A string format
   * @param args The arguments to injects
   */
  static private void execute(String format, Object ... args)
  {
    sLogger.debug("DbInitializer::execute() - IN");
    
    DbConnectionPool dbPool = DbConnectionPool.getPool();
    DbConnection dbConn = dbPool.allocate();
    Connection conn = dbConn.getConnection();
    
    String sql = String.format(format, args);
    
    try
    {
      PreparedStatement stmt = conn.prepareStatement(sql);
      stmt.execute();
    }
    catch (SQLException e)
    {
      sLogger.debug("DbInitializer::execute() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    sLogger.debug("DbInitializer::execute() - OUT");
  }
  
  /**
   * Execute SELECT SQL statements
   * 
   * @param format A string format
   * @param args The arguments to injects
   * @return a result set
   */
  static private ResultSet query(String format, Object ... args)
  {
    sLogger.debug("DbInitializer::execute() - IN");
    
    DbConnectionPool dbPool = DbConnectionPool.getPool();
    DbConnection dbConn = dbPool.allocate();
    Connection conn = dbConn.getConnection();
    
    ResultSet rs = null;
    
    String sql = String.format(format, args);
    
    try
    {
      PreparedStatement stmt = conn.prepareStatement(sql);
      stmt.execute();
      rs = stmt.getResultSet();
    }
    catch (SQLException e)
    {
      sLogger.debug("DbInitializer::execute() - Exception caught: ", e);
    }
    
    dbPool.release(dbConn);
    sLogger.debug("DbInitializer::execute() - OUT");
    return rs;
  }
}
