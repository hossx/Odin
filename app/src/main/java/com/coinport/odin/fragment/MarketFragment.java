package com.coinport.odin.fragment;

import android.app.ProgressDialog;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.coinport.odin.R;
import com.coinport.odin.activity.MainActivity;
import com.coinport.odin.adapter.TickerViewAdapter;
import com.coinport.odin.obj.TickerItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by hoss on 14-11-23.
 */
public class MarketFragment extends Fragment {
//    private ProgressDialog dialogRef;
    private JSONArray jsonList;
    private ListView tickerListView;
    private TickerViewAdapter tva;
    private String baseCurrency = "CNY";

    private final Handler handler = new Handler();
    ArrayList<TickerItem> tickerItems = new ArrayList<TickerItem>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View marketView = inflater.inflate(R.layout.market_fragment, container, false);
        tickerListView = (ListView) marketView.findViewById(R.id.MarketView);
        tickerListView.setVerticalScrollBarEnabled(false);
        tva = new TickerViewAdapter(this.getActivity());
        tickerListView.setAdapter(tva);
        getDataWithBaseCurrency(baseCurrency);
        return marketView;
    }

    public TickerViewAdapter getAdapter() {
        return tva;
    }

    public MarketFragment setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
        return this;
    }

    public void getDataWithBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
        tva.setTickerItems(null).notifyDataSetChanged();
//        dialogRef = ((MainActivity) getActivity()).getpDialog();
//        dialogRef.setTitle("提示");
//        dialogRef.setMessage("正在下载，请稍后...");
//        dialogRef.setCancelable(false);
        new Thread(new FetchTickerTask()).start();
//        dialogRef.show();
    }

    public class FetchTickerTask implements Runnable {
        @Override
        public void run() {
            tickerItems.clear();
            try {
                String file;
                if (baseCurrency == "CNY")
                    file = "cny_mock_markets.json";
                else
                    file = "btc_mock_markets.json";
                InputStream is = getActivity().getAssets().open(file);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                String bufferString = new String(buffer);
                jsonList = new JSONArray(bufferString);
                for (int i = 0; i < jsonList.length(); ++i) {
                    JSONObject jsonObj = jsonList.getJSONObject(i);
                    tickerItems.add(TickerItem.TickerItemBuilder.generateFromJson(jsonObj));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    tva.setTickerItems(tickerItems);
                    tva.notifyDataSetChanged();
//                    dialogRef.dismiss();
                }
            });
        }
    }
}
