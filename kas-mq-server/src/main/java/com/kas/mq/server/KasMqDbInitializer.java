package com.kas.mq.server;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import com.kas.appl.AKasAppl;
import com.kas.db.DbUtils;
import com.kas.infra.base.Properties;
import com.kas.infra.base.PropertyResolver;
import com.kas.infra.utils.FileUtils;
import com.kas.infra.utils.RunTimeUtils;
import com.kas.infra.utils.StringUtils;

public class KasMqDbInitializer extends AKasAppl
{
  static final String cAppName = "KAS/MQ DataBase initialization utility";
  
  /**
   * A connection to the DB
   */
  private Connection mConnection = null;
  
  /**
   * Construct the {@link KasMqDbInitializer} passing it the startup arguments
   * 
   * @param args The startup arguments
   */
  public KasMqDbInitializer(Map<String, String> args)
  {
    super(args);
  }
  
  /**
   * Get the application name
   * 
   * @return the application name 
   */
  public String getAppName()
  {
    return cAppName;
  }
  
  /**
   * Run KAS/MQ DataBase initialization utility.<br>
   * <br>
   * 
   * @see IKasMqAppl#appExec()
   */
  public void appExec()
  {
    mLogger.debug("KasMqDbInitializer::appExec() - IN");
    
    Properties args = new Properties();
    args.putAll(mStartupArgs);
    String dbtype = mStartupArgs.getOrDefault("kas.db.type", "mysql");
    String host   = mStartupArgs.getOrDefault("kas.db.host", "localhost");
    String port   = mStartupArgs.getOrDefault("kas.db.port", "3306");
    String schema = mStartupArgs.getOrDefault("kas.db.schema", "kas");
    String user   = mStartupArgs.getOrDefault("kas.db.username", "kas");
    String pswd   = mStartupArgs.getOrDefault("kas.db.password", "kas");
    
    writeln(cAppName + " is executed with the following properties:");
    writeln("kas.db.type.......: [" + dbtype + "]");
    writeln("kas.db.host.......: [" + host + "]");
    writeln("kas.db.port.......: [" + port + "]");
    writeln("kas.db.schema.....: [" + schema + "]");
    writeln("kas.db.username...: [" + user + "]");
    writeln("kas.db.password...: [" + pswd + "]");
    
    try
    {
      String url = DbUtils.createConnUrl(dbtype, host, Integer.valueOf(port), schema, user, pswd);
      mConnection = DriverManager.getConnection(url);
      
      File dbInitFile = new File(RunTimeUtils.getProductHomeDir() + File.separator + "conf" + File.separator + "db-init-" + dbtype + ".sql");
      List<String> input = FileUtils.load(dbInitFile, "--");
      
      boolean newcmd = true;
      StringBuilder sb = null;
      for (String line : input)
      {
        line = line.trim();
        if (newcmd)
        {
          sb = new StringBuilder(line);
          newcmd = false;
        }
        else
        {
          sb.append(' ').append(line);
        }
        
        if (line.endsWith(";"))
        {
          String cmd = PropertyResolver.resolve(sb.toString(), args);
          execute(cmd);
          newcmd = true;
        }
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
    
    mLogger.debug("KasMqDbInitializer::appExec() - OUT");
  }
  
  /**
   * Execute a SQL statement
   * 
   * @param fmt The SQL statement base format
   * @param args Arguments to format the SQL
   * @return A result set, if one was generated
   * @throws SQLException if thrown by java.sql.* classes
   */
  private ResultSet execute(String fmt, Object... args) throws SQLException
  {
    mLogger.debug("KasMqDbInitializer::execute() - IN");
    
    if (mConnection == null) throw new SQLException("Null connection");
    
    String sql = String.format(fmt, args);
    ResultSet result = null;

    sql = StringUtils.clearBlanks(sql);
    mLogger.debug("KasMqDbInitializer::execute() - Executing SQL: [" + sql + "]");
    writeln("Executing SQL: [" + sql + "]");
    PreparedStatement st = mConnection.prepareStatement(sql);
    boolean b = st.execute();
    if (b) result = st.getResultSet();
    
    mLogger.debug("KasMqDbInitializer::execute() - HaveResult: [" + b + "]");
    
    mLogger.debug("KasMqDbInitializer::execute() - OUT");
    return result;
  }
  
  /**
   * Writing a message to STDOUT. The message will be followed by a Newline character.
   * 
   * @param message The message to print
   */
  static private void writeln(String message)
  {
    System.out.println(message);
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
      .append(pad).append("  ShutdownHook=(").append(StringUtils.asPrintableString(mShutdownHook)).append(")\n")
      .append(pad).append(")\n");
    return sb.toString();
  }
}