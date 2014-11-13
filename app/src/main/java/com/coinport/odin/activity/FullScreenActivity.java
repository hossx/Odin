package com.coinport.odin.activity;

import com.coinport.odin.R;
import com.coinport.odin.util.OrientationDetector;
import com.coinport.odin.util.OrientationDetector.OnScreenDirectionChanged;
import com.coinport.odin.util.Util.ScreenDirection;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class FullScreenActivity extends Activity {
  private OrientationDetector mOrientationDetector;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.full_screen);

    Log.i("FullScreenActivity", this.getIntent().getExtras().getString("direction"));
    mOrientationDetector = new OrientationDetector(this,
        ScreenDirection.valueOf(this.getIntent().getExtras().getString("direction")));
    mOrientationDetector.setOnScreenDirectionChanged(new OnScreenDirectionChanged() {

      @Override
      public void onChanged(ScreenDirection direction) {
        if (direction == ScreenDirection.NORMAL) {
          FullScreenActivity.this.finish();
        }
      }

    });
    mOrientationDetector.enable();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mOrientationDetector.disable();
  }
}
