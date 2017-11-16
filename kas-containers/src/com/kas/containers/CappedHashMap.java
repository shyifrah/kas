package com.kas.containers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.kas.containers.internal.ACappedContainer;
import com.kas.infra.utils.StringUtils;

public class CappedHashMap<K,V> extends ACappedContainer implements Map<K, V>
{
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private HashMap<K,V> mHashMap;
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public CappedHashMap(String path, int threshold, ICappedContainerListener listener)
  {
    super(path, threshold, listener);
    mHashMap = new HashMap<K, V>();
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public V put(K key, V value)
  {
    V result = null;
    if (!isCapped())                      // not capped
    {
      result = mHashMap.put(key, value);
    }
    else if (mSuspended)                  // capped & suspended
    {
      result = null;
    }
    else                                  // capped & not suspended
    {
      result = mHashMap.put(key, value);
      if (size() > mThreshold)
      {
        suspend();
      }
    }
    return result;
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public V remove(Object key)
  {
    V result = null;
    if ((!isCapped()) || (!mSuspended))   // not capped nor suspended
    {
      result = mHashMap.remove(key);
    }
    else                                  // capped & suspended
    {
      result = mHashMap.remove(key);
      if (result != null)
      {
        if (size() <= mThreshold)
        {
          resume();
        }
      }
    }

    return result;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public V get(Object key)
  {
    return mHashMap.get(key);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void putAll(Map<? extends K, ? extends V> m)
  {
    if (!isCapped())
    {
      mHashMap.putAll(m);
    }
    else
    {
      if (!mSuspended)
      {
        mHashMap.putAll(m);
        if (size() > mThreshold)
        {
          suspend();
        }
      }
    }
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public void clear()
  {
    mHashMap.clear();
    if (isCapped())
    {
      if (mSuspended)
      {
        resume();
      }
    }
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public int size()
  {
    return mHashMap.size();
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public boolean isEmpty()
  {
    return size() > 0;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public boolean containsKey(Object key)
  {
    return mHashMap.containsKey(key);
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public boolean containsValue(Object value)
  {
    return mHashMap.containsValue(value);
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public Set< Map.Entry<K, V> > entrySet()
  {
    return mHashMap.entrySet();
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public Set<K> keySet()
  {
    return mHashMap.keySet(); 
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public Collection<V> values()
  {
    return mHashMap.values();
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public boolean equals(CappedHashMap<?, ?> other)
  {
    return ((mThreshold == other.mThreshold) && 
        (mPath.equals(other.mPath)) && 
        (mHashMap.equals(other.mHashMap)));
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public String toPrintableString(int level)
  {
    String pad = pad(level);
    StringBuffer sb = new StringBuffer();
    sb.append(name()).append("(\n")
      .append(pad).append("  ConfigPath=").append(mPath).append("\n")
      .append(pad).append("  Threshold=").append(mThreshold).append("\n")
      .append(pad).append("  Suspended=").append(mSuspended).append("\n")
      .append(pad).append("  Listener=").append(mListener).append("\n")
      .append(pad).append("  Entries=(\n")
      .append(StringUtils.asPrintableString(mHashMap, level+2)).append("\n")
      .append(pad).append("  )\n")
      .append(pad).append(")");
    return sb.toString();
  }
}
