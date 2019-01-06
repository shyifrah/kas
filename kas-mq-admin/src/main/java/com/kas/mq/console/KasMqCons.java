package com.kas.mq.console;

import java.util.HashMap;
import java.util.Map;
import com.kas.appl.AKasApp;
import com.kas.appl.AppLauncher;
import com.kas.infra.utils.ConsoleUtils;
import com.kas.infra.utils.RunTimeUtils;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.console.cmds.CommandFactory;
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
   * <br>
   * The main logic is quite simple: keep reading commands from the command line until
   * it is terminated via the "exit" or SIGTERM signal.
   * 
   * @see IKasMqAppl#appExec()
   */
  public void appExec()
  {
    ConsoleUtils.writeln(cAppName + " started");
    ConsoleUtils.writeln(" ");
    
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
        if ((isNewCommand) && (line.equalsIgnoreCase(cExitCommand)))
        {
          isExitCommand = true;
        }
        else
        {
          buffer.append(line);
          
          if (line.endsWith(cCommandTerminator))
          {
            isNewCommand = true;
            isExitCommand = process(buffer.toString());
            buffer = new StringBuffer();
          }
          else
          {
            isNewCommand = false;
            buffer.append(' ');
          }
        }
      }
    }
    
    ConsoleUtils.writeln(" ");
    ConsoleUtils.writeln(cAppName + " ended");
  }
  
  /**
   * Process a command text
   * 
   * @param text The text of the command entered 
   * @return {@code true} if admin console should terminate, {@code false} otherwise
   */
  private boolean process(String text)
  {
    text = text.substring(0, text.length()-1);
    text = text.trim();
    
    ICommand cmd = CommandFactory.getInstance().newCommand(text);
    if (cmd == null)
    {
      return false;
    }
    
    try
    {
      cmd.parse();
      ConsoleUtils.writeln("Parsed command: " + cmd.toString());
    }
    catch (IllegalArgumentException e)
    {
      ConsoleUtils.writeln("Error: " + e.getMessage());
      return false;
    }
    
    try
    {
      cmd.exec(mConnection);
    }
    catch (Throwable e)
    {
      ConsoleUtils.writeln("Error: " + e.getMessage());
      return false;
    }
    
    return false;
  }
  
  /**
   * Get the object's detailed string representation
   * 
   * @param level The string padding level
   * @return the string representation with the specified level of padding
   * 
   * @see com.kas.infra.base.IObject#toPrintableString(int)
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
