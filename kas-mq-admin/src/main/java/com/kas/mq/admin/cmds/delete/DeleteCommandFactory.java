package com.kas.mq.admin.cmds.delete;

import com.kas.mq.admin.CommandFactory;

/**
 * DELETE command factory
 * 
 * @author Pippo
 */
public class DeleteCommandFactory extends CommandFactory
{
  public DeleteCommandFactory()
  {
    super(DeleteCommandFactory.class.getPackage().getName());
  }
  
  protected DeleteCommandFactory(String pkg)
  {
    super(pkg);
  }
}
