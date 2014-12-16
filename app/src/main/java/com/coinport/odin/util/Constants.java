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
    public static final String DOMAIN_PORT = "10.0.2.2:9000";
    public static final String PROTOCOL = "http://";

    public static final String TICKER_URL = PROTOCOL + DOMAIN_PORT + "/api/m/ticker/";
    public static final String DEPTH_URL = PROTOCOL + DOMAIN_PORT + "/api/m/%1$s-%2$s/depth";
    public static final String TX_URL = PROTOCOL + DOMAIN_PORT + "/api/%1$s-%2$s/transaction";
    public static final String LOGIN_URL = PROTOCOL + DOMAIN_PORT + "/account/login";
//    public static final String PROFILE_URL = PROTOCOL + DOMAIN_PORT + "/account#/accountprofiles";
    public static final String BID_URL = PROTOCOL + DOMAIN_PORT + "/trade/%1$s-%2$s/bid";
    public static final String ASK_URL = PROTOCOL + DOMAIN_PORT + "/trade/%1$s-%2$s/ask";
    public static final String ASSET_URL = PROTOCOL + DOMAIN_PORT + "/api/account/%1$s";

    public static final String PLAY_SESSION = "PLAY_SESSION";
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
