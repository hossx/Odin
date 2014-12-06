package com.coinport.odin.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.coinport.odin.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public final class Util {

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

    public static JSONArray getJsonArrayFromFile(Context context, String filename) {
        JSONArray ja = null;
        try {
            ja = getJsonObjectFromFile(context, filename).getJSONObject("data").getJSONArray("items");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ja;
    }

    private static String getApplicationRoot() {
        return Environment.getExternalStorageDirectory() + "/coinport/";
    }
}
