package com.kas.mq.console.cmds;

import com.kas.mq.console.CommandFactory;

/**
 * A factory that generate commands based on their text
 * 
 * @author Pippo
 */
public class MainCommandFactory extends CommandFactory
{
  /**
   * Singleton instance
   */
  static private MainCommandFactory sInstance = new MainCommandFactory();
  
  /**
   * Get the singleton
   * 
   * @return the singleton instance
   */
  static public MainCommandFactory getInstance()
  {
    return sInstance;
  }
  
  /**
   * Private Constructor
   */
  private MainCommandFactory()
  {
  }
}
