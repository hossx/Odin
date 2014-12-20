package com.coinport.odin.activity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.coinport.odin.App;
import com.coinport.odin.R;
import com.coinport.odin.library.ptr.PullToRefreshBase;
import com.coinport.odin.library.ptr.PullToRefreshScrollView;
import com.coinport.odin.network.NetworkAsyncTask;
import com.coinport.odin.network.NetworkRequest;
import com.coinport.odin.network.OnApiResponseListener;
import com.coinport.odin.obj.AccountInfo;
import com.coinport.odin.util.Constants;
import com.coinport.odin.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class AssetActivity extends Activity {
    private ArrayList<HashMap<String, String>> assetItems = new ArrayList<>();
    private SimpleAdapter adapter;
    protected PullToRefreshScrollView refreshableView;

    private Typeface iconTF;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_asset);

        iconTF = Typeface.createFromAsset(getAssets(), "coinport.ttf");
        refreshableView = (PullToRefreshScrollView) findViewById(R.id.refreshable_view);
        refreshableView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                fetchAsset(true);
            }
        });

        adapter = new SimpleAdapter(AssetActivity.this, assetItems, R.layout.asset_item, new String[]{
                "currency", "valid", "pending"}, new int[] {
                R.id.asset_currency, R.id.asset_valid, R.id.asset_pending}) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                String currency = ((TextView) view.findViewById(R.id.asset_currency)).getText().toString();
                TextView icon = (TextView) view.findViewById(R.id.currency_icon);
                icon.setTypeface(iconTF);
                icon.setTextColor(Color.BLACK);
                icon.setText(Util.iconFont.get(currency));
                icon.setTextSize(20);
                return view;
            }
        };

        ListView lv = (ListView) findViewById(R.id.assets);
        lv.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchAsset(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_asset, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fetchAsset(final boolean isRefresh) {
        AccountInfo ai = App.getAccount();
        String url = String.format(Constants.ASSET_URL, ai.uid);
        NetworkAsyncTask task = new NetworkAsyncTask(url, Constants.HttpMethod.GET)
                .setOnSucceedListener(new OnApiResponseListener())
                .setOnFailedListener(new OnApiResponseListener())
                .setRenderListener(new NetworkAsyncTask.OnPostRenderListener() {
                    @Override
                    public void onRender(NetworkRequest s) {
                        if (isRefresh)
                            refreshableView.onRefreshComplete();
                        if (s.getApiStatus() != NetworkRequest.ApiStatus.SUCCEED)
                            return;
                        TextView sumCny = (TextView) findViewById(R.id.asset_sum_cny);
                        TextView sumBtc = (TextView) findViewById(R.id.asset_sum_btc);
                        sumCny.setText(String.format(getString(R.string.asset_sum_cny), "314324.2432"));
                        sumBtc.setText(String.format(getString(R.string.asset_sum_btc), "4324.2432"));

                        try {
                            JSONObject jsonObject = Util.getJsonObjectByPath(s.getApiResult(), "data.accounts");
                            assetItems.clear();
                            Iterator<String> it = jsonObject.keys();
                            while (it.hasNext()) {
                                HashMap<String, String> fields = new HashMap<>();
                                JSONObject jsonObj = jsonObject.getJSONObject(it.next());
                                double pending = jsonObj.getJSONObject("locked").getDouble("value") +
                                        jsonObj.getJSONObject("pendingWithdrawal").getDouble("value");
                                fields.put("currency", jsonObj.getString("currency"));
                                fields.put("valid", jsonObj.getJSONObject("available").getString("display"));
                                fields.put("pending", (new BigDecimal(pending).setScale(4, RoundingMode.CEILING)).toPlainString());
                                assetItems.add(fields);
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        task.execute();
    }
}
