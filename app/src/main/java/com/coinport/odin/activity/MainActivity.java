package com.coinport.odin.activity;

import com.coinport.odin.R;

import android.os.Bundle;
import android.app.TabActivity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.Window;
import android.widget.TabHost;

// TODO(chaoma): replace this deprecated technology.
@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {

  private TabHost tabHost;

  private Intent marketIntent = new Intent();
  private Intent tradeIntent = new Intent();
  private Intent settingIntent = new Intent();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.activity_main);

    marketIntent.setClass(MainActivity.this, MarketActivity.class);
    tradeIntent.setClass(MainActivity.this, TradeActivity.class);
    settingIntent.setClass(MainActivity.this, SettingActivity.class);

    tabHost = this.getTabHost();
    LayoutInflater.from(this).inflate(R.layout.activity_main, tabHost.getTabContentView(), true);
    // tabHost.setBackgroundColor(Color.argb(150, 22, 70, 150));

    tabHost.addTab(tabHost.newTabSpec("market").setIndicator(getString(R.string.tab_market))
        .setContent(marketIntent));
    tabHost.addTab(tabHost.newTabSpec("trade").setIndicator(getString(R.string.tab_trade))
        .setContent(tradeIntent));
    tabHost.addTab(tabHost.newTabSpec("setting").setIndicator(getString(R.string.tab_setting))
        .setContent(settingIntent));
    tabHost.setCurrentTab(0);

    /*
     * TODO(chaoma): set the height/background/icon of the tabs. TabWidget
     * tabWidget = tabHost.getTabWidget();
     * tabWidget.setBackgroundColor(Color.WHITE); for (int i = 0; i <
     * tabWidget.getChildCount(); ++i) {
     * tabWidget.getChildAt(i).getLayoutParams().height = 70; }
     */
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

}
