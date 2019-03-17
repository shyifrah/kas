package com.kas.mq.console;

import java.util.HashMap;
import java.util.Map;
import com.kas.appl.AKasApp;
import com.kas.appl.AppLauncher;
import com.kas.infra.base.IObject;
import com.kas.infra.utils.ConsoleUtils;
import com.kas.infra.utils.RunTimeUtils;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.console.cmds.MainCommandFactory;
import com.kas.mq.internal.MqContextConnection;

/**
 * KAS/MQ admin console
 * 
 * @author Pippo
 */
public class KasMqCons extends AKasApp 
{
  static final String cKasHome = "/build/install/kas-mq-admin";
  static final String cAppName = "KAS/MQ Admin Console";
  static final String cMainPrompt = ConsoleUtils.RED + "KAS/MQ Admin> " + ConsoleUtils.RESET;
  static final String cContPrompt = ConsoleUtils.RED + "            > " + ConsoleUtils.RESET;
  static final String cExitCommand = "EXIT";
  static final String cCommandTerminator = ";";
  
  static public void main(String [] args)
  {
    Map<String, String> defaults = new HashMap<String, String>();
    String kasHome = RunTimeUtils.getProperty(RunTimeUtils.cProductHomeDirProperty, System.getProperty("user.dir") + cKasHome);
    defaults.put(RunTimeUtils.cProductHomeDirProperty, kasHome);
    
    AppLauncher launcher = new AppLauncher(args, defaults);
    Map<String, String> settings = launcher.getSettings();
    
    KasMqCons app = new KasMqCons(settings);
    launcher.launch(app);
  }
  
  /**
   * A {@link MqContextConnection} which will act as the client
   */
  private MqContextConnection mConnection = new MqContextConnection(cAppName);
  
  /**
   * The main command factory
   */
  private MainCommandFactory mCommandFactory = MainCommandFactory.getInstance();
  
  /**
   * Construct the {@link KasMqCons} passing it the startup arguments
   * 
   * @param args The startup arguments
   */
  public KasMqCons(Map<String, String> args)
  {
  }
  
  /**
   * Get the application name
   * 
   * @return the application name 
   */
  public String getAppName()
  {
    return cAppName;
  }
  
  /**
   * Run KAS/MQ administrative console CLI.<br>
   * The main logic is quite simple: keep reading commands from the command line until
   * it is terminated via the "exit" or SIGTERM signal.
   */
  public void appExec()
  {
    ConsoleUtils.writeln(cAppName + " started");
    ConsoleUtils.writeln(" ");
    
    mCommandFactory.init();
    
    boolean isExitCommand  = false;
    boolean isNewCommand   = true;
    String prompt = null;
    StringBuffer buffer = new StringBuffer();
    
    while (!isExitCommand)
    {
      prompt = isNewCommand ? cMainPrompt : cContPrompt;
      
      String line = ConsoleUtils.readClearText(prompt);
      line = line.trim();
      if (line.length() > 0)
      {
        if (line.equalsIgnoreCase(cExitCommand))
          line = line + ";";
        
        buffer.append(line);
        if (line.endsWith(cCommandTerminator))
        {
          isExitCommand = process(buffer.toString());
          
          isNewCommand = true;
          buffer = new StringBuffer();
        }
        else
        {
          isNewCommand = false;
          buffer.append(' ');
        }
      }
    }
    
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln(cAppName + " ended");
  }
  
  /**
   * Process a command text
   * 
   * @param text
   *   The text of the command entered 
   * @return
   *   {@code true} if admin console should terminate, {@code false} otherwise
   */
  private boolean process(String text)
  {
    text = text.substring(0, text.length()-1);
    text = text.trim();
    if (text == null)
      return false;
    if (text.length() == 0)
      return false;
    
    text = text.replaceAll("\\(", " (").replaceAll("\\)", ") ");
    
    ICommand cmd = mCommandFactory.newCommand(text);
    if (cmd == null)
    {
      ConsoleUtils.writeln("Unknown command: [%s]", text);
      return false;
    }

    try
    {
      cmd.exec(mConnection);
    }
    catch (IllegalArgumentException e)
    {
      ConsoleUtils.writelnRed("Error: %s", e.getMessage());
      return false;
    }
    catch (RuntimeException e)
    {
      return true;
    }
    catch (Throwable e)
    {
      ConsoleUtils.writelnRed("Exception: Class=[%s], Message=[%s]", e.getClass().getName(), e.getMessage());
      return false;
    }
    
    return false;
  }
  
  /**
   * Returns the {@link IObject} string representation.
   * 
   * @param level
   *   The required padding level
   * @return
   *   the string representation with the specified level of padding
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuilder sb = new StringBuilder();
    sb.append(name()).append("(\n")
      .append(pad).append("  ShutdownHook=(").append(StringUtils.asPrintableString(mShutdownHook)).append(")\n")
      .append(pad).append("  Context=(").append(mConnection.toPrintableString(0)).append(")\n");
    sb.append(pad).append(")\n");
    return sb.toString();
  }
}
