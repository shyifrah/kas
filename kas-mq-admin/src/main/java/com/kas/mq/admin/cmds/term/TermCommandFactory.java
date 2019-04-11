package com.kas.mq.admin.cmds.term;

import com.kas.mq.admin.CommandFactory;

/**
 * TERMINATE command factory
 * 
 * @author Pippo
 */
public class TermCommandFactory extends CommandFactory
{
  public TermCommandFactory()
  {
    super(TermCommandFactory.class.getPackage().getName());
  }
  
  protected TermCommandFactory(String pkg)
  {
    super(pkg);
  }
}
