package com.kas.mq.server.internal;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.UniqueId;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.internal.IMqConnectionPool;
import com.kas.mq.internal.MqConnection;

/**
 * Not really a pool, just a class that keeps track of all allocated connections
 * 
 * @author Pippo
 */
public class MqServerConnectionPool extends AKasObject implements IMqConnectionPool
{
  /**
   * Singleton instance
   */
  private static MqServerConnectionPool sInstance = new MqServerConnectionPool();
  
  /**
   * Get the singleton instance
   * 
   * @return the singleton instance
   */
  public static MqServerConnectionPool getInstance()
  {
    return sInstance;
  }
  
  /**
   * Logger
   */
  private ILogger mLogger;
  
  /**
   * Map of all allocated connections
   */
  private Map<UniqueId, MqServerConnection> mConnections = new ConcurrentHashMap<UniqueId, MqServerConnection>();
  
  /**
   * Private constructor
   */
  private MqServerConnectionPool()
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
  }
  
  /**
   * Allocate a new {@link MqServerConnection}
   * 
   * @return the newly allocated connection
   */
  public MqServerConnection allocate()
  {
    mLogger.debug("MqServerConnectionPool::allocate() - IN");
    
    MqServerConnection conn = new MqServerConnection();
    UniqueId uid = conn.getConnectionId();
    mConnections.put(uid, conn);
    
    mLogger.debug("MqServerConnectionPool::allocate() - OUT");
    return conn;
  }
  
  /**
   * Release the specified connection
   * 
   * @param conn {@link MqServerConnection} to be released
   */
  public void release(MqConnection conn)
  {
    mLogger.debug("MqServerConnectionPool::release() - IN");
    
    mConnections.remove(conn.getConnectionId());
    conn.disconnect();
    conn = null;
    
    mLogger.debug("MqServerConnectionPool::release() - OUT");
  }
  
  /**
   * Release the connection associated with the specified {@code id}
   * 
   * @param id {@link UniqueId} of the {@link MqServerConnection}
   */
  public void release(UniqueId id)
  {
    mLogger.debug("MqServerConnectionPool::release() - IN");
    
    MqServerConnection conn = mConnections.remove(id);
    conn.disconnect();
    conn = null;
    
    mLogger.debug("MqServerConnectionPool::release() - OUT");
  }
  
  /**
   * Terminate the connection pool
   */
  public void shutdown()
  {
    mLogger.debug("MqServerConnectionPool::shutdown() - IN");
    
    Collection<MqServerConnection> col = mConnections.values();
    for (Iterator<MqServerConnection> iter = col.iterator(); iter.hasNext();)
    {
      MqServerConnection conn = iter.next();
      UniqueId id = conn.getConnectionId();
      mLogger.debug("MqServerConnectionPool::shutdown() - Closing connection ID " + id);
      conn.disconnect();
      iter.remove();
    }
    
    mConnections.clear();
    
    mLogger.debug("MqServerConnectionPool::shutdown() - OUT");
  }
  
  /**
   * Get the connection associated with {@code id}
   * 
   * @param id The {@link UniqueId} of the connection
   * @return The {@link MqServerConnection connection} associated with the specified session ID
   */
  public MqServerConnection getConnection(UniqueId id)
  {
    mLogger.debug("MqServerConnectionPool::getConnection() - IN, ConnId=" + id);
    
    if (id == null)
      return null;
    
    MqServerConnection conn = mConnections.get(id);
    
    mLogger.debug("MqServerConnectionPool::getConnection() - OUT");
    return conn;
  }
  
  /**
   * Get all connections
   * 
   * @return a collection of all connections
   */
  public Collection<MqServerConnection> getConnections()
  {
    return mConnections.values();
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
      .append(pad).append("  Connections=(").append(StringUtils.asPrintableString(mConnections, level+2)).append(")\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
