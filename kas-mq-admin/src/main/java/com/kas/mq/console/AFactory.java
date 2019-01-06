package com.kas.mq.console;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A base factory for commands
 * 
 * @author Pippo
 */
public class AFactory implements IFactory
{
  /**
   * A {@link TreeMap} to map command verbs with {@link ICommand} that handles it
   */
  protected Map<String, ICommand> mCommandVerbs = new TreeMap<String, ICommand>();
  
  /**
   * Initialize the command factory.<br>
   * We scan the contents of the directory represented by this package.
   * If a class file is found that designates a {@link ICommand}, we instantiate it
   * and then call this class' {@link ICommand#init(IFactory)} method.
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
}
