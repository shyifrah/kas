package com.kas.mq.server.internal;

import com.kas.infra.base.AKasObject;
import com.kas.infra.base.IObject;
import com.kas.infra.base.Properties;
import com.kas.infra.utils.StringUtils;
import com.kas.logging.ILogger;
import com.kas.logging.LoggerFactory;
import com.kas.mq.impl.IMqMessage;
import com.kas.mq.impl.internal.IMqConstants;
import com.kas.mq.impl.internal.MqLocalQueue;
import com.kas.mq.server.IRepository;

/**
 * A responder client is a {@link IClient} that responds to client requests
 * 
 * @author Pippo
 */
public class ClientResponder extends AKasObject
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
    mLogger.debug("ClientResponder::defineQueue() - IN, Queue=" + queue + ", Threshold=" + threshold);
    
    boolean success = false;
    MqLocalQueue mqq = mRepository.getLocalQueue(queue);
    
    if ((queue == null) || (queue.length() == 0))
    {
      mLogger.debug("ClientResponder::defineQueue() - Invalid queue name: null or empty string");
      setResponse("Invalid queue name: null or empty string");
    }
    else if (mqq != null)
    {
      mLogger.debug("ClientResponder::defineQueue() - Queue with name \"" + queue + "\" already exists");
      setResponse("Queue with name \"" + queue + "\" already exists");
    }
    else
    {
      mqq = mRepository.defineLocalQueue(queue, threshold);
      mLogger.debug("ClientResponder::defineQueue() - Created queue " + StringUtils.asPrintableString(mqq));
      setResponse("Queue with name " + queue + " and threshold of " + threshold + " was successfully defined");
      success = true;
    }
    
    mLogger.debug("ClientResponder::defineQueue() - OUT, Returns=" + success);
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
    mLogger.debug("ClientResponder::deleteQueue() - IN, Queue=" + queue + ", Force=" + force);
    
    boolean success = false;
    MqLocalQueue mqq = mRepository.getLocalQueue(queue);
    
    if ((queue == null) || (queue.length() == 0))
    {
      mLogger.debug("ClientResponder::deleteQueue() - Invalid queue name: null or empty string");
      setResponse("Invalid queue name: null or empty string");
    }
    else if (mqq == null)
    {
      mLogger.debug("ClientResponder::deleteQueue() - Queue with name \"" + queue + "\" doesn't exist");
      setResponse("Queue with name \"" + queue + "\" doesn't exist");
      success = true;
    }
    else
    {
      int size = mqq.size();
      if (size == 0)
      {
        mRepository.deleteLocalQueue(queue);
        mLogger.debug("ClientResponder::deleteQueue() - Queue with name \"" + queue + "\" was successfully deleted");
        setResponse("Queue with name \"" + queue + "\" was successfully deleted");
        success = true;
      }
      else if (force)
      {
        mRepository.deleteLocalQueue(queue);
        mLogger.debug("ClientResponder::deleteQueue() - Queue with name \"" + queue + "\" was successfully deleted (" + size + " messages discarded)");
        setResponse("Queue with name \"" + queue + "\" was successfully deleted (" + size + " messages discarded)");
        success = true;
      }
      else
      {
        mLogger.debug("ClientResponder::deleteQueue() - Queue is not empty (" + size + " messages) and FORCE was not specified");
        setResponse("Queue is not empty (" + size + " messages) and FORCE was not specified");
      }
    }
    
    mLogger.debug("ClientResponder::deleteQueue() - OUT, Returns=" + success);
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
   * @see com.kas.mq.impl.internal.IClient#queryQueue(String, boolean, boolean)
   */
  public Properties queryQueue(String name, boolean prefix, boolean all)
  {
    mLogger.debug("ClientResponder::queryQueue() - IN, Name=" + name + ", Prefix=" + prefix + ", All=" + all);
    
    if (name == null) name = "";
    
    Properties props = mRepository.queryQueues(name, prefix, all);
    int total = props.size();
    
    setResponse(String.format("%s queues matched filtering criteria", (total == 0 ? "No" : total)));
    mLogger.debug("ClientResponder::queryQueue() - OUT, Returns=" + total + " queues");
    return props;
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
    mLogger.debug("ClientResponder::get() - IN, Queue=" + queue + ", Timeout=" + timeout + ", Interval=" + interval);
    
    IMqMessage<?> result = null;
    MqLocalQueue mqq = mRepository.getLocalQueue(queue);
    if ((queue == null) || (queue.length() == 0))
    {
      mLogger.debug("ClientResponder::get() - Invalid queue name: null or empty string");
      setResponse("Invalid queue name: null or empty string");
    }
    else if (mqq == null)
    {
      mLogger.debug("ClientResponder::get() - Queue with name \"" + queue + "\" doesn't exist");
      setResponse("Queue with name \"" + queue + "\" doesn't exist");
    }
    else
    {
      result = mqq.get(timeout, interval);
      if (result == null)
      {
        mLogger.debug("ClientResponder::get() - No message found in queue " + queue);
        setResponse("No message found in queue " + queue);
      }
      else
      {
        mLogger.debug("ClientResponder::get() - Picked up message from " + queue + ". Message: " + StringUtils.asPrintableString(result));
        setResponse("");
      }
    }
    
    mLogger.debug("ClientResponder::get() - OUT");
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
    mLogger.debug("ClientResponder::put() - IN, Queue=" + queue);
    
    mLogger.debug("ClientResponder::put() - Message to put: " + StringUtils.asPrintableString(message));
    
    MqLocalQueue locq = mRepository.getLocalQueue(queue);
    MqLocalQueue dead = mRepository.getDeadQueue();
    if ((queue == null) || (queue.length() == 0))
    {
      mLogger.debug("ClientResponder::put() - Invalid queue name: null or empty string");
      setResponse("Invalid queue name: null or empty string");
      dead.put(message);
    }
    else if (locq == null)
    {
      mLogger.debug("ClientResponder::put() - Queue with name \"" + queue + "\" doesn't exist");
      setResponse("Queue with name \"" + queue + "\" doesn't exist");
      dead.put(message);
    }
    else
    {
      boolean success = locq.put(message);
      if (!success) dead.put(message);
      
      mLogger.debug("ClientResponder::put() - Message was put to queue " + queue);
      setResponse("");
      String user = message.getStringProperty(IMqConstants.cKasPropertyPutUserName, null);
      locq.setLastAccess(user, "put");
    }
    
    mLogger.debug("ClientResponder::put() - OUT");
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
