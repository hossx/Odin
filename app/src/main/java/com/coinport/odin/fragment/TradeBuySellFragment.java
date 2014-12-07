package com.coinport.odin.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.coinport.odin.R;
import com.coinport.odin.activity.TradeActivity;
import com.coinport.odin.adapter.DepthAdapter;
import com.coinport.odin.obj.DepthItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
            buyItems.clear();
            sellItems.clear();
            try {
                String file;
                file = "btc_cny_depth_mock.json";
                InputStream is = getActivity().getAssets().open(file);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                String bufferString = new String(buffer);
                JSONObject depthResult = new JSONObject(bufferString);

                JSONArray buyJsonList = depthResult.getJSONObject("data").getJSONArray("b");
                for (int i = 0; i < buyJsonList.length(); ++i) {
                    JSONObject jsonObj = buyJsonList.getJSONObject(i);
                    buyItems.add(DepthItem.DepthItemBuilder.generateFromJson(jsonObj, true));
                }
                JSONArray sellJsonList = depthResult.getJSONObject("data").getJSONArray("a");
                for (int i = 0; i < sellJsonList.length(); ++i) {
                    JSONObject jsonObj = sellJsonList.getJSONObject(i);
                    sellItems.add(0, DepthItem.DepthItemBuilder.generateFromJson(jsonObj, false));
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            depthHandler.post(new Runnable() {
                @Override
                public void run() {
                    buyAdapter.setDepthItems(buyItems);
                    sellAdapter.setDepthItems(sellItems);
                    buyAdapter.notifyDataSetChanged();
                    sellAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
