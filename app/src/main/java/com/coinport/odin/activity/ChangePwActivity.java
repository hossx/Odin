package com.coinport.odin.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.coinport.odin.App;
import com.coinport.odin.R;
import com.coinport.odin.network.NetworkAsyncTask;
import com.coinport.odin.network.NetworkRequest;
import com.coinport.odin.network.OnApiResponseListener;
import com.coinport.odin.util.Constants;
import com.coinport.odin.util.Util;

import java.util.HashMap;
import java.util.Map;

public class ChangePwActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_change_pw);
        Button okBtn = (Button) findViewById(R.id.btn_ok);
        okBtn.setOnClickListener(this);
        Button cancelBtn = (Button) findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(this);
        getActionBar().setDisplayShowHomeEnabled(false);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_change_pw, menu);
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
                String oldPwd = ((TextView) findViewById(R.id.oldPw)).getText().toString();
                String newPwd = ((TextView) findViewById(R.id.newPw)).getText().toString();
                String confirmNewePwd = ((TextView) findViewById(R.id.confirmNewPw)).getText().toString();
                if (oldPwd.equals("") || newPwd.equals("")) {
                    Toast.makeText(ChangePwActivity.this, getString(R.string.change_pw_null_fail),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!newPwd.equals(confirmNewePwd)) {
                    Toast.makeText(ChangePwActivity.this, getString(R.string.change_pw_check_fail),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, String> params = new HashMap<>();
                params.put("oldPassword", Util.sha256base64(oldPwd));
                params.put("newPassword", Util.sha256base64(newPwd));
                NetworkAsyncTask task = new NetworkAsyncTask(Constants.CHANGE_PW_URL, Constants.HttpMethod.POST)
                        .setOnSucceedListener(new OnApiResponseListener())
                        .setOnFailedListener(new OnApiResponseListener())
                        .setRenderListener(new NetworkAsyncTask.OnPostRenderListener() {
                            @Override
                            public void onRender(NetworkRequest s) {
                                if (s.getApiStatus() != NetworkRequest.ApiStatus.SUCCEED) {
                                    if (s.getApiStatus() == NetworkRequest.ApiStatus.INTERNAL_ERROR) {
                                        Toast.makeText(ChangePwActivity.this, App.getErrorMessage(s.getApiCode()),
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ChangePwActivity.this, getString(R.string.request_failed),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(ChangePwActivity.this, getString(R.string.change_pw_succeed),
                                            Toast.LENGTH_SHORT).show();
                                    ChangePwActivity.this.finish();
                                }
                            }
                        });
                task.execute(params);
                break;
            case R.id.btn_cancel:
                finish();
                break;
        }
    }
}
