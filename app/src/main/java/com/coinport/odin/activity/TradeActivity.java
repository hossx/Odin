package com.coinport.odin.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.coinport.odin.App;
import com.coinport.odin.fragment.QuickContactFragment;
import com.coinport.odin.library.psts.PagerSlidingTabStrip;
import com.coinport.odin.R;
import com.coinport.odin.adapter.TradePagerAdapter;

public class TradeActivity extends FragmentActivity implements View.OnClickListener {
    private String inCurrency;
    private String outCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_trade);
        Intent intent = this.getIntent();
        inCurrency = intent.getStringExtra("inCurrency");
        outCurrency = intent.getStringExtra("outCurrency");
        ActionBar bar = getActionBar();
        bar.setDisplayShowHomeEnabled(false);
        if (bar != null)
            bar.setTitle(inCurrency + "-" + outCurrency);
//        TextView tv = (TextView) findViewById(R.id.trade_page_text);
//        tv.setText("inCurrency: " + inCurrency + " outCurrency: " + outCurrency);

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        TradePagerAdapter adapter = new TradePagerAdapter(getSupportFragmentManager(), this);

        pager.setAdapter(adapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);

        tabs.setViewPager(pager);
        tabs.setIndicatorHeight(8);
        tabs.setIndicatorColor(getResources().getColor(R.color.tab_gray));
        tabs.setDividerColor(getResources().getColor(R.color.tab_gray));
        tabs.setTextColor(Color.WHITE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trade, menu);

        TextView tv = new TextView(this);
        tv.setTypeface(App.getIconTf());
        tv.setText("\ue61b");
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(30);
        tv.setClickable(true);
        tv.setBackgroundResource(R.drawable.background_tab);

        tv.setOnClickListener(this);
        menu.add(Menu.NONE, R.id.kline, Menu.NONE, "k").setActionView(tv)
                .setVisible(true).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
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

    public String getInCurrency() {
        return inCurrency;
    }

    public String getOutCurrency() {
        return outCurrency;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.kline:
                Intent intent = new Intent(this, KLineActivity.class);
                intent.putExtra("inCurrency", inCurrency);
                intent.putExtra("outCurrency", outCurrency);
                this.startActivity(intent);
                break;
        }
    }
}
