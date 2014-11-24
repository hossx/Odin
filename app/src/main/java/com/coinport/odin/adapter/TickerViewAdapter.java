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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hoss on 14-11-24.
 */
public class TickerViewAdapter extends BaseAdapter {
    final int CP_GREEN = Color.rgb(10, 185, 43);
    private LayoutInflater inflater;
    private Context context = null;
    private ArrayList<TickerItem> tickerItems;
    private HashMap<String, String> iconFont = new HashMap<String, String>();
    private Typeface iconTF;

    public TickerViewAdapter(Context context) {
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

    public TickerViewAdapter setTickerItems(ArrayList<TickerItem> tickerItems) {
        this.tickerItems = tickerItems;
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
            price.setTextColor(CP_GREEN);
            amplitude.setTextColor(CP_GREEN);
        } else {
            price.setTextColor(Color.RED);
            amplitude.setTextColor(Color.RED);
        }

        return convertView;
    }
}
