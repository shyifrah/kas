package com.kas.mq.server.security;

/**
 * A user entity interface
 * 
 * @author Pippo
 */
public interface IUserEntity extends IEntity
{
  /**
   * Get the user's password
   * 
   * @return the user's password
   */
  public abstract byte [] getPassword();
}
