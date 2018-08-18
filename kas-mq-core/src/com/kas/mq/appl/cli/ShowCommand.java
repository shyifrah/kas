//package com.kas.mq.appl.cli;
//
//import java.util.Scanner;
//import java.util.Set;
//import java.util.TreeSet;
//import com.kas.mq.client.IClient;
//import com.kas.mq.internal.TokenDeque;
//
///**
// * A SHOW command
// * 
// * @author Pippo
// */
//public class ShowCommand extends ACliCommand
//{
//  static public final Set<String> sCommandVerbs = new TreeSet<String>();
//  static
//  {
//    sCommandVerbs.add("SHOW");
//  }
//  
//  /**
//   * Construct a {@link ShowCommand} passing the command arguments and the client object
//   * that will perform actions on behalf of this command.
//   * 
//   * @param scanner A scanner to be used in case of further interaction is needed 
//   * @param args The command arguments specified when command was entered
//   * @param client The client that will perform the actual disconnection
//   */
//  protected ShowCommand(Scanner scanner, TokenDeque args, IClient client)
//  {
//    super(scanner, args, client);
//  }
//
//  /**
//   * Display help screen for this command.
//   */
//  public void help()
//  {
//    if (mCommandArgs.size() > 0)
//    {
//      writeln("Execssive command arguments are ignored for HELP SHOW");
//      writeln(" ");
//    }
//    
//    writeln("Purpose: ");
//    writeln(" ");
//    writeln("     Display information regarding the administrative console session.");
//    writeln(" ");
//    writeln("Format: ");
//    writeln(" ");
//    writeln("     >>--- SHOW ---><");
//    writeln(" ");
//    writeln("Description: ");
//    writeln(" ");
//    writeln("     The command will display some basic information regarding the user and the remote"); 
//    writeln("     KAS/MQ manager.");
//    writeln(" ");
//    writeln("Examples:");
//    writeln(" ");
//    writeln("     Display information about current session:");
//    writeln("          KAS/MQ Admin> SHOW");
//    writeln(" ");
//  }
//  
//  /**
//   * A show command will give some information regarding the current user and the remote KAS/MQ manager.<br>
//   * <br>
//   * No arguments are passed to the command. If entered, the command will fail.
//   * 
//   * @return {@code false} always because there is no way that this command will terminate the command processor.
//   */
//  public boolean run()
//  {
//    if (mCommandArgs.size() > 0)
//    {
//      mClient.setResponse("Excessive argument specified: \"" + mCommandArgs.poll() + "\". Type HELP SHOW to see available command options");
//      return false;
//    }
//    
//    writeln(mClient.getResponse());
//    writeln(" ");
//    return false;
//  }
//}
