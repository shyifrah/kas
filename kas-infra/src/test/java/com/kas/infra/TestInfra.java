package com.kas.infra;

import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kas.infra.base.TimeStamp;

public class TestInfra
{
  static public void main(String [] args)
  {
    System.setProperty("log4j.configurationFile", "build/resources/test/log4j2.xml");
    
    File file = new File(".");
    System.out.println("Current directory: " + file.getAbsolutePath());
    
    Logger logger = LogManager.getLogger(TestInfra.class);
    
    TimeStamp ts = TimeStamp.now();
    logger.info("Timestamp is: {}", ts);
  }
}
