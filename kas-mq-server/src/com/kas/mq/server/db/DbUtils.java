package com.kas.mq.server.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DbUtils
{
  /**
   * Get int value from result set
   * 
   * @param rs The result set
   * @param col The column label
   * @param defval Default value to return in case of error
   * @return int value
   */
  static public int getInt(ResultSet rs, String col, int defval)
  {
    int result = defval;
    if (rs != null)
    {
      try
      {
        result = rs.getInt(col);
      }
      catch (SQLException e) {}
    }
    return result;
  }
  
  /**
   * Get int value from result set
   * 
   * @param rs The result set
   * @param col The column index
   * @param defval Default value to return in case of error
   * @return int value
   */
  static public int getInt(ResultSet rs, int col, int defval)
  {
    int result = defval;
    if (rs != null)
    {
      try
      {
        result = rs.getInt(col);
      }
      catch (SQLException e) {}
    }
    return result;
  }
}
