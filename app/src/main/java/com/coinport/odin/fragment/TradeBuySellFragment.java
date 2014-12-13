package com.coinport.odin.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.coinport.odin.R;
import com.coinport.odin.activity.TradeActivity;
import com.coinport.odin.adapter.DepthAdapter;
import com.coinport.odin.obj.DepthItem;
import com.coinport.odin.util.Constants;
import com.coinport.odin.network.NetworkRequest;
import com.coinport.odin.util.Util;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
        View buySellView = inflater.inflate(R.layout.trade_buy_sell_fragment, container, false);
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
    }

    private void stopFetchData() {
        if (timer != null)
            timer.cancel();
        if (fetchDepthTask != null)
            fetchDepthTask.cancel();
    }

    private class FetchDepthTask extends TimerTask {

        @Override
        public void run() {
            try {
                String url = String.format(Constants.depthUrl, inCurrency.toLowerCase(), outCurrency.toLowerCase());
                NetworkRequest get = new NetworkRequest();
                get.setCharset(HTTP.UTF_8).setConnectionTimeout(5000).setSoTimeout(5000).setOnHttpRequestListener(
                        new NetworkRequest.OnHttpRequestListener() {
                    @Override
                    public void onRequest(NetworkRequest request) throws Exception {

                    }

                    @Override
                    public String onSucceed(int statusCode, NetworkRequest request) throws Exception {
                        String result =  request.getInputStream();
                        JSONObject depthResult = new JSONObject(result);
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
                        return result;
                    }

                    @Override
                    public String onFailed(int statusCode, NetworkRequest request) throws Exception {
                        return "GET 请求失败：statusCode "+ statusCode;
                    }
                }).get(url);

                url = String.format(Constants.txUrl, inCurrency.toLowerCase(), outCurrency.toLowerCase());
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
                            public String onSucceed(int statusCode, NetworkRequest request) throws Exception {
                                String result =  request.getInputStream();
                                JSONObject txResult = new JSONObject(result);
                                lastPrice = Util.getJsonObjectByPath(Util.getJsonArrayByPath(txResult, "data.items")
                                    .getJSONObject(0), "price").getString("display");
                                return result;
                            }

                            @Override
                            public String onFailed(int statusCode, NetworkRequest request) throws Exception {
                                return "GET 请求失败：statusCode "+ statusCode;
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
