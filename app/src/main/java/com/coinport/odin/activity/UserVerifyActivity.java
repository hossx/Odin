package com.coinport.odin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.coinport.odin.App;
import com.coinport.odin.R;
import com.coinport.odin.network.CustomCookieStore;
import com.coinport.odin.network.NetworkAsyncTask;
import com.coinport.odin.network.NetworkRequest;
import com.coinport.odin.network.OnApiResponseListener;
import com.coinport.odin.obj.AccountInfo;
import com.coinport.odin.util.Constants;
import com.coinport.odin.util.Util;

import org.apache.http.cookie.Cookie;

import java.util.HashMap;
import java.util.Map;

public class UserVerifyActivity extends Activity implements View.OnClickListener {
    private Spinner regionSelector, typeSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_user_verify);
        getActionBar().setDisplayShowHomeEnabled(false);
        Button btnOk = (Button) findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(this);
        regionSelector = (Spinner) findViewById(R.id.region_selector);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.region_array,
                R.layout.black_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        regionSelector.setAdapter(spinnerAdapter);
        typeSelector = (Spinner) findViewById(R.id.type_selector);
        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.type_array, R.layout.black_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSelector.setAdapter(spinnerAdapter);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_user_verify, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                String realName = ((EditText) findViewById(R.id.real_name)).getText().toString();
                String id = ((EditText) findViewById(R.id.id)).getText().toString();
                if (realName.equals("") || id.equals("")) {
                    Toast.makeText(this, getString(R.string.verify_lack_info), Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, String> params = new HashMap<>();
                params.put("realName", realName);
                params.put("location", regionSelector.getSelectedItem().toString());
                params.put("identiType", typeSelector.getSelectedItem().toString());
                params.put("idNumber", id);
                NetworkAsyncTask task = new NetworkAsyncTask(Constants.USER_VERIFY_URL, Constants.HttpMethod.POST)
                        .setOnSucceedListener(new OnApiResponseListener())
                        .setOnFailedListener(new OnApiResponseListener())
                        .setRenderListener(new NetworkAsyncTask.OnPostRenderListener() {
                            @Override
                            public void onRender(NetworkRequest s) {
                                if (s.getApiStatus() != NetworkRequest.ApiStatus.SUCCEED) {
                                    if (s.getApiStatus() == NetworkRequest.ApiStatus.UNAUTH) {
                                        Intent intent = Util.toLoginFromAuthFail(UserVerifyActivity.this, true);
                                        UserVerifyActivity.this.startActivity(intent);
                                    } else {
                                        Toast.makeText(UserVerifyActivity.this, getString(R.string.request_failed),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    return;
                                }
                                Cookie session = ((CustomCookieStore) s.getCookieStore()).getCookie(
                                        Constants.PLAY_SESSION);
                                if (session != null) {
                                    App.setAccount(new AccountInfo(session.getValue()));
                                }
                                if (getIntent().getBooleanExtra("fromWDPage", false)) {
                                    UserVerifyActivity.this.setResult(0);
                                }
                                UserVerifyActivity.this.finish();
                            }
                        });
                task.execute(params);
                break;
        }
    }
}
