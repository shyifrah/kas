package com.kas.db;

/**
 * A set of DB-related utility functions
 * 
 * @author Pippo
 */
public class DbUtils
{
  /**
   * Create a connection URL
   * 
   * @param dbtype The database type
   * @param host The database host name
   * @param port The database listening port
   * @param schema The schema used
   * @param user The database user's name
   * @param pswd The database user's password
   * @return the connection URL
   */
  static public String createConnUrl(String dbtype, String host, int port, String schema, String user, String pswd)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(String.format("jdbc:%s://%s:%d/%s?user=%s&password=%s", dbtype, host, port, schema, user, pswd));
    
    switch (dbtype)
    {
      case "mysql":
        sb.append("&serverTimezone=UTC&useSSL=false");
        break;
      case "postgresql":
        sb.append("&ssl=false");
        break;
    }
    
    return sb.toString();
  }
}
