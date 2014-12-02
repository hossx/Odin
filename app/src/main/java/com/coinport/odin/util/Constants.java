package com.coinport.odin.util;

import android.graphics.Color;

import com.coinport.odin.R;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hoss on 14-11-27.
 */
public class Constants {
    public static final int CP_GREEN = Color.rgb(10, 185, 43);
    public static final int CP_RED = Color.RED;
    public static final Map<Integer, Integer> ORDER_STATUS_MAP;
    static {
        Map<Integer, Integer> tMap = new HashMap<Integer, Integer>();
        tMap.put(0, R.string.order_status_pending);
        tMap.put(1, R.string.order_status_pending);
        tMap.put(2, R.string.order_status_done);
        tMap.put(3, R.string.order_status_canceled);
        tMap.put(4, R.string.order_status_canceled);
        tMap.put(5, R.string.order_status_canceled);
        ORDER_STATUS_MAP = Collections.unmodifiableMap(tMap);
    }
}
