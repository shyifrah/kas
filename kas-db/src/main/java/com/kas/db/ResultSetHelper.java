package com.kas.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import com.kas.infra.base.AKasObject;

/**
 * A helper class for simplifying access to data held by a result set
 * 
 * @author Pippo
 */
public class ResultSetHelper extends AKasObject
{
  /**
   * The result set
   */
  private ResultSet mResultSet;
  
  /**
   * Construct a helper for the specified {@link ResultSet}i
   * 
   * @param rs The result set
   */
  public ResultSetHelper(ResultSet rs)
  {
    mResultSet = rs;
  }
  
  /**
   * Move cursor to next row
   * 
   * @return {@code true} if the new current row is valid; {@code false} if there are no more rows
   */
  public boolean next()
  {
    if (mResultSet == null)
      return false;
    
    boolean result = false;
    try
    {
      result = mResultSet.next();
    }
    catch (SQLException e) {}
    return result;
  }
   
  /**
   * Get integer value from a result set
   * 
   * @param label The column label
   * @param defval Default value to return in case of an error
   * @return the value of the specified column in the row pointed by the cursor
   */
  public int getIntegerCol(String label, int defval)
  {
    int result = defval;
    if (mResultSet != null)
    {
      try
      {
        result = mResultSet.getInt(label);
      }
      catch (SQLException e) {}
    }
    return result;
  }
  
  /**
   * Get integer value from a result set
   * 
   * @param index The column index
   * @param defval Default value to return in case of an error
   * @return the value of the specified column in the row pointed by the cursor
   */
   public int getIntegerCol(int index, int defval)
  {
    int result = defval;
    if (mResultSet != null)
    {
      try
      {
        result = mResultSet.getInt(index);
      }
      catch (SQLException e) {}
    }
    return result;
  }
  
  /**
   * Get string value from a result set
   * 
   * @param label The column label
   * @param defval Default value to return in case of an error
   * @return the value of the specified column in the row pointed by the cursor
   */
  public String getStringCol(String label, String defval)
  {
    String result = defval;
    if (mResultSet != null)
    {
      try
      {
        result = mResultSet.getString(label);
      }
      catch (SQLException e) {}
    }
    return result;
  }
  
  /**
   * Get string value from a result set
   * 
   * @param index The column index
   * @param defval Default value to return in case of an error
   * @return the value of the specified column in the row pointed by the cursor
   */
  public String getStringCol(int index, String defval)
  {
    String result = defval;
    if (mResultSet != null)
    {
      try
      {
        result = mResultSet.getString(index);
      }
      catch (SQLException e) {}
    }
    return result;
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
    return mResultSet.toString();
  }
}
