package com.kas.mq.admin.cmds.query;

import com.kas.mq.admin.CommandFactory;

/**
 * QUERY command factory
 * 
 * @author Pippo
 */
public class QueryCommandFactory extends CommandFactory
{
  public QueryCommandFactory()
  {
    super(QueryCommandFactory.class.getPackage().getName());
  }
  
  protected QueryCommandFactory(String pkg)
  {
    super(pkg);
  }
}
