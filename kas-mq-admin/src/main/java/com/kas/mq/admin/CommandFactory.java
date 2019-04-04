package com.kas.mq.admin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kas.infra.classes.ClassPath;
import com.kas.infra.utils.ConsoleUtils;
import com.kas.infra.utils.ThrowableFormatter;

/**
 * A base factory for commands
 * 
 * @author Pippo
 */
public class CommandFactory implements ICommandFactory
{
  /**
   * Logger
   */
  static private Logger sLogger = LogManager.getLogger(CommandFactory.class);
  
  /**
   * A {@link TreeMap} to map command verbs with {@link ICommand} that handles it
   */
  protected Map<String, ICommand> mCommandVerbs = new TreeMap<String, ICommand>();
  
  /**
   * The name of this package
   */
  protected String mPackageName;
  
  /**
   * Class path data extractor
   */
  protected ClassPath mClassPath;
  
  /**
   * Construct this command factory
   */
  protected CommandFactory()
  {
    mPackageName = this.getClass().getPackage().getName();
    mClassPath = ClassPath.getInstance();
  }
  
  /**
   * Initialize the command factory.<br>
   * We scan the contents of the directory represented by this package.
   * If a class file is found that designates a {@link ICommand}, we instantiate it
   * and then call this class' {@link ICommand#init(ICommandFactory)} method.
   */
  public void init()
  {
    sLogger.trace("CommandFactory::init() - IN");
    
    sLogger.trace("CommandFactory::init() - Getting list of classes in current package: {}", mPackageName);
    Map<String, Class<?>> classes = mClassPath.getPackageClasses(mPackageName, false);
    
    for (Map.Entry<String, Class<?>> entry : classes.entrySet())
    {
      String cn = entry.getKey();
      Class<?> cls = entry.getValue();
      
      sLogger.debug("CommandFactory::init() - Processing class [{}]", cn);
      if (isCommandDrivenClass(cls))
        registerCommandClass(cls);
    }
    
    sLogger.trace("CommandFactory::init() - OUT");
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
        synchronized (mCommandVerbs)
        {
          mCommandVerbs.put(verb, cmd);
        }
      }
      
      success = true;
    }
    catch (Throwable e) {}
    
    sLogger.trace("CommandFactory::registerCommandClass() - OUT, Success=[{}]", success);
    return success;
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
   * Register a command verb with a specified command object
   * 
   * @param verb
   *   The command verb
   * @param cmd
   *   The command object that will handle commands beginning with {@code verb}
   */
  public void register(String verb, ICommand cmd)
  {
    
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
    ICommand cmd = mCommandVerbs.get(verb);
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
