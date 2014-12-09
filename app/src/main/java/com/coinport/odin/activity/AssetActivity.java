package com.coinport.odin.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.coinport.odin.R;
import com.coinport.odin.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class AssetActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_asset);

        TextView sumCny = (TextView) findViewById(R.id.asset_sum_cny);
        TextView sumBtc = (TextView) findViewById(R.id.asset_sum_btc);
        sumCny.setText(String.format(getString(R.string.asset_sum_cny), "314324.2432"));
        sumBtc.setText(String.format(getString(R.string.asset_sum_btc), "4324.2432"));

        ListView lv = (ListView) findViewById(R.id.assets);
        ArrayList<HashMap<String, String>> aiList = new ArrayList<>();
        try {
            JSONObject jsonObject = Util.getJsonObjectFromFile(this, "asset_mock.json").getJSONObject("data")
                .getJSONObject("accounts");
            Iterator<String> it = jsonObject.keys();
            while (it.hasNext()) {
                HashMap<String, String> fields = new HashMap<>();
                JSONObject jsonObj = jsonObject.getJSONObject(it.next());
                double pending = jsonObj.getJSONObject("locked").getDouble("value") +
                    jsonObj.getJSONObject("pendingWithdrawal").getDouble("value");
                fields.put("currency", jsonObj.getString("currency"));
                fields.put("valid", jsonObj.getJSONObject("available").getString("display"));
                fields.put("pending", (new BigDecimal(pending).setScale(4, RoundingMode.CEILING)).toPlainString());
                aiList.add(fields);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SimpleAdapter adapter = new SimpleAdapter(this, aiList, R.layout.asset_item, new String[]{
            "currency", "valid", "pending"}, new int[] {R.id.asset_currency, R.id.asset_valid, R.id.asset_pending});
        lv.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_asset, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
