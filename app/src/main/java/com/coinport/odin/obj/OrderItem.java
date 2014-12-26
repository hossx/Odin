package com.coinport.odin.obj;

import com.coinport.odin.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Created by hoss on 14-12-2.
 */
public class OrderItem {
    static private final MathContext MC = new MathContext(6, RoundingMode.HALF_EVEN);
    private String id;
    private String operation;
    private int status;
    private long submitTime;
    private String submitPrice;
    private String submitAmount;
    private String actualPrice;
    private String actualAmount;


    public static class OrderItemBuilder {
        public static OrderItem generateFromJson(JSONObject json) {
            OrderItem item = new OrderItem();
            try {
                double finishedAmount = json.getJSONObject("finishedAmount").getDouble("value");
                double finishedQuantity = json.getJSONObject("finishedQuantity").getDouble("value");
                String price = "0";
                if (finishedQuantity > 0.000000001) {
                    price = (new BigDecimal(finishedAmount / finishedQuantity, MC)).toPlainString();
                }
                item.setId(json.getString("id")).setOperation(json.getString("operation"))
                    .setStatus(json.getInt("status")).setSubmitTime(json.getLong("submitTime")).setActualPrice(price)
                    .setSubmitPrice(json.getJSONObject("price").getString("display"))
                    .setSubmitAmount(Util.autoDisplayDouble(json.getJSONObject("amount").getDouble("value")))
                    .setActualAmount(Util.autoDisplayDouble(json.getJSONObject("finishedQuantity").getDouble("value")));
                return item;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

    }

    public String getActualAmount() {
        return actualAmount;
    }

    public OrderItem setActualAmount(String actualAmount) {
        this.actualAmount = actualAmount;
        return this;
    }

    public String getActualPrice() {
        return actualPrice;
    }

    public OrderItem setActualPrice(String actualPrice) {
        this.actualPrice = actualPrice;
        return this;
    }

    public String getSubmitAmount() {
        return submitAmount;
    }

    public OrderItem setSubmitAmount(String submitAmount) {
        this.submitAmount = submitAmount;
        return this;
    }

    public String getSubmitPrice() {
        return submitPrice;
    }

    public OrderItem setSubmitPrice(String submitPrice) {
        this.submitPrice = submitPrice;
        return this;
    }

    public long getSubmitTime() {
        return submitTime;
    }

    public OrderItem setSubmitTime(long submitTime) {
        this.submitTime = submitTime;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public OrderItem setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getOperation() {
        return operation;
    }

    public OrderItem setOperation(String operation) {
        this.operation = operation;
        return this;
    }

    public String getId() {
        return id;
    }

    public OrderItem setId(String id) {
        this.id = id;
        return this;
    }
}
