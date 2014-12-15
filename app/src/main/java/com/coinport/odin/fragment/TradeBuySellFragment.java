package com.coinport.odin.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.coinport.odin.App;
import com.coinport.odin.R;
import com.coinport.odin.activity.TradeActivity;
import com.coinport.odin.adapter.DepthAdapter;
import com.coinport.odin.network.NetworkAsyncTask;
import com.coinport.odin.network.OnApiResponseListener;
import com.coinport.odin.obj.AccountInfo;
import com.coinport.odin.obj.DepthItem;
import com.coinport.odin.util.Constants;
import com.coinport.odin.network.NetworkRequest;
import com.coinport.odin.util.Util;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TradeBuySellFragment extends Fragment {
    private ListView buyListView;
    private DepthAdapter buyAdapter;
    private DepthAdapter sellAdapter;
    private String inCurrency, outCurrency;

    private Timer timer = new Timer();
    private TimerTask fetchDepthTask = null;
    ArrayList<DepthItem> buyItems = new ArrayList<>();
    ArrayList<DepthItem> sellItems = new ArrayList<>();
    private final Handler depthHandler = new Handler();
    private TextView lastPriceView;
    private String lastPrice;

    private View buySellView = null;
    public TradeBuySellFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inCurrency = ((TradeActivity)getActivity()).getInCurrency();
        outCurrency = ((TradeActivity)getActivity()).getOutCurrency();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopFetchData();
    }

    @Override
    public void onStart() {
        super.onStart();
        startFetchData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        buySellView = inflater.inflate(R.layout.trade_buy_sell_fragment, container, false);
        buyListView = (ListView) buySellView.findViewById(R.id.buy_depth);
        ListView sellListView = (ListView) buySellView.findViewById(R.id.sell_depth);
        buyAdapter = new DepthAdapter(getActivity());
        buyListView.setAdapter(buyAdapter);
        sellAdapter = new DepthAdapter(getActivity());
        sellListView.setAdapter(sellAdapter);
        lastPriceView = (TextView) buySellView.findViewById(R.id.last_price);
        return buySellView;
    }

    private void startFetchData() {
        timer.cancel();
        if (fetchDepthTask != null)
            fetchDepthTask.cancel();
        fetchDepthTask = new FetchDepthTask();
        timer = new Timer();
        timer.schedule(fetchDepthTask, 0, 5000);
        fetchAsset();
    }

    private void stopFetchData() {
        if (timer != null)
            timer.cancel();
        if (fetchDepthTask != null)
            fetchDepthTask.cancel();
    }

    private void fetchAsset() {
        AccountInfo ai = App.getAccount();
        String url = String.format(Constants.ASSET_URL, ai.uid);
        NetworkAsyncTask task = new NetworkAsyncTask(url, Constants.HttpMethod.GET)
            .setOnSucceedListener(new OnApiResponseListener())
            .setOnFailedListener(new OnApiResponseListener())
            .setRenderListener(new NetworkAsyncTask.OnPostRenderListener() {
                @Override
                public void onRender(NetworkRequest s) {
                    JSONObject inCurrencyObj = Util.getJsonObjectByPath(s.getApiResult(),
                        "data.accounts." + inCurrency);
                    JSONObject outCurrencyObj = Util.getJsonObjectByPath(s.getApiResult(),
                            "data.accounts." + outCurrency);
                    if (buySellView == null)
                        return;
                    try {
                        String inValid, outValid, inPending, outPending;
                        double inPendingV, outPendingV;
                        if (inCurrencyObj != null) {
                            inValid = inCurrencyObj.getJSONObject("available").getString("display");
                            inPendingV = inCurrencyObj.getJSONObject("locked").getDouble("value") +
                                inCurrencyObj.getJSONObject("pendingWithdrawal").getDouble("value");
                            inPending = (new BigDecimal(inPendingV).setScale(4, RoundingMode.CEILING)).toPlainString();
                        } else {
                            inValid = "0";
                            inPending = "0";
                        }
                        if (outCurrencyObj != null) {
                            outValid = outCurrencyObj.getJSONObject("available").getString("display");
                            outPendingV = outCurrencyObj.getJSONObject("locked").getDouble("value") +
                                outCurrencyObj.getJSONObject("pendingWithdrawal").getDouble("value");
                            outPending = (new BigDecimal(outPendingV).setScale(4, RoundingMode.CEILING))
                                .toPlainString();
                        } else {
                            outValid = "0";
                            outPending = "0";
                        }
                        TextView inValidView = (TextView)buySellView.findViewById(R.id.in_valid_amount);
                        inValidView.setText(inValid);
                        TextView inFrozenView = (TextView) buySellView.findViewById(R.id.in_frozen_amount);
                        inFrozenView.setText(inPending);
                        TextView outValidView = (TextView)buySellView.findViewById(R.id.out_valid_amount);
                        outValidView.setText(outValid);
                        TextView outFrozenView = (TextView) buySellView.findViewById(R.id.out_frozen_amount);
                        outFrozenView.setText(outPending);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        task.execute();
    }

    private class FetchDepthTask extends TimerTask {

        @Override
        public void run() {
            try {
                String url = String.format(Constants.DEPTH_URL, inCurrency.toLowerCase(), outCurrency.toLowerCase());
                NetworkRequest get = new NetworkRequest();
                get.setCharset(HTTP.UTF_8).setConnectionTimeout(5000).setSoTimeout(5000).setOnHttpRequestListener(
                        new NetworkRequest.OnHttpRequestListener() {
                    @Override
                    public void onRequest(NetworkRequest request) throws Exception {

                    }

                    @Override
                    public NetworkRequest onSucceed(int statusCode, NetworkRequest request) throws Exception {
                        JSONObject depthResult = new JSONObject(request.getInputStream());
                        JSONArray buyJsonList = Util.getJsonArrayByPath(depthResult, "data.b");
                        buyItems.clear();
                        for (int i = 0; i < buyJsonList.length(); ++i) {
                            JSONObject jsonObj = buyJsonList.getJSONObject(i);
                            buyItems.add(DepthItem.DepthItemBuilder.generateFromJson(jsonObj, true));
                        }
                        JSONArray sellJsonList = Util.getJsonArrayByPath(depthResult, "data.a");
                        sellItems.clear();
                        for (int i = 0; i < sellJsonList.length(); ++i) {
                            JSONObject jsonObj = sellJsonList.getJSONObject(i);
                            sellItems.add(0, DepthItem.DepthItemBuilder.generateFromJson(jsonObj, false));
                        }
                        return request;
                    }

                    @Override
                    public NetworkRequest onFailed(int statusCode, NetworkRequest request) throws Exception {
                        return request;
//                        return "GET 请求失败：statusCode "+ statusCode;
                    }
                }).get(url);

                url = String.format(Constants.TX_URL, inCurrency.toLowerCase(), outCurrency.toLowerCase());
                NetworkRequest getTx = new NetworkRequest();
                getTx.setCharset(HTTP.UTF_8).setConnectionTimeout(5000).setSoTimeout(5000).setOnHttpRequestListener(
                        new NetworkRequest.OnHttpRequestListener() {
                            @Override
                            public void onRequest(NetworkRequest request) throws Exception {
                                Map<String, String> params = new HashMap<>();
                                params.put("limit", "1");
                                params.put("skip", "0");
                                request.addRequestParameters(params);
                            }

                            @Override
                            public NetworkRequest onSucceed(int statusCode, NetworkRequest request) throws Exception {
                                JSONObject txResult = new JSONObject(request.getInputStream());
                                lastPrice = Util.getJsonObjectByPath(Util.getJsonArrayByPath(txResult, "data.items")
                                    .getJSONObject(0), "price").getString("display");
                                return request;
                            }

                            @Override
                            public NetworkRequest onFailed(int statusCode, NetworkRequest request) throws Exception {
                                return request;
//                                return "GET 请求失败：statusCode "+ statusCode;
                            }
                        }).get(url);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            depthHandler.post(new Runnable() {
                @Override
                public void run() {
                    buyAdapter.setDepthItems(buyItems);
                    sellAdapter.setDepthItems(sellItems);
                    buyAdapter.notifyDataSetChanged();
                    sellAdapter.notifyDataSetChanged();
                    lastPriceView.setText(lastPrice);
                }
            });
        }
    }
}
