package com.kas.mq.admin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kas.infra.utils.ConsoleUtils;
import com.kas.infra.utils.ThrowableFormatter;
import com.kas.mq.internal.PackageRegistrar;

/**
 * A base factory for commands
 * 
 * @author Pippo
 */
public class CommandFactory extends PackageRegistrar<String, ICommand> implements ICommandFactory
{
  /**
   * Logger
   */
  static private Logger sLogger = LogManager.getLogger(CommandFactory.class);
  
  /**
   * Construct this command factory
   * 
   * @param pkg
   *   The name of the package
   */
  protected CommandFactory(String pkg)
  {
    super(pkg);
  }
  
  /**
   * Register class {@code cls}.
   * <br>
   * We test {@code cls} to be a {@link ICommand}. If it is, we register it within the factory.
   * 
   * @param cls
   *   The class that needs initialization.
   */
  protected void register(Class<?> cls)
  {
    if (isCommandDrivenClass(cls))
    {
      registerCommandClass(cls);
    }
  }
  
  /**
   * Test if {@code cls} is a driven, non-abstract instance of {@link ICommand}
   * 
   * @param cls
   *   The class object to test
   * @return
   *   {@code true} if {@code cls} is an {@link ICommand}, {@code false} otherwise
   */
  public boolean isCommandDrivenClass(Class<?> cls)
  {
    int mods = cls.getModifiers();
    if (!ICommand.class.isAssignableFrom(cls))
      return false;
    if (Modifier.isAbstract(mods))
      return false;
    if (Modifier.isInterface(mods))
      return false;
    return true;
  }
  
  /**
   * Registering a {@code cls} with its verbs
   * 
   * @param cls
   *   The command class implementing {@link ICommand} interface
   * @return
   *   {@code true} if command registered successfully, {@code false} otherwise
   */
  private boolean registerCommandClass(Class<?> cls)
  {
    sLogger.trace("CommandFactory::registerCommandClass() - IN, Class=[{}]", cls.getName());
    
    boolean success = false;
    try
    {
      Constructor<?> ctor = cls.getDeclaredConstructor();
      boolean accessible = ctor.isAccessible();
      ctor.setAccessible(true);
      Object obj = ctor.newInstance();
      ctor.setAccessible(accessible);
      ICommand cmd = (ICommand)obj;
      
      List<String> verbs = cmd.getCommandVerbs();
      for (String verb : verbs)
      {
        sLogger.debug("CommandFactory::registerCommandClass() - Registering Verb=[{}] with Command=[{}]", verb, cls.getName());
        register(verb, cmd);
      }
      
      success = true;
    }
    catch (Throwable e) {}
    
    sLogger.trace("CommandFactory::registerCommandClass() - OUT, Success=[{}]", success);
    return success;
  }

  /**
   * Get a {@link ICommand} object to handle the new command text.<br>
   * Note that the only the verb upper-cased to locate the specific class,
   * but the arguments should remain untouched. This is because some arguments
   * are case sensitive (e.g. PASSWORD).
   * 
   * @param cmdText
   *   The command text
   * @return
   *   the {@link ICommand} that will handle the command text
   */
  public ICommand newCommand(String cmdText)
  {
    String [] tokens = cmdText.split(" ");
    if (tokens.length == 0)
    {
      ConsoleUtils.writeln("Missing command verb");
      return null;
    }
    
    String verb = tokens[0].toUpperCase();
    ICommand cmd = mRegistration.get(verb);
    if (cmd != null)
    {
      String reminder = cmdText.substring(verb.length()).trim();
      try
      {
        cmd.parse(reminder);
        ConsoleUtils.writeln(cmd.toString());
      }
      catch (IllegalArgumentException e)
      {
        ConsoleUtils.writelnRed("Error: %s", e.getMessage());
        cmd = null;
      }
      catch (Throwable e)
      {
        ConsoleUtils.writelnRed("Exception: %s", new ThrowableFormatter(e).toString());
        cmd = null;
      }
    }    
    
    return cmd;
  }
}
