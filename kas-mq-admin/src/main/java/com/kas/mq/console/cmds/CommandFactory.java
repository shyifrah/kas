package com.kas.mq.console.cmds;

import com.kas.mq.console.AFactory;
import com.kas.mq.console.ICommand;

/**
 * A factory that generate commands based on their text
 * 
 * @author Pippo
 */
public class CommandFactory extends AFactory
{
  /**
   * Singleton instance
   */
  static private CommandFactory sInstance = new CommandFactory();
  
  /**
   * Get the singleton
   * 
   * @return the singleton instance
   */
  static public CommandFactory getInstance()
  {
    return sInstance;
  }
  
  /**
   * Private Constructor
   */
  private CommandFactory()
  {
  }
  
  /**
   * Get a {@link ICommand} object to handle the new command text.<br>
   * Note that the only the verb upper-cased to locate the specific class,
   * but the arguments should remain untouched. This is because some arguments
   * are case sensitive (e.g. PASSWORD).
   * 
   * @param cmdText The command text
   * @return the {@link ICommand} that will handle the command text
   */
  public ICommand newCommand(String cmdText)
  {
    ICommand cmd = null;
    String text = cmdText.trim();
    if (text == null)
      return null;
    if (text.length() == 0)
      return null;
    
    text = cmdText.replaceAll("\\(", " (").replaceAll("\\)", ") ");
    
    String [] tokens = text.split(" ");
    if (tokens.length > 0)
    {
      String verb = tokens[0].toUpperCase();
      cmd = mCommandVerbs.get(verb);
      if (cmd != null)
      {
        String reminder = text.substring(verb.length()).trim();
        cmd.setText(reminder);
      }
    }
    
    return cmd;
  }
}
