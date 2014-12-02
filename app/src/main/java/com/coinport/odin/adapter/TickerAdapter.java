package com.coinport.odin.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.coinport.odin.R;
import com.coinport.odin.obj.TickerItem;
import com.coinport.odin.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hoss on 14-11-24.
 */
public class TickerAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context = null;
    private ArrayList<TickerItem> tickerItems;
    private HashMap<String, String> iconFont = new HashMap<String, String>();
    private Typeface iconTF;
    private String baseCurrency;

    public TickerAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        iconTF = Typeface.createFromAsset(context.getAssets(), "coinport.ttf");
        iconFont.put("CNY", "\ue633");
        iconFont.put("BTC", "\ue62a");
        iconFont.put("LTC", "\ue632");
        iconFont.put("DRK", "\ue629");
        iconFont.put("BTSX", "\ue62b");
        iconFont.put("XRP", "\ue62c");
        iconFont.put("NXT", "\ue62d");
        iconFont.put("ZET", "\ue62e");
        iconFont.put("VRC", "\ue62f");
        iconFont.put("BC", "\ue630");
        iconFont.put("DOGE", "\ue631");
    }

    public TickerAdapter setTickerItems(ArrayList<TickerItem> tickerItems, String baseCurrency) {
        if (tickerItems == null)
            this.tickerItems = null;
        else
            this.tickerItems = (ArrayList<TickerItem>) tickerItems.clone();
        this.baseCurrency = baseCurrency;
        return this;
    }

    @Override
    public int getCount() {
        if (tickerItems == null)
            return 0;
        else
            return tickerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.ticker_item, null);
        }
        TextView name = (TextView) convertView.findViewById(R.id.currency_name);
//        TextView icon = (TextView) convertView.findViewById(R.id.currency_icon);
        TextView price = (TextView) convertView.findViewById(R.id.price);
        TextView volume = (TextView) convertView.findViewById(R.id.volume);
        TextView amplitude = (TextView) convertView.findViewById(R.id.amplitude);
        TextView unit = (TextView) convertView.findViewById(R.id.price_unit);

        TickerItem ti = tickerItems.get(position);

        name.setText(ti.getInCurrency());
//        icon.setTypeface(iconTF);
//        icon.setTextColor(Color.BLACK);
//        icon.setText(iconFont.get(ti.getInCurrency()));
//        icon.setTextSize(20);
        price.setText(ti.getPrice());
        volume.setText(ti.getVolume());
        amplitude.setText(String.format("%1$.2f", ti.getAmplitude() * 100) + "%");
        unit.setText(ti.getOutCurrency());
        if (ti.getAmplitude() >= 0) {
            price.setTextColor(Constants.CP_GREEN);
            amplitude.setTextColor(Constants.CP_GREEN);
        } else {
            price.setTextColor(Color.RED);
            amplitude.setTextColor(Color.RED);
        }
//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent toTrade = new Intent();
//                toTrade.setClass(context, TradeActivity.class);
//                context.startActivity(toTrade);
////                System.out.println("in currency: " + ((TextView) v.findViewById(R.id.currency_name)).getText() + " out currency: " + baseCurrency);
//            }
//        });

        return convertView;
    }
}
