package com.kas.mq.admin.cmds.define;

import com.kas.mq.admin.CommandFactory;

/**
 * DEFINE command factory
 * 
 * @author Pippo
 */
public class DefineCommandFactory extends CommandFactory
{
  public DefineCommandFactory()
  {
    super(DefineCommandFactory.class.getPackage().getName());
  }
  
  protected DefineCommandFactory(String pkg)
  {
    super(pkg);
  }
}
