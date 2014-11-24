package com.coinport.odin.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.coinport.odin.R;
import com.coinport.odin.activity.MainActivity;
import com.coinport.odin.fragment.DepositWithdrawalFragment;
import com.coinport.odin.fragment.MarketFragment;
import com.coinport.odin.fragment.TradeFragment;
import com.coinport.odin.fragment.UserFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hoss on 14-11-21.
 */
public class CpPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.ViewTabProvider {

    private MainActivity context;
    private LayoutInflater inflater;

    private View[] tabs;
    private List<Fragment> fragments = new ArrayList();
    private Map<Integer, Fragment> fragmentMap = new HashMap<Integer, Fragment>();

    public CpPagerAdapter(FragmentManager fm, MainActivity context) {
        super(fm);
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initUI();
    }

    @Override
    public int getCount() {
        return tabs.length;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
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
        int tabWidth = size.x / 4;

        View marketTab = inflater.inflate(R.layout.tab_item, null);
        marketTab.setMinimumWidth(tabWidth);
        View tradeTab = inflater.inflate(R.layout.tab_item, null);
        tradeTab.setMinimumWidth(tabWidth);
        View depositWithdrawTab = inflater.inflate(R.layout.tab_item, null);
        depositWithdrawTab.setMinimumWidth(tabWidth);
        View userTab = inflater.inflate(R.layout.tab_item, null);
        userTab.setMinimumWidth(tabWidth);

        initTab(marketTab, "\ue612", context.getString(R.string.tab_market));
        initTab(tradeTab, "\ue603", context.getString(R.string.tab_trade));
        initTab(depositWithdrawTab, "\ue605", context.getString(R.string.tab_deposit_withdrawal));
        initTab(userTab, "\ue624", context.getString(R.string.tab_user));

        tabs = new View[] {marketTab, tradeTab, depositWithdrawTab, userTab};

        Fragment marketFragment = new MarketFragment();
        fragments.add(marketFragment);
        fragmentMap.put(R.id.market_fragment, marketFragment);

        Fragment tradeFragment = new TradeFragment();
        fragments.add(new TradeFragment());
        fragmentMap.put(R.id.trade_fragment, tradeFragment);

        Fragment depositWithdrawalFragment = new DepositWithdrawalFragment();
        fragments.add(depositWithdrawalFragment);
        fragmentMap.put(R.id.deposit_withdrawal_fragment, depositWithdrawalFragment);

        Fragment userFragment = new UserFragment();
        fragments.add(userFragment);
        fragmentMap.put(R.id.user_fragment, userFragment);
    }

    private void initTab(View tab, String icon, String title) {
        TextView iconTextView = (TextView) tab.findViewById(R.id.tab_icon);
        iconTextView.setGravity(Gravity.CENTER);
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "coinport.ttf");
        iconTextView.setTypeface(tf);
        iconTextView.setText(icon);
        iconTextView.setTextColor(Color.WHITE);
        iconTextView.setTextSize(30);

        TextView titleTextView = (TextView) tab.findViewById(R.id.tab_name);
        titleTextView.setGravity(Gravity.CENTER);
        titleTextView.setText(title);
        titleTextView.setTextColor(Color.WHITE);
        titleTextView.setTextSize(15);
        titleTextView.setPadding(0, 4, 0, 0);
    }
}
