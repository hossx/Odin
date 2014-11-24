package com.coinport.odin.obj;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hoss on 14-11-24.
 */
public class TickerItem {
    private String inCurrency;
    private String outCurrency;
    private String price;
    private String volume;
    private double amplitude;
    private String trend;

    public static class TickerItemBuilder {
        public static TickerItem generateFromJson(JSONObject json) {
            TickerItem item = new TickerItem();
            try {
                item.setInCurrency(json.getString("i")).setOutCurrency(json.getString("c"))
                        .setPrice(json.getString("p")).setVolume(json.getString("v"))
                        .setAmplitude(json.getDouble("g")).setTrend(json.getString("t"));
                return item;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

    }


    public String getInCurrency() {
        return inCurrency;
    }

    public TickerItem setInCurrency(String inCurrency) {
        this.inCurrency = inCurrency;
        return this;
    }

    public String getOutCurrency() {
        return outCurrency;
    }

    public TickerItem setOutCurrency(String outCurrency) {
        this.outCurrency = outCurrency;
        return this;
    }

    public String getPrice() {
        return price;
    }

    public TickerItem setPrice(String price) {
        this.price = price;
        return this;
    }

    public String getVolume() {
        return volume;
    }

    public TickerItem setVolume(String volume) {
        this.volume = volume;
        return this;
    }

    public double getAmplitude() {
        return amplitude;
    }

    public TickerItem setAmplitude(double amplitude) {
        this.amplitude = amplitude;
        return this;
    }

    public String getTrend() {
        return trend;
    }

    public TickerItem setTrend(String trend) {
        this.trend = trend;
        return this;
    }
}
