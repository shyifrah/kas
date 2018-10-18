package com.kas.mq.admin;

import java.util.Map;
import java.util.NoSuchElementException;
import com.kas.appl.AKasAppl;
import com.kas.infra.base.ConsoleLogger;
import com.kas.infra.logging.IBaseLogger;
import com.kas.infra.typedef.TokenDeque;
import com.kas.infra.utils.ConsoleUtils;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.MqConfiguration;
import com.kas.mq.admin.commands.CliCommandFactory;
import com.kas.mq.admin.commands.ICliCommand;
import com.kas.mq.impl.MqContext;

/**
 * MQ administration CLI.
 * 
 * @author Pippo
 */
public class KasMqAdmin extends AKasAppl 
{
  static final String cAppName = "KAS/MQ admin CLI";
  static final String cAdminPrompt = ConsoleUtils.RED + "KAS/MQ Admin> " + ConsoleUtils.RESET;
  
  static IBaseLogger sStartupLogger = new ConsoleLogger(KasMqAdmin.class.getName());
  
  
  /**
   * KAS/MQ server's configuration
   */
  private MqConfiguration mConfig = null;
  
  /**
   * A {@link MqContext} which will act as the client
   */
  private MqContext mClient = new MqContext();
  
  /**
   * Construct the {@link KasMqAdmin} passing it the startup arguments
   * 
   * @param args The startup arguments
   */
  public KasMqAdmin(Map<String, String> args)
  {
    super(args);
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
   * Initializing the KAS/MQ admin CLI.<br>
   * <br>
   * Initialization consisting of:
   * - configuration initialization
   * 
   * @return {@code true} if initialization completed successfully, {@code false} otherwise 
   */
  public boolean appInit()
  {
    boolean init = true;
    
    mConfig = new MqConfiguration();
    mConfig.init();
    if (!mConfig.isInitialized())
    {
      init = false;
    }
    else
    {
      mConfig.register(this);
    }
    
    return init;
  }
  
  /**
   * Terminating the KAS/MQ admin CLI.<br>
   * <br>
   * Termination consisting of:
   * - configuration termination
   * 
   * @return {@code true} if initialization completed successfully, {@code false} otherwise 
   */
  public synchronized boolean appTerm()
  {
    mConfig.term();
    return true;
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
   * The read command is clear-text.
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
   * The read command is masked text.
   * 
   * @param prompt The prompt message to print to the user
   * @return a {@link TokenDeque} containing command's tokens 
   */
  protected TokenDeque readMasked(String prompt)
  {
    String input = ConsoleUtils.readMaskedText(prompt);
    return new TokenDeque(input);
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
      .append(pad).append("  Config=(").append(StringUtils.asPrintableString(mConfig)).append(")\n")
      .append(pad).append("  ShutdownHook=(").append(StringUtils.asPrintableString(mShutdownHook)).append(")\n")
      .append(pad).append("  Context=(").append(mClient.toPrintableString(0)).append(")\n");
    sb.append(pad).append(")\n");
    return sb.toString();
  }
}
