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
//    public static final String DOMAIN_PORT = "10.0.2.2:9000";
//    public static final String PROTOCOL = "http://";
    public static final String DOMAIN_PORT = "exchange.coinport.com";
    public static final String PROTOCOL = "https://";

    public static final String TERMS_RUL = PROTOCOL + DOMAIN_PORT + "/terms.html";
    public static final String TICKER_URL = PROTOCOL + DOMAIN_PORT + "/api/m/ticker/";
    public static final String DEPTH_URL = PROTOCOL + DOMAIN_PORT + "/api/m/%1$s-%2$s/depth";
    public static final String TX_URL = PROTOCOL + DOMAIN_PORT + "/api/%1$s-%2$s/transaction";
    public static final String LOGIN_URL = PROTOCOL + DOMAIN_PORT + "/account/login";
//    public static final String PROFILE_URL = PROTOCOL + DOMAIN_PORT + "/account#/accountprofiles";
    public static final String BID_URL = PROTOCOL + DOMAIN_PORT + "/trade/%1$s-%2$s/bid";
    public static final String ASK_URL = PROTOCOL + DOMAIN_PORT + "/trade/%1$s-%2$s/ask";
    public static final String ASSET_URL = PROTOCOL + DOMAIN_PORT + "/api/account/%1$s";
    public static final String ORDER_URL = PROTOCOL + DOMAIN_PORT + "/api/user/%1$s/order/%2$s-%3$s";
    public static final String CANCEL_ORDER_URL = PROTOCOL + DOMAIN_PORT + "/trade/%1$s-%2$s/order/cancel/%3$s";
    public static final String TRANSFER_URL = PROTOCOL + DOMAIN_PORT + "/api/%1$s/transfer/%2$s";
//    public static final String TRANSFER_URL = "https://exchange.coinport.com/api/%1$s/transfer/%2$s";
    public static final String DEPOSIT_ADDRESS_URL = PROTOCOL + DOMAIN_PORT + "/depoaddr/%1$s/%2$s";
//    public static final String DEPOSIT_ADDRESS_URL = "https://exchange.coinport.com/depoaddr/%1$s/%2$s";
    public static final String FEE_URL = PROTOCOL + DOMAIN_PORT + "/api/m/fee";
    public static final String EMAIL_CODE_URL = PROTOCOL + DOMAIN_PORT + "/emailverification";
    public static final String SMS_CODE_URL = PROTOCOL + DOMAIN_PORT + "/smsverification2";
    public static final String WITHDRAWAL_URL = PROTOCOL + DOMAIN_PORT + "/account/withdrawal";
    public static final String USER_VERIFY_URL = PROTOCOL + DOMAIN_PORT + "/account/realnameverify";
    public static final String BANK_CARD_URL = PROTOCOL + DOMAIN_PORT + "/account/querybankcards";
    public static final String ADD_BANK_CARD_URL = PROTOCOL + DOMAIN_PORT + "/account/addbankcard";
    public static final String RM_BANK_CARD_URL = PROTOCOL + DOMAIN_PORT + "/account/deletebankcard";
    public static final String CHANGE_PW_URL = PROTOCOL + DOMAIN_PORT + "/account/dochangepwd";
    public static final String LOGOUT_URL = PROTOCOL + DOMAIN_PORT + "/account/logout";
    public static final String CAPTCHA_URL = PROTOCOL + DOMAIN_PORT + "/captcha";
    public static final String REGISTER_URL = PROTOCOL + DOMAIN_PORT + "/account/register";
    public static final String KLINE_URL = PROTOCOL + DOMAIN_PORT + "/api/%1$s/history";

    public static final String ANDROID_APP_VERSION_URL  = PROTOCOL + DOMAIN_PORT + "/app/android/download/version.json";
    public static final String ANDROID_APP_DOWNLOAD_URL = PROTOCOL + DOMAIN_PORT + "/app/android/download/coinport.apk";

    public static final String PLAY_SESSION = "PLAY_SESSION";
    static {
        Map<Integer, Integer> osMap = new HashMap<>();
        osMap.put(0, R.string.order_status_pending);
        osMap.put(1, R.string.order_status_pending);
        osMap.put(2, R.string.order_status_done);
        osMap.put(3, R.string.order_status_canceled);
        osMap.put(4, R.string.order_status_canceled);
        osMap.put(5, R.string.order_status_canceled);
        ORDER_STATUS_MAP = Collections.unmodifiableMap(osMap);
    }

    public enum HttpMethod {
        GET, POST
    }
}
