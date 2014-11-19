package com.coinport.odin.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.coinport.odin.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class MarketActivity extends Activity {

    private HttpClient client = null;
//    private Button tickerBtn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.market);

        client = new DefaultHttpClient();
//        tickerBtn = (Button) findViewById(R.id.tickerBtn);
//        tickerBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new AsyncTask<String, Void, String>() {
//                    @Override
//                    protected String doInBackground(String... params) {
//                        String url = params[0];
//
//                        HttpGet get = new HttpGet(url);
//                        try {
//                            HttpResponse response = client.execute(get);
//                            String res = EntityUtils.toString(response.getEntity());
//                            return res;
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        return null;
//                    }
//
//                    @Override
//                    protected void onPostExecute(String s) {
//                        super.onPostExecute(s);
//                        System.out.println(s);
//                    }
//                }.execute("http://192.168.0.2:9000/api/m/ticker/cny");
//            }
//        });
    }
}
