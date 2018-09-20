package com.kas.mq.server.internal;

import com.kas.infra.base.IObject;
import com.kas.infra.base.UniqueId;

public interface ISession extends IObject
{
  public abstract UniqueId getSessionId();
  
  public abstract boolean isRunning();
}
