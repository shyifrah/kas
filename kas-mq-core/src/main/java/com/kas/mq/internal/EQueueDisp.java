package com.kas.mq.internal;

/**
 * Queue disposition
 * 
 * @author Pippo
 */
public enum EQueueDisp
{
  /**
   * Permanent queue is one that is backed up to the file-system
   */
  PERMANENT,
  
  /**
   * Temporary queue is one that upon server restarts, its contents are cleared
   */
  TEMPORARY
  ;
  
  /**
   * Get the disposition value that matches {@code disp}
   * 
   * @param disp The string value
   * @return The {@link EQueueDisp}
   */
  static public EQueueDisp fromString(String disp)
  {
    return EQueueDisp.valueOf(EQueueDisp.class, disp);
  }
}
