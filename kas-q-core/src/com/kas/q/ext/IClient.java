package com.kas.q.ext;

import javax.jms.QueueConnectionFactory;

public interface IClient extends QueueConnectionFactory
{
  /***************************************************************************************************************
   * Connect to a KasQ server using the default user identity
   * 
   * @param host hostname or IP-address of the KasQ server
   * @param port port number on which the KasQ server listens for new connections
   * 
   * @return true if connection was successfully established, false otherwise
   * 
   * @throws KasException
   */
  //public abstract boolean connect(String host, int port) throws KasException;
  
  /***************************************************************************************************************
   * Connect to a KasQ server using the specific user identity
   * 
   * @param host hostname or IP-address of the KasQ server
   * @param port port number on which the KasQ server listens for new connections
   * @param userName the caller's user name
   * @param password the caller's password 
   * 
   * @return true if connection was successfully established, false otherwise
   * 
   * @throws KasException
   */
  //public abstract boolean connect(String host, int port, String userName, String password) throws KasException;
  
  /***************************************************************************************************************
   * Disconnect from a KasQ server
   * 
   * @return true if connection was successfully terminated, false otherwise
   * 
   * @throws KasException
   */
  //public abstract boolean disconnect() throws KasException;
  
  /***************************************************************************************************************
   * Is client connected to remote host
   * 
   * @return true if connection was successfully established, false otherwise
   */
  //public abstract boolean isConnected();
}
