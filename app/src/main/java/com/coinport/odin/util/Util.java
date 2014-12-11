package com.coinport.odin.util;

import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.coinport.odin.R;
import com.google.zxing.common.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

public final class Util {
    public static HashMap<String, String> iconFont = new HashMap<>();

    public static enum ScreenDirection {
        NORMAL, INVERT, LEFT_UP, RIGHT_UP
    }

    public static Map<Integer, Integer> transferStatus = new HashMap<>();
    static {
        transferStatus.put(0, R.string.transfer_pending);
        transferStatus.put(1, R.string.transfer_processing);
        transferStatus.put(2, R.string.transfer_processed);
        transferStatus.put(3, R.string.transfer_processed);
        transferStatus.put(4, R.string.transfer_succeed);
        transferStatus.put(5, R.string.transfer_failed);
        transferStatus.put(6, R.string.transfer_succeed);
        transferStatus.put(7, R.string.transfer_succeed);
        transferStatus.put(8, R.string.transfer_cancelled);
        transferStatus.put(9, R.string.transfer_rejected);
        transferStatus.put(10, R.string.transfer_failed);
        transferStatus.put(11, R.string.transfer_processing);
        transferStatus.put(12, R.string.transfer_failed);
        transferStatus.put(13, R.string.transfer_failed);
        transferStatus.put(14, R.string.transfer_failed);
        transferStatus.put(15, R.string.transfer_failed);
        transferStatus.put(16, R.string.transfer_failed);

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

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, AbsListView.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static JSONObject getJsonObjectFromFile(Context context, String filename) {
        InputStream is = null;
        JSONObject json = null;
        try {
            is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            String bufferString = new String(buffer);
            json = new JSONObject(bufferString);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (is != null) try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return json;
    }

    public static JSONObject getJsonObjectByPath(JSONObject obj, String pathStr) {
        JSONObject result = null;
        JSONObject newObj = obj;
        String[] paths = pathStr.split("//.");
        try {
            for (int i = 0; i < paths.length; ++i) {
                result = newObj.getJSONObject(paths[i]);
                newObj = result;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JSONArray getJsonArrayByPath(JSONObject obj, String pathStr) {
        JSONArray result = null;
        JSONObject tmpObj = null;
        String name = null;
        int baseEnd = pathStr.lastIndexOf(".");
        if (baseEnd == -1) {
            tmpObj = obj;
            name = pathStr;
        } else {
            tmpObj = getJsonObjectByPath(obj, pathStr.substring(0, baseEnd));
            name = pathStr.substring(baseEnd + 1, pathStr.length());
        }
        try {
            result =  tmpObj.getJSONArray(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JSONArray getJsonArrayFromFile(Context context, String filename) {
        return getJsonArrayByPath(getJsonObjectFromFile(context, filename), "data.items");
    }

    private static String getApplicationRoot() {
        return Environment.getExternalStorageDirectory() + "/coinport/";
    }
}
