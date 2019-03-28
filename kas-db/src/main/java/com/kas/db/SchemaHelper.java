package com.kas.db;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kas.config.MainConfiguration;
import com.kas.infra.base.Properties;
import com.kas.infra.base.PropertyResolver;
import com.kas.infra.utils.FileUtils;
import com.kas.infra.utils.RunTimeUtils;

/**
 * This class is used to help facilitate operations against the DB
 * 
 * @author Pippo
 */
final public class SchemaHelper
{
  /**
   * Logger
   */
  private Logger mLogger;
  
  /**
   * A {@link Connection} that will be used to execute commands
   */
  private Connection mConnection;
  
  /**
   * Construct the helper
   */
  public SchemaHelper(Connection conn)
  {
    mLogger = LogManager.getLogger(getClass());
    mConnection = conn;
  }
  
  /**
   * Check if the schema exists
   * 
   * @return
   *   {@code true} if the schema exists, {@code false} otherwise
   */
  public boolean isExist()
  {
    mLogger.trace("SchemaHelper::isExist() - IN");
    
    boolean exists = false;
    String sql = "select column_name, data_type from information_schema.columns where table_name = 'kas_mq_parameters';";
    try
    {
      PreparedStatement ps = mConnection.prepareStatement(sql);
      ResultSet rs =  ps.executeQuery();
      if (rs.next()) exists = true;
    }
    catch (SQLException e) {}
    
    mLogger.trace("SchemaHelper::isExist() - OUT, Returns=" + exists);
    return exists;
  }
  
  /**
   * Initialize database schema
   */
  public void init()
  {
    mLogger.trace("SchemaHelper::init() - IN");
    
    Properties props = MainConfiguration.getInstance().getSubset(DbConfiguration.cDbConfigPrefix);
    String dbtype = props.getStringProperty(DbConfiguration.cDbConfigPrefix + "type", DbConfiguration.cDefaultDbType);
    
    mLogger.trace("SchemaHelper::init() - Database type is {}", dbtype);
    
    File dbInitFile = new File(RunTimeUtils.getProductHomeDir() + File.separator + "conf" + File.separator + "sql" + File.separator + "db-init-" + dbtype + ".sql");
    List<String> input = FileUtils.load(dbInitFile, "--");
    
    boolean newcmd = true;
    StringBuilder sb = null;
    for (String line : input)
    {
      line = line.trim();
      mLogger.trace("SchemaHelper::init() - Current line: [{}]", line);
      
      if (newcmd)
      {
        mLogger.trace("SchemaHelper::init() - Current line is the start of a new command");
        sb = new StringBuilder(line);
        newcmd = false;
      }
      else
      {
        mLogger.trace("SchemaHelper::init() - Current line is a continuation of the previous command, concatenating...");
        sb.append(' ').append(line);
      }
      
      if (line.endsWith(";"))
      {
        mLogger.trace("SchemaHelper::init() - End of command detected. Execute it...");
        String cmd = PropertyResolver.resolve(sb.toString(), props);
        try
        {
          DbUtils.execute(mConnection, cmd);
        }
        catch (SQLException e) {}
        newcmd = true;
      }
    }
    mLogger.trace("DbUtils::init() - OUT");
  }
}
