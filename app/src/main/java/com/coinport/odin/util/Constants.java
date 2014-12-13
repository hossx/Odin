package com.coinport.odin.util;

import android.graphics.Color;

import com.coinport.odin.R;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final int CP_GREEN = Color.rgb(10, 185, 43);
    public static final int CP_RED = Color.RED;
    public static final Map<Integer, Integer> ORDER_STATUS_MAP;
    public static final String tickerUrl = "https://exchange.coinport.com/api/m/ticker/";
    public static final String depthUrl = "https://exchange.coinport.com/api/m/%1$s-%2$s/depth";
    public static final String txUrl = "https://exchange.coinport.com/api/%1$s-%2$s/transaction";
    public static final String loginUrl = "http://192.168.0.2:9000/account/login";

    static {
        Map<Integer, Integer> tMap = new HashMap<>();
        tMap.put(0, R.string.order_status_pending);
        tMap.put(1, R.string.order_status_pending);
        tMap.put(2, R.string.order_status_done);
        tMap.put(3, R.string.order_status_canceled);
        tMap.put(4, R.string.order_status_canceled);
        tMap.put(5, R.string.order_status_canceled);
        ORDER_STATUS_MAP = Collections.unmodifiableMap(tMap);
    }

    public enum HttpMethod {
        GET, POST
    }
}
