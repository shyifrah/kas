package com.kas.mq.admin;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import com.kas.appl.AKasApp;
import com.kas.appl.AppLauncher;
import com.kas.infra.typedef.TokenDeque;
import com.kas.infra.utils.ConsoleUtils;
import com.kas.infra.utils.RunTimeUtils;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.admin.cmds.CliCommandFactory;
import com.kas.mq.admin.cmds.ICliCommand;
import com.kas.mq.impl.MqContext;

/**
 * KAS/MQ admin console
 * 
 * @author Pippo
 */
public class KasMqAdmin extends AKasApp 
{
  static final String cKasHome = "/build/install/kas-mq-admin";
  static final String cAppName = "KAS/MQ Admin Console";
  static final String cAdminPrompt = ConsoleUtils.RED + "KAS/MQ Admin> " + ConsoleUtils.RESET;
  
  static public void main(String [] args)
  {
    Map<String, String> defaults = new HashMap<String, String>();
    String kasHome = RunTimeUtils.getProperty(RunTimeUtils.cProductHomeDirProperty, System.getProperty("user.dir") + cKasHome);
    defaults.put(RunTimeUtils.cProductHomeDirProperty, kasHome);
    
    AppLauncher launcher = new AppLauncher(args, defaults);
    Map<String, String> settings = launcher.getSettings();
    
    KasMqAdmin app = new KasMqAdmin(settings);
    launcher.launch(app);
  }
  
  /**
   * A {@link MqContext} which will act as the client
   */
  private MqContext mClient = new MqContext(cAppName);
  
  /**
   * Construct the {@link KasMqAdmin} passing it the startup arguments
   * 
   * @param args The startup arguments
   */
  public KasMqAdmin(Map<String, String> args)
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
   * Run KAS/MQ admin CLI.<br>
   * <br>
   * The main logic is quite simple: keep reading commands from the command line until
   * it is terminated via the "exit" or SIGTERM signal.
   * 
   * @see IKasMqAppl#appExec()
   */
  public void appExec()
  {
    writeln(cAppName + " started");
    writeln(" ");
    
    try
    {
      boolean stop = false;
      while (!stop)
      {
        TokenDeque command = readClear(cAdminPrompt);
        if (command == null)
          stop = true;
        else
          stop = process(command);
      }
    }
    catch (NoSuchElementException e)
    {
      // do nothing
    }
    catch (Throwable e)
    {
      writeln(" ");
      writeln("Exception caught: ");
      e.printStackTrace();
    }
    
    writeln(" ");
    writeln(cAppName + " ended");
  }
  
  /**
   * Process a command represented by a queue of tokens.<br>
   * <br>
   * According to the first element in the queue - the command verb - we determine which type of Command object
   * should be created and then we execute it.
   * 
   * @param cmdWords The queue containing the command tokens
   * @param scanner The scanner, in case further interaction with the user is needed
   * @return {@code true} if an "exit" command was issued, {@code false} otherwise
   */
  private boolean process(TokenDeque cmdWords)
  {
    if (cmdWords.isEmpty() || cmdWords.peek().equals(""))
    {
      writeln(" ");
      return false;
    }
    
    String verb = cmdWords.peek();
    ICliCommand command = CliCommandFactory.newCommand(cmdWords, mClient);
    if (command == null)
    {
      writeln("Unknown command verb: \"" + verb + "\". Type HELP to see available commands");
      writeln(" ");
      return false;
    }
    
    return command.run();
  }
  
  /**
   * Writing a message to STDOUT.
   * 
   * @param message The message to print
   */
  protected void write(String message)
  {
    System.out.print(message);
  }
  
  /**
   * Writing a message to STDOUT. The message will be followed by a Newline character.
   * 
   * @param message The message to print
   */
  protected void writeln(String message)
  {
    System.out.println(message);
  }
  
  /**
   * Reading a command (a single line) from STDIN and return it as a queue of tokens.<br>
   * The read input is clear-text.
   * 
   * @param prompt The prompt message to print to the user
   * @return a {@link TokenDeque} containing command's tokens 
   */
  protected TokenDeque readClear(String prompt)
  {
    TokenDeque tdq = null;
    try
    {
      String input = ConsoleUtils.readClearText(prompt);
      tdq = new TokenDeque(input);
    }
    catch (Throwable e) {}
    return tdq;
  }
  
  /**
   * Reading a command (a single line) from STDIN and return it as a queue of tokens.<br>
   * The read input is masked text.
   * 
   * @param prompt The prompt message to print to the user
   * @return a {@link TokenDeque} containing command's tokens 
   */
  protected TokenDeque readMasked(String prompt)
  {
    TokenDeque tdq = null;
    try
    {
      String input = ConsoleUtils.readMaskedText(prompt);
      tdq = new TokenDeque(input);
    }
    catch (Throwable e) {}
    return tdq;
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
      .append(pad).append("  Context=(").append(mClient.toPrintableString(0)).append(")\n");
    sb.append(pad).append(")\n");
    return sb.toString();
  }
}
