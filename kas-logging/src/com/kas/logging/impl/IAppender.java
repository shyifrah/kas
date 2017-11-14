package com.kas.logging.impl;

import com.kas.infra.base.IInitializable;
import com.kas.infra.base.IObject;

public interface IAppender extends IObject, IInitializable
{
  public void write(String logger, String message, Throwable ex, ELogLevel level);
}
