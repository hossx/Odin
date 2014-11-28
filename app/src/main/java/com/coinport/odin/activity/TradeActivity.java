package com.coinport.odin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.astuetz.PagerSlidingTabStrip;
import com.coinport.odin.R;
import com.coinport.odin.adapter.TradePagerAdapter;

public class TradeActivity extends FragmentActivity {
    private String inCurrency;
    private String outCurrency;
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private TradePagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_trade);
        Intent intent = this.getIntent();
        inCurrency = intent.getStringExtra("inCurrency");
        outCurrency = intent.getStringExtra("outCurrency");
//        TextView tv = (TextView) findViewById(R.id.trade_page_text);
//        tv.setText("inCurrency: " + inCurrency + " outCurrency: " + outCurrency);

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new TradePagerAdapter(getSupportFragmentManager(), this);

        pager.setAdapter(adapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);

        tabs.setViewPager(pager);
        tabs.setIndicatorHeight(8);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trade, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public String getInCurrency() {
        return inCurrency;
    }

    public String getOutCurrency() {
        return outCurrency;
    }

}
