package com.kas.mq.admin;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import com.kas.infra.base.ConsoleLogger;
import com.kas.infra.logging.IBaseLogger;
import com.kas.infra.typedef.TokenDeque;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.AKasMqAppl;
import com.kas.mq.IKasMqAppl;
import com.kas.mq.admin.commands.CliCommandFactory;
import com.kas.mq.admin.commands.ICliCommand;
import com.kas.mq.impl.internal.MqClientImpl;

/**
 * MQ administration CLI.
 * 
 * @author Pippo
 */
public class KasMqAdmin extends AKasMqAppl 
{
  static IBaseLogger sStartupLogger = new ConsoleLogger(KasMqAdmin.class.getName());
  
  /**
   * A {@link MqClientImpl} which will act as the client
   */
  private MqClientImpl mClientImpl = new MqClientImpl();
  
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
   * Initializing the KAS/MQ admin CLI.<br>
   * <br>
   * Initialization consisting of:
   * - super class initialization
   * 
   * @return {@code true} if initialization completed successfully, {@code false} otherwise 
   */
  public boolean init()
  {
    boolean init = super.init();
    if (init)
    {
      mLogger.info("KAS/MQ base application initialized successfully");
    }
    
    String message = "KAS/MQ admin CLI V" + mVersion.toString() + (init ? " started successfully" : " failed to start");
    sStartupLogger.info(message);
    mLogger.info(message);
    return init;
  }
  
  /**
   * Terminating the KAS/MQ admin CLI.<br>
   * <br>
   * Termination consisting of:
   * - super class termination
   * 
   * @return {@code true} if initialization completed successfully, {@code false} otherwise 
   */
  public synchronized boolean term()
  {
    mLogger.info("KAS/MQ admin CLI termination in progress");
    return super.term();
  }
  
  /**
   * Run KAS/MQ admin CLI.<br>
   * <br>
   * The main logic is quite simple: keep reading commands from the command line until
   * it is terminated via the "exit" or SIGTERM signal.
   * 
   * @return {@code true} if main thread should execute the termination, {@code false} otherwise
   * 
   * @see IKasMqAppl#run()
   */
  public boolean run()
  {
    writeln("KAS/MQ Admin Command Processor started");
    writeln(" ");
    
    Scanner scanner = null;
    try
    {
      scanner = new Scanner(System.in);
      TokenDeque command = read(scanner);
      boolean stop = false;
      while (!stop)
      {
        stop = process(scanner, command);
        if (!stop)
        {
          command = read(scanner);
        }
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
    finally
    {
      if (scanner != null)
        scanner.close();
    }
    
    writeln(" ");
    writeln("KAS/MQ Admin Command Processor ended");
    return !mShutdownHook.isRunning();
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
  private boolean process(Scanner scanner, TokenDeque cmdWords)
  {
    if (cmdWords.isEmpty() || cmdWords.peek().equals(""))
    {
      writeln(" ");
      return false;
    }
    
    String verb = cmdWords.peek();
    ICliCommand command = CliCommandFactory.newCommand(scanner, cmdWords, mClientImpl);
    if (command == null)
    {
      writeln("Unknown command verb: \"" + verb + "\". Type HELP to see available commands");
      writeln(" ");
      return false;
    }
    
    return command.run();
  }
  
  /**
   * Reading a command (one line) from STDIN and return it as a queue of tokens.
   * 
   * @param scanner The {@link Scanner} object associated with STDIN
   * 
   * @return a queue in which each element is a token from the read line
   */
  private TokenDeque read(Scanner scanner)
  {
    write("KAS/MQ Admin> ");
    String cmd = scanner.nextLine();
    return new TokenDeque(cmd);
  }
  
  /**
   * Writing a message to STDOUT.
   * 
   * @param message The message to print
   */
  private void write(String message)
  {
    System.out.print(message);
  }
  
  /**
   * Writing a message to STDOUT. The message will be followed by a Newline character.
   * 
   * @param message The message to print
   */
  private void writeln(String message)
  {
    System.out.println(message);
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
      .append(pad).append("  ClientImpl=(").append(mClientImpl.toPrintableString(0)).append(")\n");
    sb.append(pad).append(")\n");
    return sb.toString();
  }
}
