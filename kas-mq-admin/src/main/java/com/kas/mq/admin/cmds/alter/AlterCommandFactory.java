package com.kas.mq.admin.cmds.alter;

import com.kas.mq.admin.CommandFactory;

/**
 * ALTER command factory
 * 
 * @author Pippo
 */
public class AlterCommandFactory extends CommandFactory
{
  public AlterCommandFactory()
  {
    this(AlterCommandFactory.class.getPackage().getName());
  }
  
  protected AlterCommandFactory(String pkg)
  {
    super(pkg);
  }
}
