package com.kas.mq.appl.cli;

import com.kas.infra.utils.StringUtils;
import com.kas.mq.typedef.TokenQueue;

/**
 * A basic command
 * 
 * @author Pippo
 */
public abstract class ACliCommand implements ICliCommand
{
  /**
   * The command arguments
   */
  private TokenQueue mCommandArgs;
  
  /**
   * Processing the command.
   * 
   * @return {@code true} upon successful processing and {@code false} otherwise for "Exit" command.
   * The other way around for all other commands.
   */
  public abstract boolean run();

  /**
   * Generate a padding for members layout to be used by {@link #toPrintableString(int)}.
   * 
   * @return padding string
   */
  protected String pad(int level)
  {
    return StringUtils.getPadding(level);
  }
  
  /**
   * Returns the {@link ACliCommand} simple class name enclosed with chevrons.
   * 
   * @return class name enclosed with chevrons.
   * 
   * @see com.kas.infra.base.IObject#name()
   */
   public String name()
   {
     StringBuilder sb = new StringBuilder();
     sb.append("<")
       .append(this.getClass().getSimpleName())
       .append(">");
     return sb.toString();
   }

   /**
    * Returns a replica of this {@link ACliCommand}.
    * 
    * @return a replica of this {@link ACliCommand}
    * 
    * @see com.kas.infra.base.IObject#replicate()
    */
   public abstract ACliCommand replicate();

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
       .append(pad).append("  Arguments=(").append(StringUtils.asPrintableString(mCommandArgs, level+1)).append(")\n")
       .append(pad).append(")");
     return sb.toString();
   }
   
   /**
    * Writing a message to STDOUT. The message will be followed by a Newline character.
    * 
    * @param message the message to print
    */
   protected void writeln(String message)
   {
     System.out.println(message);
   }
}
