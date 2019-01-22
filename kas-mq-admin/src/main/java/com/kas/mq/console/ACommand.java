package com.kas.mq.console;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.kas.mq.internal.MqContextConnection;

/**
 * An abstract basic command.<br>
 * <br>
 * Parsing command text has the same logic for all types of commands and is implemented
 * here via the {@link #parse()} method.<br>
 * Driven classes should implement the {@link #exec(MqContextConnection)} method.
 * 
 * @author Pippo
 */
public abstract class ACommand implements ICommand
{
  static private final String  cRegExpr_ParamValue = "^\\(.*?\\)";
  static private final Pattern cPattern_ParamValue = Pattern.compile(cRegExpr_ParamValue);
  
  /**
   * A list of acceptable command verbs
   */
  protected List<String> mCommandVerbs = new ArrayList<String>();
  
  /**
   * The command text passed to the specific command
   */
  protected String mCommandText;
  
  /**
   * After parsing the arguments string (via a call to {@link #parse()},
   * the arguments and their corresponding values are populated in this map.<br>
   * Note that the map uses case-insensitive keys. DO NOT CHANGE THAT!
   */
  private Map<String, String> mArgMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);;
  
  /**
   * Parse the command arguments and populate the arguments map.<br>
   * <br>
   * First, the method scans the arguments string, parsing argument followed
   * by its associated value. If a value is missing, parsing fails.<br>
   * After parsing is over, the {@link #setArguments()} method is called
   * to allow the driven command parsers to set its data members.<br>
   * Then, the {@link #parse()} command verifies that there are no leftovers in the map.
   * If there are, it means that unknown arguments were specified and it parsing fails.<br>
   * Finally, the {@link #verify()} method is called to allow the driven command parser
   * to add extra verifications, for example, verify mandatory values were specified. 
   */
  public void parse(String text)
  {
    String param = null;
    String value = null;
    String reminder = text;
    while (reminder.length() > 0)
    {
      param = reminder.split(" ")[0].toUpperCase();
      if (param.length() == 0)
        throw new IllegalArgumentException("Missing or invalid argument at [" + reminder + "]");
      
      reminder = reminder.substring(param.length()).trim();
      Matcher matcher = cPattern_ParamValue.matcher(reminder);
      if (!matcher.find())
        throw new IllegalArgumentException("Missing or invalid value for argument [" + param + "]");
      
      value = matcher.group();
      value = value.substring(1, value.length()-1);
      mArgMap.put(param, value);
      reminder = reminder.substring(value.length()+2).trim();
    }
    
    setup();
    
    if (!mArgMap.isEmpty())
    {
      for (Map.Entry<String, String> entry : mArgMap.entrySet())
      {
        String key = entry.getKey();
        throw new IllegalArgumentException("Unknown argument [" + key + "]");
      }
    }
  }
  
  /**
   * An empty method for setting the arguments of the driven command parser.<br>
   * <br>
   * Setting the data members is done by calling the {@link #getBoolean(String, Boolean)}, 
   * {@link #getInteger(String, Integer)} and {@link #getString(String, String)} methods
   * and assign the returned value.<br>
   * Calling to these methods will remove the corresponding values from the arguments map.<br>
   */
  protected void setup()
  {
  }
  
  /**
   * Execute the command using the specified {@link MqContextConnection}
   * 
   * @param conn The {@link MqContextConnection} that will be used to execute the command
   */
  public abstract void exec(MqContextConnection conn);

  /**
   * Print HELP screen for the specified command.
   */
  public void help()
  {
  }
  
  /**
   * Get the list of command verbs acceptable by this command
   * 
   * @return the list of command verbs acceptable by this command
   */
  public List<String> getCommandVerbs()
  {
    return mCommandVerbs;
  }
  
  /**
   * Read a string value from the arguments map
   * 
   * @param key The name of the argument
   * @param defval Default value to assign, in case no matching entry is found
   * @return The value from the map, or {@code defval} if {@code key} was not found
   */
  protected String getString(String key, String defval)
  {
    String res = mArgMap.remove(key);
    if (res == null)
      res = defval;
    return res;
  }
  
  /**
   * Read an integer value from the arguments map
   * 
   * @param key The name of the argument
   * @param defval Default value to assign, in case no matching entry is found
   * @return The value from the map, or {@code defval} if {@code key} was not found
   */
  protected Integer getInteger(String key, Integer defval)
  {
    Integer result = defval;
    String sval = getString(key, null);
    if (sval != null)
    {
      try
      {
        result = Integer.valueOf(sval);
      }
      catch (NumberFormatException e)
      {
        throw new IllegalArgumentException("Illegal value for argument \"" + key + "\". Expected: integer");
      }
    }
    return result;
  }
  
  /**
   * Read a long value from the arguments map
   * 
   * @param key The name of the argument
   * @param defval Default value to assign, in case no matching entry is found
   * @return The value from the map, or {@code defval} if {@code key} was not found
   */
  protected Long getLong(String key, Long defval)
  {
    Long result = defval;
    String sval = getString(key, null);
    if (sval != null)
    {
      try
      {
        result = Long.valueOf(sval);
      }
      catch (NumberFormatException e)
      {
        throw new IllegalArgumentException("Illegal value for argument \"" + key + "\". Expected: long");
      }
    }
    return result;
  }
  
  /**
   * Read a boolean value from the arguments map
   * 
   * @param key The name of the argument
   * @param defval Default value to assign, in case no matching entry is found
   * @return The value from the map, or {@code defval} if {@code key} was not found
   */
  protected Boolean getBoolean(String key, Boolean defval)
  {
    Boolean result = defval;
    String sval = getString(key, null);
    if (sval != null)
    {
      if (sval.equalsIgnoreCase("true"))
        result = Boolean.TRUE;
      else if (sval.equalsIgnoreCase("false"))
        result = Boolean.FALSE;
      else
        throw new IllegalArgumentException("Illegal value for argument \"" + key + "\". Expected: boolean");
    }
    return result;
  }
  
  /**
   * Read an Enum value from the arguments map
   * 
   * @param key The name of the argument
   * @param type The class of the enum
   * @param defval Default value to assign, in case no matching entry is found
   * @return The value from the map, or {@code defval} if {@code key} was not found
   */
  protected <T extends Enum<T>> T getEnum(String key, Class<T> type, T defval)
  {
    T result = defval;
    String sval = getString(key, null);
    if (sval != null)
    {
      try
      {
        result = Enum.valueOf(type, sval.toUpperCase());
      }
      catch (IllegalArgumentException e)
      {
        throw new IllegalArgumentException("Illegal value for argument \"" + key + "\". Expected: Enum value");
      }
    }
    return result;
  }
}
