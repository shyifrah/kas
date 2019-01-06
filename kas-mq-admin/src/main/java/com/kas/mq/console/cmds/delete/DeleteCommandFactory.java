package com.kas.mq.console.cmds.delete;

import com.kas.infra.utils.ConsoleUtils;
import com.kas.mq.console.AFactory;
import com.kas.mq.console.ICommand;

/**
 * Delete command factory
 * 
 * @author Pippo
 */
public class DeleteCommandFactory extends AFactory
{
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
    if (text.length() == 0)
      return null;
    
    String [] tokens = text.split(" ");
    if (tokens.length < 2)
    {
      ConsoleUtils.writeln("Unknown command: [DELETE %s]", text);
    }
    else
    {
      String verb = tokens[0].toUpperCase();
      cmd = mCommandVerbs.get(verb);
      String reminder = text.substring(verb.length()).trim();
      cmd.setText(reminder);
    }
    
    return cmd;
  }
}
