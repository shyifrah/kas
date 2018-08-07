package com.kas.mq.server;

import java.io.File;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.KasException;
import com.kas.infra.utils.RunTimeUtils;

/**
 * MQ server
 * 
 * @author Pippo
 */
public class KasMqServer extends AKasObject
{
  /**
   * Main function
   * 
   * @param args Arguments passed from command line
   * 
   * @throws KasException Too many arguments
   */
  static public void main(String[] args) throws KasException
  {
    // if too many arguments were specified - terminate
    if (args.length > 1)
       throw new KasException("Too many arguments. Number of arguments is " + args.length);
    
    // only a single argument specified - make sure this the home directory
    String home = null;
    if (args.length == 1)
    {
      home = getHomeDirFromArgument(args[0]);
    }
    
    // if still no home directory, try get it from system property
    if (home == null)
    {
      home = RunTimeUtils.getProductHomeDir();
    }
    
    KasMqServer server = new KasMqServer(home);
  }
  
  /**
   * Extract and verify home directory as specified in the argument
   * 
   * @param home The home directory as specified in the argument
   * @return the KAS/MQ home directory
   */
  static private String getHomeDirFromArgument(String home)
  {
    String result = null;
    File configFile = new File(home);
    if ((configFile.exists()) && (configFile.canRead()) && (configFile.isDirectory()))
    {
      result = configFile.getAbsolutePath();
    }
    return result;
  }
  
  /**
   * Construct MQ server, specifying the home directory
   * 
   * @param home The home directory of KAS/MQ server
   */
  private KasMqServer(String home)
  {
    
  }
  
  public AKasObject replicate()
  {
    return null;
  }

  public String toPrintableString(int level)
  {
    return null;
  }
}
