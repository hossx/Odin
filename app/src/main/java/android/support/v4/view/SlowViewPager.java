package android.support.v4.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import java.lang.reflect.Method;

/**
 * Created by hoss on 14-11-22.
 */
public class SlowViewPager extends ViewPager {
    public SlowViewPager(Context context) {
        super(context);
    }

    public SlowViewPager(Context context, AttributeSet attr) {
        super(context,attr);
    }

    @Override
    void smoothScrollTo(int x, int y, int velocity) {
        super.smoothScrollTo(x, y, 1);
    }

}