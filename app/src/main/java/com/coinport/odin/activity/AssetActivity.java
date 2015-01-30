package com.coinport.odin.activity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.Time;
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
import com.coinport.odin.dialog.CustomProgressDialog;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AssetActivity extends Activity {
    private ArrayList<HashMap<String, String>> assetItems = new ArrayList<>();
    private SimpleAdapter adapter;
    protected PullToRefreshScrollView refreshableView;

    private CustomProgressDialog cpd = null;
    private Time now = new Time();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_asset);

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
                icon.setTypeface(App.getIconTf());
                icon.setTextColor(Color.WHITE);
                icon.setText(Util.iconFont.get(currency));
                icon.setTextSize(20);
                return view;
            }
        };

        ListView lv = (ListView) findViewById(R.id.assets);
        lv.setAdapter(adapter);
        lv.setEnabled(false);
        getActionBar().setDisplayShowHomeEnabled(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        cpd = CustomProgressDialog.createDialog(this);
        cpd.show();
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
                if (cpd != null) {
                    cpd.dismiss();
                    cpd = null;
                }
                NetworkRequest assetRes = s.get("asset");
                NetworkRequest cnyTickerRes = s.get("cnyT");
                NetworkRequest btcTickerRes = s.get("btcT");
                if (isRefresh) {
                    now.setToNow();
                    String label = String.format(getString(R.string.last_updated_at), now.format("%Y-%m-%d %k:%M:%S"));
                    refreshableView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                    refreshableView.onRefreshComplete();
                }
                if (assetRes.getApiStatus() != NetworkRequest.ApiStatus.SUCCEED ||
                    cnyTickerRes.getApiStatus() != NetworkRequest.ApiStatus.SUCCEED ||
                    btcTickerRes.getApiStatus() != NetworkRequest.ApiStatus.SUCCEED) {
                    Toast.makeText(AssetActivity.this, getString(R.string.request_failed), Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    JSONObject assetObject = Util.getJsonObjectByPath(assetRes.getApiResult(), "data.accounts");
                    Map<String, Double> assetMap = new HashMap<>();
                    assetItems.clear();
                    Iterator<String> it = assetObject.keys();
                    while (it.hasNext()) {
                        HashMap<String, String> fields = new HashMap<>();
                        JSONObject jsonObj = assetObject.getJSONObject(it.next());
                        double pending = jsonObj.getJSONObject("locked").getDouble("value") +
                                jsonObj.getJSONObject("pendingWithdrawal").getDouble("value");
                        fields.put("currency", jsonObj.getString("currency"));
                        fields.put("valid",
                            Util.autoDisplayDouble(jsonObj.getJSONObject("available").getDouble("value")));
                        fields.put("pending", Util.autoDisplayDouble(pending));
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
                        switch (c) {
                            case "CNY":
                                sumCnyAmount += amount;
                                sumBtcAmount += (amount / cnyBtcPrice);
                                break;
                            case "BTC":
                                sumBtcAmount += amount;
                                sumCnyAmount += (amount * cnyBtcPrice);
                                break;
                            default:
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
                                break;
                        }
                    }
                    TextView sumCny = (TextView) findViewById(R.id.asset_sum_cny);
                    TextView sumBtc = (TextView) findViewById(R.id.asset_sum_btc);
                    sumCny.setText(String.format(getString(R.string.asset_sum_cny),
                            Util.autoDisplayDouble(sumCnyAmount)));
                    sumBtc.setText(String.format(getString(R.string.asset_sum_btc),
                            Util.autoDisplayDouble(sumBtcAmount)));

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
                    priceMap.put(jsonObj.getString("c"), Util.s2d(jsonObj.getString("p")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return priceMap;
    }
}
