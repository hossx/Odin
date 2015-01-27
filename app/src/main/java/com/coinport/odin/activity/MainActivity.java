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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.coinport.odin.App;
import com.coinport.odin.fragment.DepositWithdrawalFragment;
import com.coinport.odin.fragment.QuickContactFragment;
import com.coinport.odin.library.psts.PagerSlidingTabStrip;
import com.coinport.odin.R;
import com.coinport.odin.adapter.MainPagerAdapter;
import com.coinport.odin.fragment.MarketFragment;
import com.coinport.odin.network.CpHttpClient;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MainActivity extends FragmentActivity {

    private ProgressDialog pDialog;
    private final Handler handler = new Handler();

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MainPagerAdapter adapter;

    private Drawable oldBackground = null;
//    private int currentColor = 0xFF5161BC;
    private int currentColor = 0xFF242B35;
    private Menu menu = null;
    private LinearLayout baseCurrencySelector = null;
    private LinearLayout currencySelector = null;
    private TextView textView = null;

    private long exitTime = 0;
    private int currentPagePosition = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main);

        App.setMainActivity(this);

        pDialog = new ProgressDialog(this);
        baseCurrencySelector = (LinearLayout) getLayoutInflater().inflate(R.layout.base_currency_selector, null);
//        final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources()
//                .getDisplayMetrics());
//        baseCurrencySelector.setPadding(0, 0, margin, 0);
        final ImageView cnyIV = ((ImageView) baseCurrencySelector.findViewById(R.id.base_cny_view));
        cnyIV.setBackgroundResource(R.drawable.background_tab);
        cnyIV.setBackgroundResource(R.drawable.cny_status_selected);
        final ImageView btcIV = ((ImageView) baseCurrencySelector.findViewById(R.id.base_btc_view));
        btcIV.setBackgroundResource(R.drawable.btc_status_mormal);

        cnyIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cnyIV.setBackgroundResource(R.drawable.cny_status_selected);
                btcIV.setBackgroundResource(R.drawable.btc_status_mormal);
                ((MarketFragment) adapter.getFragment(R.id.market_fragment)).fetchDataWithBaseCurrency("CNY");
            }
        });
        btcIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btcIV.setBackgroundResource(R.drawable.btc_status_selected);
                cnyIV.setBackgroundResource(R.drawable.cny_status_normal);
                ((MarketFragment) adapter.getFragment(R.id.market_fragment)).fetchDataWithBaseCurrency("BTC");
            }
        });

        textView = new TextView(this);
        textView.setText("ok");
        currencySelector = (LinearLayout) getLayoutInflater().inflate(R.layout.currency_selector, null);
        Spinner currencySpinner = (Spinner) currencySelector.findViewById(R.id.currency_spinner);
        // TODO(c): 币种信息从服务端动态加载
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.currency_array,
            R.layout.white_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(spinnerAdapter);
        currencySpinner.setSelection(0);
        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                TextView tv = (TextView) view;
//                tv.setTextColor(Color.WHITE);

                String currency = parent.getItemAtPosition(position).toString();
                ((DepositWithdrawalFragment) adapter.getFragment(R.id.deposit_withdrawal_fragment)).setCurrency(currency);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
//        final ActionBar actionBar = getActionBar();
//        actionBar.setCustomView(baseCurrencySelector);

//        actionBar.setDisplayShowCustomEnabled(true);
//        actionBar.setDisplayShowTitleEnabled(false);
        getOverflowMenu();

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new MainPagerAdapter(getSupportFragmentManager(), this);

        pager.setAdapter(adapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);

        tabs.setViewPager(pager);

//        changeColor(currentColor);
        tabs.setIndicatorHeight(8);
        tabs.setBackgroundColor(currentColor);
        tabs.setIndicatorColor(Color.WHITE);
        tabs.setDividerColor(getResources().getColor(R.color.tab_gray));
        final FragmentActivity self = this;
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPagePosition = position;
                if (menu != null)
                    changeActionBar(currentPagePosition);
                adapter.pageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        adapter.pageSelected(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        this.menu.add(Menu.NONE, R.id.base_currency_selector, Menu.NONE, "bcs").setActionView(baseCurrencySelector)
            .setVisible(true).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        this.menu.add(Menu.NONE, R.id.currency_selector, Menu.NONE, "bcs").setActionView(currencySelector)
            .setVisible(false).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//        this.menu.add(Menu.NONE, R.id.config_more, Menu.NONE, getString(R.string.menu_item_more))
//            .setIcon(R.drawable.ic_action_overflow).setVisible(true)
//            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        changeActionBar(currentPagePosition);
//        pager.setCurrentItem(3);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_contact:
                QuickContactFragment dialog = new QuickContactFragment();
                dialog.show(getSupportFragmentManager(), "QuickContactFragment");
                return true;
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            if((System.currentTimeMillis() - exitTime) > 2000) { //System.currentTimeMillis()无论何时调用，肯定大于2000
                Toast.makeText(getApplicationContext(), getString(R.string.exit_hint), Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        CpHttpClient.shutDown();
                    }
                });
                t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
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

    private void changeActionBar(int position) {
        if (position == 0) {
            MenuItem cs = menu.findItem(R.id.currency_selector);
            if (cs != null) cs.setVisible(false);
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
            MenuItem cs = menu.findItem(R.id.currency_selector);
            if (cs != null) cs.setVisible(true);
            MenuItem mi = menu.findItem(R.id.base_currency_selector);
            if (mi != null) mi.setVisible(false);

//                    actionBar.setDisplayShowCustomEnabled(true);
//
//                    MenuItem mi = menu.findItem(R.id.action_contact);
//                    if (mi != null) mi.setVisible(false);
        } else if (position == 2) {
            MenuItem cs = menu.findItem(R.id.currency_selector);
            if (cs != null) cs.setVisible(false);
            MenuItem mi = menu.findItem(R.id.base_currency_selector);
            if (mi != null) mi.setVisible(false);
        }
    }
}