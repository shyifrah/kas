//package com.kas.mq.impl.internal;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import com.kas.infra.base.AKasObject;
//import com.kas.infra.base.UniqueId;
//
//public class MqConnectionPool extends AKasObject
//{
//  static private MqConnectionPool sInstance = new MqConnectionPool();
//
//  private Map<UniqueId, MqConnection> mConnectionMap = new ConcurrentHashMap<UniqueId, MqConnection>();
//  
//  static private MqConnectionPool getInstance()
//  {
//    return sInstance;
//  }
//  
//  static public MqConnection allocate()
//  {
//    MqConnection conn = new MqConnection();
//    getInstance().mConnectionMap.put(conn.getConnectionId(), conn);
//    return conn;
//  }
//  
//  static public void release(MqConnection conn)
//  {
//    UniqueId id = conn.getConnectionId();
//    getInstance().mConnectionMap.remove(id);
//  }
//  
//  static public void close()
//  {
//    for (Map.Entry<UniqueId, MqConnection> entry : getInstance().mConnectionMap.entrySet())
//    {
//      UniqueId id = entry.getKey();
//      MqConnection conn = entry.getValue();
//      conn.disconnect();
//    }
//  }
//  
//  public String toPrintableString(int level)
//  {
//    return null;
//  }
//}
