package com.coinport.odin.activity;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.coinport.odin.R;
import com.coinport.odin.adapter.CpPagerAdapter;
import com.coinport.odin.fragment.MarketFragment;
import com.coinport.odin.fragment.QuickContactFragment;
import com.coinport.odin.obj.TickerItem;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity {

    private ProgressDialog pDialog;
    private final Handler handler = new Handler();

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private CpPagerAdapter adapter;

    private Drawable oldBackground = null;
    private int currentColor = 0xFF5161BC;
    private Menu menu = null;
    private LinearLayout baseCurrencySelector = null;
    private TextView textView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main);

        pDialog = new ProgressDialog(this);
        baseCurrencySelector = (LinearLayout) getLayoutInflater().inflate(R.layout.base_currency_selector, null);
//        final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources()
//                .getDisplayMetrics());
//        baseCurrencySelector.setPadding(0, 0, margin, 0);
        Typeface tf = Typeface.createFromAsset(getAssets(), "coinport.ttf");
        final TextView cnyTV = ((TextView) baseCurrencySelector.findViewById(R.id.base_cny_view));
        cnyTV.setTypeface(tf);
        cnyTV.setTextSize(30);
        cnyTV.setTextColor(Color.WHITE);
        cnyTV.setBackgroundResource(R.drawable.background_tab);
        final TextView btcTV = ((TextView) baseCurrencySelector.findViewById(R.id.base_btc_view));
        btcTV.setTypeface(tf);
        btcTV.setTextSize(30);
        btcTV.setTextColor(Color.GRAY);
        btcTV.setBackgroundResource(R.drawable.background_tab);

        cnyTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cnyTV.setTextColor(Color.WHITE);
                btcTV.setTextColor(Color.GRAY);
                ((MarketFragment) adapter.getFragment(R.id.market_fragment)).getDataWithBaseCurrency("CNY");
            }
        });
        btcTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btcTV.setTextColor(Color.WHITE);
                cnyTV.setTextColor(Color.GRAY);
                ((MarketFragment) adapter.getFragment(R.id.market_fragment)).getDataWithBaseCurrency("BTC");
            }
        });

        textView = new TextView(this);
        textView.setText("ok");
//        final ActionBar actionBar = getActionBar();
//        actionBar.setCustomView(baseCurrencySelector);

//        actionBar.setDisplayShowCustomEnabled(true);
//        actionBar.setDisplayShowTitleEnabled(false);
        getOverflowMenu();

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new CpPagerAdapter(getSupportFragmentManager(), this);

        pager.setAdapter(adapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);

        tabs.setViewPager(pager);

//        changeColor(currentColor);
        tabs.setIndicatorColor(currentColor);
        tabs.setIndicatorHeight(8);
        tabs.setBackgroundColor(currentColor);
        tabs.setTextColor(Color.WHITE);
        tabs.setIndicatorColor(Color.WHITE);
        tabs.setDividerColor(Color.WHITE);
        final FragmentActivity self = this;
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    MenuItem tmp = menu.findItem(R.id.market_selector);
                    if (tmp != null) tmp.setVisible(false);
                    MenuItem mi = menu.findItem(R.id.base_currency_selector);
                    if (mi != null) mi.setVisible(true);
////                    menu.add(Menu.NONE, R.id.action_contact, Menu.NONE, "contact").setIcon(R.drawable.ic_action_user).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//                    MenuItem mi = menu.findItem(R.id.action_contact);
//                    if (mi != null)
//                        mi.setVisible(true);
//                    else {
//                        TextView btcActionView = new TextView(self);
//                        btcActionView.setId(R.id.btc_view);
//                        btcActionView.setPadding(4, 0, 4, 0);
//                        Typeface tf = Typeface.createFromAsset(self.getAssets(), "coinport.ttf");
//                        btcActionView.setTypeface(tf);
//                        btcActionView.setTextColor(Color.WHITE);
//                        btcActionView.setTextSize(25);
//                        btcActionView.setText("\ue62a");
//                        btcActionView.setBackgroundColor(Color.BLUE);
//                        menu.add(Menu.NONE, R.id.action_contact, Menu.NONE, "contact").setActionView(btcActionView).setIcon(R.drawable.ic_action_user).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//                    }
                } else if (position == 1) {
                    MenuItem tmp = menu.findItem(R.id.market_selector);
                    if (tmp != null) tmp.setVisible(true);
                    MenuItem mi = menu.findItem(R.id.base_currency_selector);
                    if (mi != null) mi.setVisible(false);

//                    actionBar.setDisplayShowCustomEnabled(true);
//
//                    MenuItem mi = menu.findItem(R.id.action_contact);
//                    if (mi != null) mi.setVisible(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
//        pager.setCurrentItem(3);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        this.menu.add(Menu.NONE, R.id.base_currency_selector, Menu.NONE, "bcs").setActionView(baseCurrencySelector)
            .setVisible(true).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        this.menu.add(Menu.NONE, R.id.market_selector, Menu.NONE, "bcs").setActionView(textView).setVisible(false)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//        this.menu.add(Menu.NONE, R.id.config_more, Menu.NONE, getString(R.string.menu_item_more))
//            .setIcon(R.drawable.ic_action_overflow).setVisible(true)
//            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.config_more:
                QuickContactFragment dialog = new QuickContactFragment();
                dialog.show(getSupportFragmentManager(), "QuickContactFragment");
                return true;
            case R.id.action_exit:
                finish();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if(featureId == Window.FEATURE_ACTION_BAR && menu != null){
            if(menu.getClass().getSimpleName().equals("MenuBuilder")){
                try{
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                }
                catch(NoSuchMethodException e){
                    System.out.println(e);
                }
                catch(Exception e){
                    throw new RuntimeException(e);
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    public ProgressDialog getpDialog() {
        return pDialog;
    }
    private void changeColor(int newColor) {

        tabs.setIndicatorColor(newColor);

        // change ActionBar color just if an ActionBar is available
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            Drawable colorDrawable = new ColorDrawable(newColor);
            Drawable bottomDrawable = getResources().getDrawable(R.drawable.actionbar_bottom);
            LayerDrawable ld = new LayerDrawable(new Drawable[] { colorDrawable, bottomDrawable });

            if (oldBackground == null) {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    ld.setCallback(drawableCallback);
                } else {
                    getActionBar().setBackgroundDrawable(ld);
                }

            } else {

                TransitionDrawable td = new TransitionDrawable(new Drawable[] { oldBackground, ld });

                // workaround for broken ActionBarContainer drawable handling on
                // pre-API 17 builds
                // https://github.com/android/platform_frameworks_base/commit/a7cc06d82e45918c37429a59b14545c6a57db4e4
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    td.setCallback(drawableCallback);
                } else {
                    getActionBar().setBackgroundDrawable(td);
                }

                td.startTransition(200);

            }

            oldBackground = ld;

            // http://stackoverflow.com/questions/11002691/actionbar-setbackgrounddrawable-nulling-background-from-thread-handler
            getActionBar().setDisplayShowTitleEnabled(false);
            getActionBar().setDisplayShowTitleEnabled(true);

        }

        currentColor = newColor;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentColor", currentColor);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentColor = savedInstanceState.getInt("currentColor");
        changeColor(currentColor);
    }

    private Drawable.Callback drawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            getActionBar().setBackgroundDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
            handler.postAtTime(what, when);
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
            handler.removeCallbacks(what);
        }
    };

    private void getOverflowMenu() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}