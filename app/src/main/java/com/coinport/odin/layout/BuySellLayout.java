package com.coinport.odin.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.coinport.odin.R;

/**
 * Created by hoss on 14-11-29.
 */
public class BuySellLayout extends LinearLayout {
    public BuySellLayout(Context context) {
        super(context);
    }

    public BuySellLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BuySellLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        final ScrollView sv = (ScrollView) findViewById(R.id.scroll_panel);
        // TODO(c): scroll to top if hide the soft keyboard
        sv.post(new Runnable() {
            @Override
            public void run() {
                sv.scrollTo(0, sv.getBottom() + 400);
            }
        });
    }
}
