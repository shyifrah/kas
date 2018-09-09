package com.kas.mq.server.internal;

import java.util.Collection;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.IObject;
import com.kas.infra.base.KasException;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.internal.IClient;
import com.kas.mq.impl.internal.IMqConstants;
import com.kas.mq.impl.internal.MqLocalQueue;
import com.kas.mq.impl.internal.MqQueue;
import com.kas.mq.server.IRepository;

/**
 * A responder client is a {@link IClient} that responds to client requests
 * 
 * @author Pippo
 */
public class ClientResponder extends AKasObject implements IClient
{
  /**
   * Logger
   */
  private ILogger mLogger;
  
  /**
   * Queue repository
   */
  private IRepository mRepository;
  
  /**
   * The response from last call
   */
  private String mResponse;
  
  /**
   * Construct a Responder client, passing it the {@link IRepository} object
   * 
   * @param repository The queue repository
   */
  ClientResponder(IRepository repository)
  {
    mLogger = LoggerFactory.getLogger(this.getClass());
    mRepository = repository;
  }
  
  /**
   * Connect client to the KAS/MQ server.
   * 
   * @param host The host name or IP address
   * @param port The port number
   * 
   * @throws RuntimeException Always. This method cannot be invoked
   * 
   * @see com.kas.mq.impl.internal.IClient#connect(String, int)
   */
  public void connect(String host, int port) throws KasException
  {
    throw new RuntimeException("Cannot call connect()");
  }

  /**
   * Disconnecting from the remote KAS/MQ server.
   * 
   * @throws RuntimeException Always. This method cannot be invoked
   * 
   * @see com.kas.mq.impl.internal.IClient#disconnect()
   */
  public void disconnect() throws KasException
  {
    throw new RuntimeException("Cannot call disconnect()");
  }

  /**
   * Get the connection status.
   * 
   * @return {@code true} if client is connected, {@code false} otherwise
   * @throws RuntimeException Always. This method cannot be invoked
   * 
   * @see com.kas.mq.impl.internal.IClient#isConnected()
   */
  public boolean isConnected()
  {
    throw new RuntimeException("Cannot call isConnected()");
  }

  /**
   * Authenticate client against the KAS/MQ server.
   * 
   * @param user The user's name
   * @param pwd The user's password
   * 
   * @throws RuntimeException Always. This method cannot be invoked
   * 
   * @return {@code true} if user successfully authenticated, {@code false} otherwise
   */
  public boolean login(String user, String pwd)
  {
    throw new RuntimeException("Cannot call login()");
  }
  
  /**
   * Define a new queue.
   * 
   * @param queue The queue name to define.
   * @param threshold The queue threshold
   * @return the {@code true} if queue was defined, {@code false} otherwise
   * 
   * @see com.kas.mq.impl.internal.IClient#defineQueue(String, int)
   */
  public boolean defineQueue(String queue, int threshold)
  {
    mLogger.debug("ResponderClient::defineQueue() - IN, Queue=" + queue + ", Threshold=" + threshold);
    
    boolean success = false;
    MqLocalQueue mqq = mRepository.getLocalQueue(queue);
    
    if ((queue == null) || (queue.length() == 0))
    {
      mLogger.debug("ResponderClient::defineQueue() - Invalid queue name: null or empty string");
      setResponse("Invalid queue name: null or empty string");
    }
    else if (mqq != null)
    {
      mLogger.debug("ResponderClient::defineQueue() - Queue with name \"" + queue + "\" already exists");
      setResponse("Queue with name \"" + queue + "\" already exists");
    }
    else
    {
      mqq = mRepository.defineLocalQueue(queue, threshold);
      mLogger.debug("ResponderClient::defineQueue() - Created queue " + StringUtils.asPrintableString(mqq));
      setResponse("");
      success = true;
    }
    
    mLogger.debug("ResponderClient::defineQueue() - OUT, Returns=" + success);
    return success;
  }

  /**
   * Delete an existing queue.
   * 
   * @param queue The queue name to delete.
   * @param force Should the queue be deleted even if its not empty.
   * @return the {@code true} if queue was deleted, {@code false} otherwise
   * 
   * @see com.kas.mq.impl.internal.IClient#deleteQueue(String, boolean)
   */
  public boolean deleteQueue(String queue, boolean force)
  {
    mLogger.debug("ResponderClient::deleteQueue() - IN, Queue=" + queue + ", Force=" + force);
    
    boolean success = false;
    MqLocalQueue mqq = mRepository.getLocalQueue(queue);
    
    if ((queue == null) || (queue.length() == 0))
    {
      mLogger.debug("ResponderClient::deleteQueue() - Invalid queue name: null or empty string");
      setResponse("Invalid queue name: null or empty string");
    }
    else if (mqq == null)
    {
      mLogger.debug("ResponderClient::deleteQueue() - Queue with name \"" + queue + "\" doesn't exist");
      setResponse("Queue with name \"" + queue + "\" doesn't exist");
      success = true;
    }
    else
    {
      int size = mqq.size();
      if (size == 0)
      {
        mRepository.deleteLocalQueue(queue);
        mLogger.debug("ResponderClient::deleteQueue() - Queue with name \"" + queue + "\" was successfully deleted");
        setResponse("");
        success = true;
      }
      else if (force)
      {
        mRepository.deleteLocalQueue(queue);
        mLogger.debug("ResponderClient::deleteQueue() - Queue with name \"" + queue + "\" was successfully deleted (" + size + " messages discarded)");
        setResponse("");
        success = true;
      }
      else
      {
        mLogger.debug("ResponderClient::deleteQueue() - Queue is not empty (" + size + " messages) and FORCE was not specified");
        setResponse("Queue is not empty (" + size + " messages) and FORCE was not specified");
      }
    }
    
    mLogger.debug("ResponderClient::deleteQueue() - OUT, Returns=" + success);
    return success;
  }

  /**
   * Query KAS/MQ server for information regarding all queues whose name begins with the specified prefix.
   * 
   * @param name The queue name. If it ends with {@code asterisk}, then the name is a prefix
   * @param prefix If {@code true}, the {@code name} designates a queue name prefix. If {@code false}, it's a queue name
   * @param all if {@code true}, display all information on all queues 
   * @return the number of records returned that matched the query, or -1 if an error occurred
   * 
   * @see com.kas.mq.impl.internal.IClient#queryQueue(String, boolean)
   */
  public int queryQueue(String name, boolean prefix, boolean all)
  {
    mLogger.debug("ResponderClient::queryQueue() - IN, Name=" + name + ", Prefix=" + prefix + ", All=" + all);
    
    if (name == null)
      name = "";
    
    Collection<MqQueue> queues = mRepository.getLocalQueues();
    StringBuilder sb = new StringBuilder();
    sb.append("Query ").append((all ? "all" : "basic")).append(" data on ").append(name).append((prefix ? "*" : "")).append(":\n").append("  \n");
    int total = 0;
    for (MqQueue queue : queues)
    {
      MqLocalQueue mqlq = (MqLocalQueue)queue;
      boolean include = false;
      if (prefix)
        include = mqlq.getName().startsWith(name);
      else
        include = mqlq.getName().equals(name);
      
      if (include)
      {
        ++total;
        sb.append("Queue....................: ").append(mqlq.getName()).append('\n');
        sb.append("    Type.............: LOCAL\n");
        if (all)
        {
          sb.append("    Threshold........: ").append(mqlq.getThreshold()).append('\n');
          sb.append("    Size.............: ").append(mqlq.size()).append('\n');
          sb.append("    Last access......: ").append(mqlq.getLastAccess()).append('\n');
        }
        sb.append(" ").append('\n');
      }
    }
    
    if (total == 0)
      sb.append(" ").append('\n').append("No queues matched specified criteria");
    else
      sb.append(" ").append('\n').append(total).append(" queues matched specified criteria");
    
    setResponse(sb.toString());
    mLogger.debug("ResponderClient::queryQueue() - OUT, Returns=" + true);
    return total;
  }

  /**
   * Get a message from queue.
   * 
   * @param queue The target queue name
   * @param timeout The number of milliseconds to wait until a message available
   * @param interval The number in milliseconds the thread execution is suspended between each polling operation
   * @return the {@link IMqMessage} object or {@code null} if a message is unavailable
   * 
   * @see com.kas.mq.impl.internal.IClient#get(String, long, long)
   */
  public IMqMessage<?> get(String queue, long timeout, long interval)
  {
    mLogger.debug("ResponderClient::get() - IN, Queue=" + queue + ", Timeout=" + timeout + ", Interval=" + interval);
    
    IMqMessage<?> result = null;
    MqLocalQueue mqq = mRepository.getLocalQueue(queue);
    if ((queue == null) || (queue.length() == 0))
    {
      mLogger.debug("ResponderClient::get() - Invalid queue name: null or empty string");
      setResponse("Invalid queue name: null or empty string");
    }
    else if (mqq == null)
    {
      mLogger.debug("ResponderClient::get() - Queue with name \"" + queue + "\" doesn't exist");
      setResponse("Queue with name \"" + queue + "\" doesn't exist");
    }
    else
    {
      result = mqq.get(timeout, interval);
      if (result == null)
      {
        mLogger.debug("ResponderClient::get() - No message found in queue " + queue);
        setResponse("No message found in queue " + queue);
      }
      else
      {
        mLogger.debug("ResponderClient::get() - Picked up message from " + queue + ". Message: " + StringUtils.asPrintableString(result));
        setResponse("");
      }
    }
    
    mLogger.debug("ResponderClient::get() - OUT");
    return result;
  }

  /**
   * Put a message into the opened queue.
   * 
   * @param queue The target queue name
   * @param message The message to be put
   * 
   * @see com.kas.mq.impl.internal.IClient#put(String, IMqMessage)
   */
  public void put(String queue, IMqMessage<?> message)
  {
    mLogger.debug("ResponderClient::put() - IN, Queue=" + queue);
    
    mLogger.debug("ResponderClient::put() - Message to put: " + StringUtils.asPrintableString(message));
    
    MqLocalQueue mqq = mRepository.getLocalQueue(queue);
    MqLocalQueue ddq = mRepository.getDeadQueue();
    if ((queue == null) || (queue.length() == 0))
    {
      mLogger.debug("ResponderClient::put() - Invalid queue name: null or empty string");
      setResponse("Invalid queue name: null or empty string");
      ddq.put(message);
    }
    else if (mqq == null)
    {
      mLogger.debug("ResponderClient::put() - Queue with name \"" + queue + "\" doesn't exist");
      setResponse("Queue with name \"" + queue + "\" doesn't exist");
      ddq.put(message);
    }
    else
    {
      boolean success = mqq.put(message);
      if (!success)
        ddq.put(message);
      mLogger.debug("ResponderClient::put() - Message was put to queue " + queue);
      setResponse("");
      String user = message.getStringProperty(IMqConstants.cKasPropertyPutUserName, null);
      mqq.setLastAccess(user, "put");
    }
    
    mLogger.debug("ResponderClient::put() - OUT");
  }

  /**
   * Mark the KAS/MQ server it should shutdown
   * 
   * @return {@code true} if the server accepted the request, {@code false} otherwise
   * @throws RuntimeException Always. This method cannot be invoked
   * 
   * @see com.kas.mq.impl.internal.IClient#shutdown()
   */
  public boolean shutdown()
  {
    throw new RuntimeException("Cannot call shutdown()");
  }

  /**
   * Get last response from last {@link IClient} call.
   * 
   * @return the last message the {@link IClient} issued for a call.
   * 
   * @see com.kas.mq.impl.internal.IClient#getResponse()
   */
  public String getResponse()
  {
    return mResponse;
  }
  
  /**
   * Set response from last {@link IClient} call.
   * 
   * @param response The response from last {@link IClient} call
   * 
   * @see com.kas.mq.impl.internal.IClient#setResponse(String)
   */
  public void setResponse(String response)
  {
    mResponse = response;
  }

  /**
   * Returns the {@link IObject} string representation.
   * 
   * @param level the required level padding
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
   */
  public String toPrintableString(int level)
  {
    return name();
  }
}
