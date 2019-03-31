package com.kas.mq.admin;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
   * Construct this command factory
   */
  protected CommandFactory()
  {
    mPackageName = this.getClass().getPackage().getName();
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
    
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    if (classLoader == null)
      return;
    
    sLogger.trace("CommandFactory::init() - Current package is: [{}]", mPackageName);
    
    String pkgPath = mPackageName.replace('.', '/');
    URL url = classLoader.getResource(pkgPath);
    String path = url.getPath().substring("file:/".length());
    if (path.lastIndexOf('!') != -1)
      path = path.substring(0, path.lastIndexOf('!'));
    
    File urlFile = new File(path);
    sLogger.trace("CommandFactory::init() - URL file path is: [{}]", urlFile.getAbsolutePath());
    
    List<Class<?>> cmdClasses = null;
    if (urlFile.isDirectory())
    {
      sLogger.trace("CommandFactory::init() - URL file is a directory");
      cmdClasses = getDirClasses(urlFile);
    }
    else
    {
      sLogger.trace("CommandFactory::init() - URL file is a JAR file");
      cmdClasses = getJarClasses(urlFile);
    }
    
    
    for (Class<?> cmdClass : cmdClasses)
    {
      sLogger.trace("CommandFactory::init() - Processing class [{}]", cmdClass.getName());
      if (isCommandDrivenClass(cmdClass))
      {
        registerCommandClass(cmdClass);
      }
    }
    
    sLogger.trace("CommandFactory::init() - OUT");
  }
  
  private List<Class<?>> getJarClasses(File jar)
  {
    sLogger.trace("CommandFactory::getJarClasses() - IN, JAR=[{}]", jar.getAbsolutePath());
    
    List<Class<?>> result = new ArrayList<Class<?>>();
    
    ZipInputStream zip = null;
    try
    {
      zip = new ZipInputStream(new FileInputStream(jar.getAbsolutePath()));
    
      for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry())
      {
        sLogger.trace("CommandFactory::getJarClasses() - Current entry=[{}] Dir=[{}]", entry.getName(), entry.isDirectory());
        
        if ((!entry.isDirectory()) && (entry.getName().endsWith(".class")))
        {
          String cn = entry.getName().replace('/', '.');
          cn = cn.substring(0, cn.length()-".class".length());
          
          Class<?> cls = null;
          try
          {
            cls = Class.forName(cn);
            result.add(cls);
          }
          catch (Throwable e) {}
        }
      }
    }
    catch (Throwable e)
    {
      sLogger.error("Exceptiuon caught: ", e);
    }
    finally
    {
      if (zip != null)
      {
        try {
          zip.close();
        } catch (Throwable e) {}
      }
    }
    
    sLogger.trace("CommandFactory::getJarClasses() - OUT, JAR contains [{}] classes", result.size());
    return result;
  }
  
  private List<Class<?>> getDirClasses(File dir)
  {
    sLogger.trace("CommandFactory::getDirClasses() - IN, DIR=[{}]", dir.getAbsolutePath());
    
    List<Class<?>> result = new ArrayList<Class<?>>();
    
    File [] list = dir.listFiles();
    for (File file : list)
    {
      if (file.isDirectory())
      {
        List<Class<?>> subDirClasses = getDirClasses(file);
        result.addAll(subDirClasses);
      }
      else if (file.getAbsolutePath().endsWith(".class"))
      {
        String currPackage = dir.getAbsolutePath().replace('\\', '.');
        currPackage = currPackage.substring(currPackage.indexOf(mPackageName));
        String cn = currPackage + '.' + file.getName().substring(0, file.getName().lastIndexOf(".class"));
        try
        {
          Class<?> cls = Class.forName(cn);
          result.add(cls);
        }
        catch (Throwable e) {}
      }
    }
    
    sLogger.trace("CommandFactory::getDirClasses() - OUT, DIR contains [{}] classes", result.size());
    return result;
  }
  
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
        register(verb, cmd);
      
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
