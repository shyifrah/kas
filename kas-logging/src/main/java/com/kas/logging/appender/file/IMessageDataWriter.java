package com.kas.logging.appender.file;

import com.kas.infra.base.IObject;

/**
 * Writing a message described by a {@link MessageData} object
 * 
 * @author Pippo
 */
public interface IMessageDataWriter extends IObject
{
  public void write(MessageData md);
}
