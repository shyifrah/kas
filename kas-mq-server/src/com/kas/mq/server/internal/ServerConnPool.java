package com.kas.mq.server.internal;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.kas.infra.base.UniqueId;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;

/**
 * Not really a pool, just a class that keeps track of all allocated connections
 * 
 * @author Pippo
 */
public class ServerConnPool
{
  /**
   * Logger
   */
  private ILogger mLogger;
  
  /**
   * Map of all allocated connections
   */
  private Map<UniqueId, MqServerConnection> mConnections = new ConcurrentHashMap<UniqueId, MqServerConnection>();
  
  /**
   * Singleton instance
   */
  private static ServerConnPool sInstance = new ServerConnPool();
  
  /**
   * Get the singleton instance
   * 
   * @return the singleton instance
   */
  public static ServerConnPool getInstance()
  {
    return sInstance;
  }
  
  /**
   * Private constructor
   */
  private ServerConnPool()
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
    mLogger.debug("ServerConnectionPool::allocate() - IN");
    MqServerConnection conn = new MqServerConnection();
    UniqueId uid = conn.getConnectionId();
    mConnections.put(uid, conn);
    mLogger.debug("ServerConnectionPool::allocate() - OUT");
    return conn;
  }
  
  /**
   * Release the specified connection
   * 
   * @param conn {@link MqServerConnection} to be released
   */
  public void release(MqServerConnection conn)
  {
    mLogger.debug("ServerConnectionPool::release() - IN");
    mConnections.remove(conn.getConnectionId());
    conn.disconnect();
    conn = null;
    mLogger.debug("ServerConnectionPool::release() - OUT");
  }
  
  /**
   * Closing all connections and clearing the map
   */
  public void shutdown()
  {
    mLogger.debug("ServerConnectionPool::shutdown() - IN");
    
    Collection<MqServerConnection> col = mConnections.values();
    for (Iterator<MqServerConnection> iter = col.iterator(); iter.hasNext();)
    {
      MqServerConnection conn = iter.next();
      UniqueId id = conn.getConnectionId();
      mLogger.debug("ServerConnectionPool::shutdown() - Closing connection ID " + id);
      conn.disconnect();
      iter.remove();
    }
    
    mConnections.clear();
    
    mLogger.debug("ServerConnectionPool::shutdown() - OUT");
  }
  
  /**
   * Get the connection associated with {@code id}
   * 
   * @param id The {@link UniqueId} of the connection
   * @return The {@link MqServerConnection connection} associated with the specified session ID
   */
  public MqServerConnection getConnection(UniqueId id)
  {
    mLogger.debug("ServerConnectionPool::getConnection() - IN, ConnId=" + id);
    
    if (id == null)
      return null;
    
    MqServerConnection conn = mConnections.get(id);
    mLogger.debug("ServerConnectionPool::getConnection() - OUT");
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
}
