package com.kas.db;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.kas.config.MainConfiguration;
import com.kas.infra.base.Properties;
import com.kas.infra.base.PropertyResolver;
import com.kas.infra.utils.FileUtils;
import com.kas.infra.utils.RunTimeUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;

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
  private ILogger mLogger;
  
  /**
   * A {@link Connection} that will be used to execute commands
   */
  private Connection mConnection;
  
  /**
   * Construct the helper
   */
  public SchemaHelper(Connection conn)
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mConnection = conn;
  }
  
  /**
   * Check if the schema exists
   * 
   * @return {@code true} if the schema exists, {@code false} otherwise
   */
  public boolean isExist()
  {
    mLogger.debug("SchemaHelper::isExist() - IN");
    
    boolean exists = false;
    String sql = "select column_name, data_type from information_schema.columns where table_name = 'kas_mq_parameters';";
    try
    {
      PreparedStatement ps = mConnection.prepareStatement(sql);
      ResultSet rs =  ps.executeQuery();
      if (rs.next()) exists = true;
    }
    catch (SQLException e) {}
    
    mLogger.debug("SchemaHelper::isExist() - OUT, Returns=" + exists);
    return exists;
  }
  
  /**
   * Initialize KAS/MQ database schema
   */
  public void init()
  {
    mLogger.debug("SchemaHelper::initSchema() - IN");
    
    Properties props = MainConfiguration.getInstance().getSubset(DbConfiguration.cDbConfigPrefix);
    String dbtype = props.getStringProperty(DbConfiguration.cDbConfigPrefix + "dbtype", DbConfiguration.cDefaultDbType);
    
    mLogger.debug("SchemaHelper::initSchema() - Database type is " + dbtype);
    
    File dbInitFile = new File(RunTimeUtils.getProductHomeDir() + File.separator + "conf" + File.separator + "sql" + File.separator + "db-init-" + dbtype + ".sql");
    List<String> input = FileUtils.load(dbInitFile, "--");
    
    boolean newcmd = true;
    StringBuilder sb = null;
    for (String line : input)
    {
      line = line.trim();
      mLogger.diag("SchemaHelper::initSchema() - Current line: [" + line + "]");
      
      if (newcmd)
      {
        mLogger.diag("SchemaHelper::initSchema() - Current line is the start of a new command");
        sb = new StringBuilder(line);
        newcmd = false;
      }
      else
      {
        mLogger.diag("SchemaHelper::initSchema() - Current line is a continuation of the previous command, concatenating...");
        sb.append(' ').append(line);
      }
      
      if (line.endsWith(";"))
      {
        mLogger.diag("SchemaHelper::initSchema() - End of command detected. Execute it...");
        String cmd = PropertyResolver.resolve(sb.toString(), props);
        try
        {
          DbUtils.execute(mConnection, cmd);
        }
        catch (SQLException e) {}
        newcmd = true;
      }
    }
    mLogger.debug("DbUtils::initSchema() - OUT");
  }
}
