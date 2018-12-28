package com.kas.mq.console.cmds;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import com.kas.infra.utils.ConsoleUtils;
import com.kas.mq.console.ICommand;

/**
 * A factory that generate commands based on their text
 * 
 * @author Pippo
 */
public class CommandFactory
{
  static private final String cPackageName = new CommandFactory().getClass().getPackage().getName();
  
  /**
   * Private Constructor
   */
  private CommandFactory()
  {
  }
  
  /**
   * Create a {@link ICommand}.<br>
   * Note that the only the verb and the sub-verb are upper cased to locate
   * the specific class, but the arguments should remain untouched. This is
   * because some arguments are case sensitive (e.g. PASSWORD).
   * 
   * @param text The command text
   * @return the newly created {@link ICommand}
   */
  static public ICommand create(String text)
  {
    ICommand cmd = null;
    
    if (text == null)
      return null;
    if (text.length() == 0)
      return null;
    
    text = text.replaceAll("\\(", " (").replaceAll("\\)", ") ");
    
    String [] tokens = text.split(" ");
    if (tokens.length >= 2)
    {
      String cmdVerb = tokens[0].toUpperCase();
      String subVerb = tokens[1].toUpperCase();
      String cmdArgs = text.substring(cmdVerb.length()).trim();
      
      StringBuffer clsNameBuffer = new StringBuffer()
        .append(cPackageName)
        .append('.')
        .append(cmdVerb.substring(0, 1))
        .append(cmdVerb.substring(1).toLowerCase())
        .append(subVerb.substring(0, 1))
        .append(subVerb.substring(1).toLowerCase())
        .append("CommandParser");
      
      cmd = newInstance(clsNameBuffer.toString(), cmdVerb, cmdArgs);
    }
    
    return cmd;
  }
  
  /**
   * Create a new instance of the desired command
   * 
   * @param name The class name of the command
   * @param verb The command verb
   * @param args The command arguments string
   * @return the command or {@code null} if an exception was thrown
   */
  static private ICommand newInstance(String name, String verb, String args)
  {
    ICommand cmd = null;
    
    Class<?> cls;
    try
    {
      cls = Class.forName(name);
      Constructor<?> ctor = cls.getConstructor(String.class, String.class);
      Object obj = ctor.newInstance(verb, args);
      cmd = (ICommand)obj;
    }
    catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassCastException e)
    {
      ConsoleUtils.writeln("Failde to instantiate command object. Error message: [%s]", e.getMessage());
    }
    
    return cmd;
  }
}
