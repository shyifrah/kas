package com.kas.q;

import java.io.Serializable;
import java.util.Enumeration;
import javax.jms.ConnectionMetaData;
import javax.jms.JMSException;
import com.kas.infra.base.AKasObject;
import com.kas.infra.base.ProductVersion;

public class KasqConnectionMetaData extends AKasObject implements ConnectionMetaData, Serializable
{
  /***************************************************************************************************************
   *  
   */
  private static final long serialVersionUID = 1L;
  
  /***************************************************************************************************************
   *  
   */
  private static final   String cProviderName = "KAS/Q";
  private static final   int    cJmsMajorVersion = 2;
  private static final   int    cJmsMinorVersion = 0;
  
  /***************************************************************************************************************
   *  
   */
  private ProductVersion mProductVersion;
  
  /***************************************************************************************************************
   * Constructs a {@code KasqConnectionMetaData} object specifying the KAS/Q product version
   * 
   * @param version the KAS/Q product version
   */
  public KasqConnectionMetaData(ProductVersion version)
  {
    mProductVersion = version;
  }
  
  /***************************************************************************************************************
   *  
   */
  public String getJMSVersion() throws JMSException
  {
    return internalGetJmsVersion();
  }

  /***************************************************************************************************************
   *  
   */
  public int getJMSMajorVersion() throws JMSException
  {
    return cJmsMajorVersion;
  }

  /***************************************************************************************************************
   *  
   */
  public int getJMSMinorVersion() throws JMSException
  {
    return cJmsMinorVersion;
  }

  /***************************************************************************************************************
   *  
   */
  public String getJMSProviderName() throws JMSException
  {
    return cProviderName;
  }

  /***************************************************************************************************************
   *  
   */
  public String getProviderVersion() throws JMSException
  {
    return mProductVersion.toString();
  }

  /***************************************************************************************************************
   *  
   */
  public int getProviderMajorVersion() throws JMSException
  {
    return mProductVersion.getMajorVersion();
  }

  /***************************************************************************************************************
   *  
   */
  public int getProviderMinorVersion() throws JMSException
  {
    return mProductVersion.getMinorVersion();
  }

  /***************************************************************************************************************
   *  
   */
  public Enumeration<?> getJMSXPropertyNames() throws JMSException
  {
    throw new JMSException("Unsupported method: ConnectionMetaData.getJMSXPropertyNames()");
  }
  
  /***************************************************************************************************************
   * Gets the JMS version
   * 
   * @return the JMS version supported by KAS/Q
   */
  public String internalGetJmsVersion()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("JMS V")
      .append(cJmsMajorVersion)
      .append('.')
      .append(cJmsMinorVersion);
    return sb.toString();
  }

  /***************************************************************************************************************
   *  
   */
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    sb.append(name()).append("(\n")
      .append(pad).append("  Provider=").append(cProviderName).append(" ").append(mProductVersion.toString()).append("\n")
      .append(pad).append("  ").append(internalGetJmsVersion()).append("\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
