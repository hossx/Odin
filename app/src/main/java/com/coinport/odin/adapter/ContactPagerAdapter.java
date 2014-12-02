package com.coinport.odin.adapter;

import android.support.v4.view.PagerAdapter;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coinport.odin.library.psts.PagerSlidingTabStrip;
import com.coinport.odin.R;
import com.coinport.odin.fragment.QuickContactFragment;

/**
 * Created by hoss on 14-11-21.
 */
public class ContactPagerAdapter extends PagerAdapter implements PagerSlidingTabStrip.IconTabProvider {
    private QuickContactFragment context;

    private final int[] ICONS = { R.drawable.ic_launcher_gplus, R.drawable.ic_launcher_gmail,
            R.drawable.ic_launcher_gmaps, R.drawable.ic_launcher_chrome };

    public ContactPagerAdapter(QuickContactFragment context) {
        super();
        this.context = context;
    }

    @Override
    public int getCount() {
        return ICONS.length;
    }

    @Override
    public int getPageIconResId(int position) {
        return ICONS[position];
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // looks a little bit messy here
        TextView v = new TextView(context.getActivity());
        v.setBackgroundResource(R.color.background_window);
        v.setText("PAGE " + (position + 1));
        final int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, context.getResources()
                .getDisplayMetrics());
        v.setPadding(padding, padding, padding, padding);
        v.setGravity(Gravity.CENTER);
        container.addView(v, 0);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {
        container.removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(View v, Object o) {
        return v == ((View) o);
    }

}
