package com.coinport.odin.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.coinport.odin.R;
import com.coinport.odin.activity.TradeActivity;
import com.coinport.odin.adapter.TickerAdapter;
import com.coinport.odin.dialog.CustomProgressDialog;
import com.coinport.odin.network.NetworkRequest;
import com.coinport.odin.obj.TickerItem;
import com.coinport.odin.util.Constants;
import com.coinport.odin.util.Util;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MarketFragment extends Fragment {
    private TickerAdapter tva;
    private String baseCurrency = "CNY";
    private TextView updateTimeRef;

    private final Handler handler = new Handler();
    private Timer timer = new Timer();
    private TimerTask fetchTickerTask = null;
    private ArrayList<TickerItem> tickerItems = new ArrayList<>();

    private Time now = new Time();
    private CustomProgressDialog cpd = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View marketView = inflater.inflate(R.layout.market_fragment, container, false);
        ListView tickerListView = (ListView) marketView.findViewById(R.id.MarketView);
        tickerListView.setVerticalScrollBarEnabled(false);
        tva = new TickerAdapter(this.getActivity());
        tickerListView.setAdapter(tva);
        tickerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String inCurrency = (String) ((TextView) view.findViewById(R.id.currency_name)).getText();
                Intent toTrade = new Intent();
                toTrade.setClass(getActivity(), TradeActivity.class);
                toTrade.putExtra("inCurrency", inCurrency);
                toTrade.putExtra("outCurrency", baseCurrency);
                getActivity().startActivity(toTrade);
            }
        });
//        fetchDataWithBaseCurrency(baseCurrency);
        return marketView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateTimeRef = (TextView) getActivity().findViewById(R.id.updateTime);
    }

    public TickerAdapter getAdapter() {
        return tva;
    }

    public void fetchDataWithBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
        timer.cancel();
        if (fetchTickerTask != null)
            fetchTickerTask.cancel();
        fetchTickerTask = new FetchTickerTask();
        timer = new Timer();
//        tva.setTickerItems(null, baseCurrency).notifyDataSetChanged();
        timer.schedule(fetchTickerTask, 0, 5000);
//        dialogRef = ((MainActivity) getActivity()).getpDialog();
//        dialogRef.setTitle("提示");
//        dialogRef.setMessage("正在下载，请稍后...");
//        new Thread(new FetchTickerTask()).start();
//        dialogRef.show();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            cpd = CustomProgressDialog.createDialog(getActivity());
            cpd.show();
            fetchDataWithBaseCurrency(baseCurrency);
        } else {
            if (timer != null)
                timer.cancel();
            if (fetchTickerTask != null)
                fetchTickerTask.cancel();
        }
    }

    private class FetchTickerTask extends TimerTask {
        @Override
        public void run() {
            tickerItems.clear();
            try {
                String url = Constants.TICKER_URL + baseCurrency.toLowerCase();
                final Long start = System.currentTimeMillis();
                NetworkRequest get = new NetworkRequest();
                get.setCharset(HTTP.UTF_8).setConnectionTimeout(5000).setSoTimeout(5000);
                get.setOnHttpRequestListener(new NetworkRequest.OnHttpRequestListener() {
                    @Override
                    public void onRequest(NetworkRequest request) throws Exception {

                    }

                    @Override
                    public NetworkRequest onSucceed(int statusCode, NetworkRequest request) throws Exception {
                        JSONArray jsonList = Util.getJsonArrayByPath(new JSONObject(request.getInputStream()), "data");
                        if (jsonList != null)
                            for (int i = 0; i < jsonList.length(); ++i) {
                                JSONObject jsonObj = jsonList.getJSONObject(i);
                                tickerItems.add(TickerItem.TickerItemBuilder.generateFromJson(jsonObj));
                            }
                        Log.i("market_fragment", String.valueOf(System.currentTimeMillis() - start));
                        return request;
                    }

                    @Override
                    public NetworkRequest onFailed(int statusCode, NetworkRequest request) throws Exception {
                        return request;
//                        return "GET 请求失败：statusCode "+ statusCode;
                    }
                });

                get.get(url);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    tva.setTickerItems(tickerItems, baseCurrency);
                    now.setToNow();
                    updateTimeRef.setText(now.format("%Y-%m-%d %k:%M:%S"));
                    tva.notifyDataSetChanged();
                    if (cpd != null) {
                        cpd.dismiss();
                        cpd = null;
                    }
//                    dialogRef.dismiss();
                }
            });
        }
    }
}
