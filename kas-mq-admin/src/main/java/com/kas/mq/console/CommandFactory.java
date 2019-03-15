package com.kas.mq.console;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import com.kas.infra.utils.ConsoleUtils;
import com.kas.infra.utils.helpers.ThrowableFormatter;

/**
 * A base factory for commands
 * 
 * @author Pippo
 */
public class CommandFactory implements ICommandFactory
{
  /**
   * A {@link TreeMap} to map command verbs with {@link ICommand} that handles it
   */
  protected Map<String, ICommand> mCommandVerbs = new TreeMap<String, ICommand>();
  
  /**
   * Initialize the command factory.<br>
   * We scan the contents of the directory represented by this package.
   * If a class file is found that designates a {@link ICommand}, we instantiate it
   * and then call this class' {@link ICommand#init(ICommandFactory)} method.
   */
  public void init()
  {
    String pkgname = this.getClass().getPackage().getName();
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    if (classLoader == null)
      return;
    
    String path = pkgname.replace('.', '/');
    
    URL resource = classLoader.getResource(path);
    File dir = new File(resource.getFile());
    
    File [] files = dir.listFiles();
    if (files == null)
      return;
    
    for (File file : files)
    {
      if (file.isFile() && (file.getName().endsWith(".class")))
      {
        try
        {
          String cn = pkgname + '.' + file.getName().substring(0, file.getName().lastIndexOf(".class"));
          Class<?> cls = Class.forName(cn);
          
          if (isCommandDrivenClass(cls))
          {
            Constructor<?> ctor = cls.getDeclaredConstructor();
            boolean accessible = ctor.isAccessible();
            ctor.setAccessible(true);
            Object obj = ctor.newInstance();
            ctor.setAccessible(accessible);
            ICommand cmd = (ICommand)obj;
            
            List<String> verbs = cmd.getCommandVerbs();
            for (String verb : verbs)
              register(verb, cmd);
          }
        }
        catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {}
      }
    }
  }

  /**
   * Test if {@code cls} is a driven, non-abstract instance of {@link ICommand}
   * 
   * @param cls The class object to test
   * @return {@code true} if {@code cls} is an {@link ICommand}, {@code false} otherwise
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
   * @param verb The command verb
   * @param cmd The command object that will handle commands beginning with {@code verb}
   */
  public void register(String verb, ICommand cmd)
  {
    synchronized (mCommandVerbs)
    {
      mCommandVerbs.put(verb, cmd);
    }
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
