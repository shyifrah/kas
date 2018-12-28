package com.kas.mq.console;

import com.kas.mq.internal.MqContextConnection;

/**
 * A command interface
 * 
 * @author Pippo
 */
public interface ICommand
{
  /**
   * Main method: parsing the command text
   */
  public abstract void parse();
  
  /**
   * Execute the command using the specified {@link MqContextConnection}
   * 
   * @param conn The {@link MqContextConnection} that will be used to execute the command
   */
  public abstract void exec(MqContextConnection conn);
}
