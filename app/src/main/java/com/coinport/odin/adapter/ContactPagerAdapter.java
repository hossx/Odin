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

public class ContactPagerAdapter extends PagerAdapter implements PagerSlidingTabStrip.IconTabProvider {
    private QuickContactFragment context;

    private final String[] MESSAGES;

    private final int[] ICONS = { R.drawable.ic_launcher_gmaps, R.drawable.ic_launcher_gmail,
        /* R.drawable.ic_launcher_weixin*/R.drawable.ic_launcher_qq,
        R.drawable.ic_launcher_weibo};

    public ContactPagerAdapter(QuickContactFragment context) {
        super();
        this.context = context;
        MESSAGES = new String[]{context.getString(R.string.contact_address),
                context.getString(R.string.contact_mail),
                context.getString(R.string.contact_qq),
                context.getString(R.string.contact_weibo),
                context.getString(R.string.contact_phone)};
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
        v.setText(MESSAGES[position]);
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
        return v == o;
    }

}
