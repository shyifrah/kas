package com.kas.containers;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import com.kas.containers.internal.ACappedContainer;
import com.kas.infra.utils.StringUtils;

public class CappedHashSet<E> extends ACappedContainer implements Set<E>
{
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  private HashSet<E> mHashSet;
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public CappedHashSet(String path, int threshold, ICappedContainerListener listener)
  {
    super(path, threshold, listener);
    mHashSet = new HashSet<E>();
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public boolean add(E element)
  {
    boolean result;
    if (!isCapped())                      // not capped
    {
      result = mHashSet.add(element);
    }
    else if (mSuspended)                  // capped & suspended
    {
      result = false;
    }
    else                                  // capped & not suspended
    {
      result = mHashSet.add(element);
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
  public boolean remove(Object element)
  {
    boolean result;
    if ((!isCapped()) || (!mSuspended))   // not capped nor suspended
    {
      result = mHashSet.remove(element);
    }
    else                                  // capped & suspended
    {
      result = mHashSet.remove(element);
      if (result)
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
  public boolean addAll(Collection<? extends E> collection)
  {
    boolean result;
    if (!isCapped())        // not capped
    {
      result = mHashSet.addAll(collection);
    }
    else if (mSuspended)    // suspended
    {
      result = false;
    }
    else                    // capped and not suspended
    {
      result = mHashSet.addAll(collection);
      if (result)
      {
        if (size() > mThreshold)
        {
          suspend();
        }
      }
    }
    return result;
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public boolean removeAll(Collection<?> collection)
  {
    boolean result;
    if ((!isCapped()) || (!mSuspended))        // not capped nor suspended
    {
      result = mHashSet.removeAll(collection);
    }
    else                                       // suspended
    {
      result = mHashSet.removeAll(collection);
      if (result)
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
  public void clear()
  {
    mHashSet.clear();
    if (mSuspended)
    {
      resume();
    }
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public boolean contains(Object element)
  {
    return mHashSet.contains(element);
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public boolean containsAll(Collection<?> collection)
  {
    return mHashSet.containsAll(collection);
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public boolean retainAll(Collection<?> collection)
  {
    boolean result;
    if ((!isCapped()) || (!mSuspended))        // not capped nor suspended
    {
      result = mHashSet.retainAll(collection);
    }
    else                                       // suspended
    {
      result = mHashSet.retainAll(collection);
      if (result)
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
  public boolean isEmpty()
  {
    return mHashSet.size() == 0;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public int size()
  {
    return mHashSet.size();
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public Iterator<E> iterator()
  {
    return mHashSet.iterator();
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public Object[] toArray()
  {
    return mHashSet.toArray();
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public <T> T[] toArray(T[] array)
  {
    return mHashSet.toArray(array);
  }

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public boolean equals(CappedHashSet<?> other)
  {
    return ((mThreshold == other.mThreshold) && 
        (mPath.equals(other.mPath)) && 
        (mHashSet.equals(other.mHashSet)));
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
      .append(StringUtils.asPrintableString(mHashSet, level+2)).append("\n")
      .append(pad).append("  )\n")
      .append(pad).append(")");
    return sb.toString();
  }

}
