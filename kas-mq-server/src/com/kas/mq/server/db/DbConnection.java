package com.kas.mq.server.db;

import java.sql.Connection;
import java.sql.SQLException;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.UniqueId;

/**
 * A {@link DbConnection} is merely a wrapper to a {@link Connection} that associates it with a {@link UniqueId}.
 * 
 * @author Pippo
 */
public class DbConnection extends AKasObject
{
  /**
   * A connection ID
   */
  private UniqueId mConnId;
  
  /**
   * A connection object
   */
  private Connection mConn;
  
  /**
   * Construct a DB connection object
   * 
   * @param conn The actual connection
   */
  DbConnection(Connection conn)
  {
    mConnId = UniqueId.generate();
    mConn = conn;
  }
  
  /**
   * Get the Connection object
   * 
   * @return the Connection object
   */
  public Connection getConnection()
  {
    return mConn;
  }
  
  /**
   * Get the connection ID
   * 
   * @return the connection ID
   */
  public UniqueId getConnId()
  {
    return mConnId;
  }
  
  /**
   * Close the connection
   */
  public void close()
  {
    try
    {
      mConn.close();
    }
    catch (SQLException e) {}
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
    String pad = pad(level);
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n")
      .append(pad).append("  ID=").append(mConnId.toString()).append("\n")
      .append(pad).append("  Conn=").append(mConn).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
