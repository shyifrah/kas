package com.kas.containers.internal;

import com.kas.containers.ICappedContainerListener;
import com.kas.infra.base.AKasObject;

public abstract class ACappedContainer extends AKasObject
{
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  protected String                   mPath;
  protected int                      mThreshold;
  protected boolean                  mSuspended; 
  protected ICappedContainerListener mListener;
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  protected ACappedContainer(String path, int threshold, ICappedContainerListener listener)
  {
    mPath      = path;
    mThreshold = threshold;
    mListener  = listener;
    mSuspended = false;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  protected boolean isCapped()
  {
    return mThreshold != Constants.cDefaultUnlimitedSize;
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  public abstract String toPrintableString(int level);

  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  protected void resume()
  {
    mSuspended = false;
    if (mListener != null)
      mListener.onResume();
  }
  
  //------------------------------------------------------------------------------------------------------------------
  //
  //------------------------------------------------------------------------------------------------------------------
  protected void suspend()
  {
    mSuspended = true;
    if (mListener != null)
      mListener.onSuspend();
  }
}
