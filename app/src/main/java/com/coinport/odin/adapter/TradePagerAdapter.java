package com.coinport.odin.adapter;

import android.graphics.Point;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.coinport.odin.R;
import com.coinport.odin.fragment.TradeBuySellFragment;
import com.coinport.odin.fragment.TradeOrderFragment;
import com.coinport.odin.fragment.TradeTxFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hoss on 14-11-26.
 */
public class TradePagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.ViewTabProvider {
    private FragmentActivity context;
    private View[] tabs;
    private List<Fragment> fragments = new ArrayList();
    private Map<Integer, Fragment> fragmentMap = new HashMap<Integer, Fragment>();

    public TradePagerAdapter(FragmentManager fm, FragmentActivity context) {
        super(fm);
        this.context = context;
        initUI();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return tabs.length;
    }

    @Override
    public View getPageViewTab(int position) {
        return tabs[position];
    }

    public Fragment getFragment(int fragmentId) {
        if (fragmentMap.containsKey(fragmentId)) {
            return fragmentMap.get(fragmentId);
        } else {
            return null;
        }
    }

    private void initUI() {
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int tabWidth = size.x / 3;

        TextView buy = new TextView(context);
        initTab(buy, context.getString(R.string.trade_tab_buy_sell), tabWidth);
        TextView order = new TextView(context);
        initTab(order, context.getString(R.string.trade_tab_order), tabWidth);
        TextView tx = new TextView(context);
        initTab(tx, context.getString(R.string.trade_tab_tx), tabWidth);

        tabs = new View[] {buy, order, tx};

        Fragment buySellFragment = new TradeBuySellFragment();
        fragments.add(buySellFragment);
        fragmentMap.put(R.id.trade_buy_fragment, buySellFragment);
        Fragment orderFragment = new TradeOrderFragment();
        fragments.add(orderFragment);
        fragmentMap.put(R.id.trade_order_fragment, orderFragment);
        Fragment txFragment = new TradeTxFragment();
        fragments.add(txFragment);
        fragmentMap.put(R.id.trade_tx_fragment, txFragment);
    }

    private void initTab(TextView tab, String title, int width) {
        tab.setText(title);
        tab.setMinimumWidth(width);
        tab.setGravity(Gravity.CENTER);
        tab.setTextSize(15);
    }
}
