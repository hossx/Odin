package com.coinport.odin.util;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;
import android.view.OrientationEventListener;

import com.coinport.odin.util.Util.ScreenDirection;

public class OrientationDetector {

  public static abstract class OnScreenDirectionChanged {
    public abstract void onChanged(ScreenDirection direction);
  }

  private static final ScreenDirection mDefaultDirection = ScreenDirection.NORMAL;
  private ScreenDirection mCurrentScreenDirection = mDefaultDirection;
  private final static long ROTATION_EFFECT_DELAY_MS = 1000;
  private SetOrientationTask mSetOrientationTask = new SetOrientationTask();
  private Handler mOrientationHandler = new Handler();
  private Context mContext;
  private OrientationEventListener mOrientationEventListener = null;

  private OnScreenDirectionChanged mOnScreenDirectionChanged = null;

  public OrientationDetector(Context context, ScreenDirection direction) {
    mContext = context;
    mCurrentScreenDirection = direction;
    mOrientationEventListener = new OrientationEventListener(mContext, SensorManager.SENSOR_DELAY_NORMAL) {

      @Override
      public void onOrientationChanged(int orientation) {
        if (orientation < 45 || orientation > 315) {
          mayChangeScreenDirection(ScreenDirection.NORMAL);
        } else if (orientation > 45 && orientation < 135) {
          mayChangeScreenDirection(ScreenDirection.LEFT_UP);
        } else if (orientation > 225 && orientation < 315) {
          mayChangeScreenDirection(ScreenDirection.RIGHT_UP);
        } else {
          mayChangeScreenDirection(ScreenDirection.INVERT);
        }
      }

    };
  }

  private OrientationDetector(Context context) {
    this(context, mDefaultDirection);
  }

  public void setOnScreenDirectionChanged(OnScreenDirectionChanged l) {
    mOnScreenDirectionChanged = l;
  }

  public void enable() {
    if (mOrientationEventListener.canDetectOrientation()) {
      mOrientationEventListener.enable();
    } else {
      Log.e("OrientationDetector", "can't detect orientation!");
    }
  }

  public void disable() {
    mOrientationEventListener.disable();
  }

  private class SetOrientationTask implements Runnable {
    private ScreenDirection mOrientation = mCurrentScreenDirection;

    public ScreenDirection getScreenDirection() {
      return mOrientation;
    }

    public void setScreenDirection(ScreenDirection orientation) {
      mOrientation = orientation;
    }

    @Override
    public void run() {
      if (mCurrentScreenDirection != mOrientation) {
        mCurrentScreenDirection = mOrientation;
        if (mOnScreenDirectionChanged != null) {
          mOnScreenDirectionChanged.onChanged(mCurrentScreenDirection);
        }
      }
    }
  }

  private void mayChangeScreenDirection(ScreenDirection orientation) {
    if (mSetOrientationTask.getScreenDirection() == orientation) {
      return;
    }
    mOrientationHandler.removeCallbacks(mSetOrientationTask);
    mSetOrientationTask.setScreenDirection(orientation);
    mOrientationHandler.postDelayed(mSetOrientationTask, ROTATION_EFFECT_DELAY_MS);
  }
}
