package com.coinport.odin.obj;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hoss on 14-11-27.
 */
public class DepthItem {
    private double price;
    private double amount;
    private String priceDisplay;
    private String amountDisplay;
    private boolean isBuy;

    public static class DepthItemBuilder {
        public static DepthItem generateFromJson(JSONObject json, boolean isBuy) {
            DepthItem item = new DepthItem();
            try {
                item.setPrice(json.getDouble("pv")).setAmount(json.getDouble("av"))
                        .setPriceDisplay(json.getString("p")).setAmountDisplay(json.getString("a")).setBuy(isBuy);
                return item;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

    }

    public boolean isBuy() {
        return isBuy;
    }

    public DepthItem setBuy(boolean isBuy) {
        this.isBuy = isBuy;
        return this;
    }

    public double getPrice() {
        return price;
    }

    public DepthItem setPrice(double price) {
        this.price = price;
        return this;
    }

    public double getAmount() {
        return amount;
    }

    public DepthItem setAmount(double amount) {
        this.amount = amount;
        return this;
    }

    public String getPriceDisplay() {
        return priceDisplay;
    }

    public DepthItem setPriceDisplay(String priceDisplay) {
        this.priceDisplay = priceDisplay;
        return this;
    }

    public String getAmountDisplay() {
        return amountDisplay;
    }

    public DepthItem setAmountDisplay(String amountDisplay) {
        this.amountDisplay = amountDisplay;
        return this;
    }
}
