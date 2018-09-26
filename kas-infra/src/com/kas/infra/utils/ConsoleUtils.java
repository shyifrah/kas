package com.kas.infra.utils;

import java.io.Console;
import java.io.PrintStream;
import java.util.Scanner;

public class ConsoleUtils
{
  /**
   * Get an indication whether the console supports colored texts
   * 
   * @return {@code true} if it does, {@code false} otherwise
   */
  static public boolean isColoredTextSupported()
  {
    return OsUtils.getOsType() == OsUtils.EOsType.cUnix;
  }
  
  /**
   * Reset text
   */
  public static final String RESET = isColoredTextSupported() ? "\033[0m" : "";  // Text Reset

  /**
   * Colored text
   */
  public static final String BLACK   = isColoredTextSupported() ? "\033[0;30m" : "";  // BLACK
  public static final String RED     = isColoredTextSupported() ? "\033[0;31m" : "";  // RED
  public static final String GREEN   = isColoredTextSupported() ? "\033[0;32m" : "";  // GREEN
  public static final String YELLOW  = isColoredTextSupported() ? "\033[0;33m" : "";  // YELLOW
  public static final String BLUE    = isColoredTextSupported() ? "\033[0;34m" : "";  // BLUE
  public static final String PURPLE  = isColoredTextSupported() ? "\033[0;35m" : "";  // PURPLE
  public static final String CYAN    = isColoredTextSupported() ? "\033[0;36m" : "";  // CYAN
  public static final String WHITE   = isColoredTextSupported() ? "\033[0;37m" : "";  // WHITE

  /**
   * Bold text
   */
  public static final String BLACK_BOLD  = isColoredTextSupported() ? "\033[1;30m" : "";  // BLACK
  public static final String RED_BOLD    = isColoredTextSupported() ? "\033[1;31m" : "";  // RED
  public static final String GREEN_BOLD  = isColoredTextSupported() ? "\033[1;32m" : "";  // GREEN
  public static final String YELLOW_BOLD = isColoredTextSupported() ? "\033[1;33m" : "";  // YELLOW
  public static final String BLUE_BOLD   = isColoredTextSupported() ? "\033[1;34m" : "";  // BLUE
  public static final String PURPLE_BOLD = isColoredTextSupported() ? "\033[1;35m" : "";  // PURPLE
  public static final String CYAN_BOLD   = isColoredTextSupported() ? "\033[1;36m" : "";  // CYAN
  public static final String WHITE_BOLD  = isColoredTextSupported() ? "\033[1;37m" : "";  // WHITE

  /**
   * Underlined text
   */
  public static final String BLACK_UNDERLINED  = isColoredTextSupported() ? "\033[4;30m" : "";  // BLACK
  public static final String RED_UNDERLINED    = isColoredTextSupported() ? "\033[4;31m" : "";  // RED
  public static final String GREEN_UNDERLINED  = isColoredTextSupported() ? "\033[4;32m" : "";  // GREEN
  public static final String YELLOW_UNDERLINED = isColoredTextSupported() ? "\033[4;33m" : "";  // YELLOW
  public static final String BLUE_UNDERLINED   = isColoredTextSupported() ? "\033[4;34m" : "";  // BLUE
  public static final String PURPLE_UNDERLINED = isColoredTextSupported() ? "\033[4;35m" : "";  // PURPLE
  public static final String CYAN_UNDERLINED   = isColoredTextSupported() ? "\033[4;36m" : "";  // CYAN
  public static final String WHITE_UNDERLINED  = isColoredTextSupported() ? "\033[4;37m" : "";  // WHITE

  /**
   * Text's background
   */
  public static final String BLACK_BACKGROUND  = isColoredTextSupported() ? "\033[40m" : "";  // BLACK
  public static final String RED_BACKGROUND    = isColoredTextSupported() ? "\033[41m" : "";  // RED
  public static final String GREEN_BACKGROUND  = isColoredTextSupported() ? "\033[42m" : "";  // GREEN
  public static final String YELLOW_BACKGROUND = isColoredTextSupported() ? "\033[43m" : "";  // YELLOW
  public static final String BLUE_BACKGROUND   = isColoredTextSupported() ? "\033[44m" : "";  // BLUE
  public static final String PURPLE_BACKGROUND = isColoredTextSupported() ? "\033[45m" : "";  // PURPLE
  public static final String CYAN_BACKGROUND   = isColoredTextSupported() ? "\033[46m" : "";  // CYAN
  public static final String WHITE_BACKGROUND  = isColoredTextSupported() ? "\033[47m" : "";  // WHITE

  /**
   * Bright text
   */
  public static final String BLACK_BRIGHT  = isColoredTextSupported() ? "\033[0;90m" : "";  // BLACK
  public static final String RED_BRIGHT    = isColoredTextSupported() ? "\033[0;91m" : "";  // RED
  public static final String GREEN_BRIGHT  = isColoredTextSupported() ? "\033[0;92m" : "";  // GREEN
  public static final String YELLOW_BRIGHT = isColoredTextSupported() ? "\033[0;93m" : "";  // YELLOW
  public static final String BLUE_BRIGHT   = isColoredTextSupported() ? "\033[0;94m" : "";  // BLUE
  public static final String PURPLE_BRIGHT = isColoredTextSupported() ? "\033[0;95m" : "";  // PURPLE
  public static final String CYAN_BRIGHT   = isColoredTextSupported() ? "\033[0;96m" : "";  // CYAN
  public static final String WHITE_BRIGHT  = isColoredTextSupported() ? "\033[0;97m" : "";  // WHITE

  /**
   * Bold Bright text
   */
  public static final String BLACK_BOLD_BRIGHT  = isColoredTextSupported() ? "\033[1;90m" : "";  // BLACK
  public static final String RED_BOLD_BRIGHT    = isColoredTextSupported() ? "\033[1;91m" : "";  // RED
  public static final String GREEN_BOLD_BRIGHT  = isColoredTextSupported() ? "\033[1;92m" : "";  // GREEN
  public static final String YELLOW_BOLD_BRIGHT = isColoredTextSupported() ? "\033[1;93m" : "";  // YELLOW
  public static final String BLUE_BOLD_BRIGHT   = isColoredTextSupported() ? "\033[1;94m" : "";  // BLUE
  public static final String PURPLE_BOLD_BRIGHT = isColoredTextSupported() ? "\033[1;95m" : "";  // PURPLE
  public static final String CYAN_BOLD_BRIGHT   = isColoredTextSupported() ? "\033[1;96m" : "";  // CYAN
  public static final String WHITE_BOLD_BRIGHT  = isColoredTextSupported() ? "\033[1;97m" : "";  // WHITE

  /**
   * Bright text's background
   */
  public static final String BLACK_BACKGROUND_BRIGHT  = isColoredTextSupported() ? "\033[0;100m" : "";  // BLACK
  public static final String RED_BACKGROUND_BRIGHT    = isColoredTextSupported() ? "\033[0;101m" : "";  // RED
  public static final String GREEN_BACKGROUND_BRIGHT  = isColoredTextSupported() ? "\033[0;102m" : "";  // GREEN
  public static final String YELLOW_BACKGROUND_BRIGHT = isColoredTextSupported() ? "\033[0;103m" : "";  // YELLOW
  public static final String BLUE_BACKGROUND_BRIGHT   = isColoredTextSupported() ? "\033[0;104m" : "";  // BLUE
  public static final String PURPLE_BACKGROUND_BRIGHT = isColoredTextSupported() ? "\033[0;105m" : "";  // PURPLE
  public static final String CYAN_BACKGROUND_BRIGHT   = isColoredTextSupported() ? "\033[0;106m" : "";  // CYAN
  public static final String WHITE_BACKGROUND_BRIGHT  = isColoredTextSupported() ? "\033[0;107m" : "";  // WHITE
  
  static private Console     sConsole = System.console();
  static private Scanner     sStdin   = new Scanner(System.in);
  static private PrintStream sStdout  = System.out;
  
  /**
   * Print the prompt message and read a string from STDIN
   *  
   * @param prompt The message to print before reading the string
   * @return The read string
   */
  static public String readClearText(String prompt)
  {
    String input;
    if (sConsole != null)
    {
      input = sConsole.readLine(prompt);
    }
    else
    {
      sStdout.print(prompt);
      input = sStdin.nextLine();
    }
    return input;
  }
  
  /**
   * Print the prompt message and read a string from STDIN, while suppressing
   * the input characters.
   *  
   * @param prompt The message to print before reading the string
   * @return The read string
   */
  static public String readMaskedText(String prompt)
  {
    String input;
    if (sConsole != null)
    {
      char [] ca = sConsole.readPassword(prompt);
      input = new String(ca);
    }
    else
    {
      sStdout.print(prompt);
      input = sStdin.nextLine();
    }
    return input;
  }
}
