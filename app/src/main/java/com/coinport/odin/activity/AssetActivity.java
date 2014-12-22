package com.coinport.odin.activity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.coinport.odin.App;
import com.coinport.odin.R;
import com.coinport.odin.library.ptr.PullToRefreshBase;
import com.coinport.odin.library.ptr.PullToRefreshScrollView;
import com.coinport.odin.network.BarrierTaskSet;
import com.coinport.odin.network.NetworkAsyncTask;
import com.coinport.odin.network.NetworkRequest;
import com.coinport.odin.network.OnApiResponseListener;
import com.coinport.odin.obj.AccountInfo;
import com.coinport.odin.util.Constants;
import com.coinport.odin.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_asset, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private void fetchAsset(final boolean isRefresh) {
        AccountInfo ai = App.getAccount();
        String assetUrl = String.format(Constants.ASSET_URL, ai.uid);
        NetworkAsyncTask assetTask = new NetworkAsyncTask(assetUrl, Constants.HttpMethod.GET)
                .setOnSucceedListener(new OnApiResponseListener())
                .setOnFailedListener(new OnApiResponseListener());
        String cnyTickerUrl = Constants.TICKER_URL + "cny";
        NetworkAsyncTask cnyTickerTask = new NetworkAsyncTask(cnyTickerUrl, Constants.HttpMethod.GET)
                .setOnSucceedListener(new OnApiResponseListener())
                .setOnFailedListener(new OnApiResponseListener());
        String btcTickerUrl = Constants.TICKER_URL + "btc";
        NetworkAsyncTask btcTickerTask = new NetworkAsyncTask(btcTickerUrl, Constants.HttpMethod.GET)
                .setOnSucceedListener(new OnApiResponseListener())
                .setOnFailedListener(new OnApiResponseListener());
        Map<String, NetworkAsyncTask> tasks = new HashMap<>();
        tasks.put("asset", assetTask);
        tasks.put("cnyT", cnyTickerTask);
        tasks.put("btcT", btcTickerTask);
        BarrierTaskSet ts = new BarrierTaskSet(tasks, null);
        ts.setRenderListener(new BarrierTaskSet.OnPostRenderListener() {
            @Override
            public void onRender(Map<String, NetworkRequest> s) {
                NetworkRequest assetRes = s.get("asset");
                NetworkRequest cnyTickerRes = s.get("cnyT");
                NetworkRequest btcTickerRes = s.get("btcT");
                if (isRefresh)
                    refreshableView.onRefreshComplete();
                if (assetRes.getApiStatus() != NetworkRequest.ApiStatus.SUCCEED ||
                    cnyTickerRes.getApiStatus() != NetworkRequest.ApiStatus.SUCCEED ||
                    btcTickerRes.getApiStatus() != NetworkRequest.ApiStatus.SUCCEED) {
                    Toast.makeText(AssetActivity.this, getString(R.string.request_failed), Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    JSONObject assetObject = Util.getJsonObjectByPath(assetRes.getApiResult(), "data.accounts");
                    Map<String, Double> assetMap = new HashMap<String, Double>();
                    assetItems.clear();
                    Iterator<String> it = assetObject.keys();
                    while (it.hasNext()) {
                        HashMap<String, String> fields = new HashMap<>();
                        JSONObject jsonObj = assetObject.getJSONObject(it.next());
                        double pending = jsonObj.getJSONObject("locked").getDouble("value") +
                                jsonObj.getJSONObject("pendingWithdrawal").getDouble("value");
                        fields.put("currency", jsonObj.getString("currency"));
                        fields.put("valid", jsonObj.getJSONObject("available").getString("display"));
                        fields.put("pending", (new BigDecimal(pending).setScale(4, RoundingMode.CEILING))
                            .toPlainString());
                        assetMap.put(jsonObj.getString("currency"), jsonObj.getJSONObject("total").getDouble("value"));
                        assetItems.add(fields);
                    }
                    adapter.notifyDataSetChanged();

                    Map<String, Double> cnyPriceMap = getPriceMap(Util.getJsonArrayByPath(cnyTickerRes.getApiResult(),
                        "data"));
                    Map<String, Double> btcPriceMap = getPriceMap(Util.getJsonArrayByPath(btcTickerRes.getApiResult(),
                        "data"));
                    double sumCnyAmount = 0.0, sumBtcAmount = 0.0;
                    double cnyBtcPrice = 2000.0;
                    if (cnyPriceMap.containsKey("BTC"))
                        cnyBtcPrice = cnyPriceMap.get("BTC");
                    for (String c : assetMap.keySet()) {
                        double amount = assetMap.get(c);
                        if (c.equals("CNY")) {
                            sumCnyAmount += amount;
                            sumBtcAmount += (amount / cnyBtcPrice);
                        } else if (c.equals("BTC")) {
                            sumBtcAmount += amount;
                            sumCnyAmount += (amount * cnyBtcPrice);
                        } else {
                            if (cnyPriceMap.containsKey(c)) {
                                sumCnyAmount += amount * cnyPriceMap.get(c);
                            } else {
                                if (btcPriceMap.containsKey(c)) {
                                    sumCnyAmount += amount * btcPriceMap.get(c) * cnyBtcPrice;
                                } else {
                                    sumCnyAmount += amount;
                                }
                            }
                            if (btcPriceMap.containsKey(c)) {
                                sumBtcAmount += amount * btcPriceMap.get(c);
                            } else {
                                if (cnyPriceMap.containsKey(c)) {
                                    sumBtcAmount += amount * cnyPriceMap.get(c) / cnyBtcPrice;
                                } else {
                                    sumBtcAmount += amount;
                                }
                            }
                        }
                    }
                    TextView sumCny = (TextView) findViewById(R.id.asset_sum_cny);
                    TextView sumBtc = (TextView) findViewById(R.id.asset_sum_btc);
                    sumCny.setText(String.format(getString(R.string.asset_sum_cny),
                        Util.displayDouble(sumCnyAmount, 4)));
                    sumBtc.setText(String.format(getString(R.string.asset_sum_btc),
                        Util.displayDouble(sumBtcAmount, 4)));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        ts.execute();
    }

    private Map<String, Double> getPriceMap(JSONArray jsonArray) {
        Map<String, Double> priceMap = new HashMap<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); ++i) {
                try {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    priceMap.put(jsonObj.getString("c"), Double.valueOf(jsonObj.getString("p")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return priceMap;
    }
}
