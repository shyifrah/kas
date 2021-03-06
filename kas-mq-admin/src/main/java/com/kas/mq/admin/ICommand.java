package com.kas.mq.admin;

import java.util.List;
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
   * 
   * @param text
   *   The command text
   */
  public abstract void parse(String text);
  
  /**
   * Execute the command using the specified {@link MqContextConnection}
   * 
   * @param conn
   *   The {@link MqContextConnection} that will be used to execute the command
   */
  public abstract void exec(MqContextConnection conn);

  /**
   * Print HELP screen for the specified command.
   */
  public abstract void help();
  
  /**
   * Get the list of command verbs acceptable by this command
   * 
   * @return
   *   the list of command verbs acceptable by this command
   */
  public abstract List<String> getCommandVerbs();
}
