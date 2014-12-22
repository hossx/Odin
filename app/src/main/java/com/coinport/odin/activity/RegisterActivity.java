package com.coinport.odin.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.coinport.odin.R;
import com.coinport.odin.network.NetworkAsyncTask;
import com.coinport.odin.network.NetworkRequest;
import com.coinport.odin.network.OnApiResponseListener;
import com.coinport.odin.util.Constants;
import com.coinport.odin.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends Activity implements View.OnClickListener {
    private static final String BASE64 = "base64,";
    private String uuid = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_register);

        TextView terms = (TextView) findViewById(R.id.register_terms);
        PackageManager pm = getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.TERMS_RUL));
        if (testIntent.resolveActivity(pm) != null) {
            terms.setText(Html.fromHtml("<a href=\"" + Constants.TERMS_RUL + "\">" + getString(R.string.register_terms) + "</a>"));
            terms.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            terms.setText(getString(R.string.register_terms) + "(" + Constants.TERMS_RUL + ")");
        }
        Button okBtn = (Button) findViewById(R.id.btn_ok);
        okBtn.setOnClickListener(this);
        Button cancelBtn = (Button) findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        NetworkAsyncTask task = new NetworkAsyncTask(Constants.CAPTCHA_URL, Constants.HttpMethod.GET)
//                .setOnSucceedListener(new OnApiResponseListener())
//                .setOnFailedListener(new OnApiResponseListener())
//                .setRenderListener(new NetworkAsyncTask.OnPostRenderListener() {
//                    @Override
//                    public void onRender(NetworkRequest s) {
//                        if (s.getApiStatus() != NetworkRequest.ApiStatus.SUCCEED)
//                            return;
//                        JSONObject obj = Util.getJsonObjectByPath(s.getApiResult(), "data");
//                        try {
//                            uuid = obj.getString("uuid");
//                            String imageStr = obj.getString("imageSrc");
//                            imageStr = imageStr.substring(imageStr.indexOf(BASE64) + BASE64.length());
//                            byte[] imageBytes = Base64.decode(imageStr, Base64.DEFAULT);
//                            Bitmap bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
//                            ImageView iv = (ImageView) findViewById(R.id.captcha);
//                            iv.setImageBitmap(bmp);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//        task.execute();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_register, menu);
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
                String email = ((EditText) findViewById(R.id.register_email)).getText().toString();
                String pw = ((EditText) findViewById(R.id.register_pw)).getText().toString();
                String pwConfirm = ((EditText) findViewById(R.id.register_confirm_pw)).getText().toString();
//                String code = ((EditText) findViewById(R.id.register_code)).getText().toString();
                CheckBox cb = (CheckBox) findViewById(R.id.agree_terms);
                if (email.equals("") || pw.equals("")) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.register_null_fail),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!pw.equals(pwConfirm)) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.change_pw_check_fail),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!cb.isChecked()) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.register_agree_fail),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, String> params = new HashMap<>();
                params.put("uuid", uuid);
//                params.put("text", code);
                params.put("email", email);
                params.put("password", Util.sha256base64(pw));
                NetworkAsyncTask task = new NetworkAsyncTask(Constants.REGISTER_URL, Constants.HttpMethod.POST)
                        .setOnSucceedListener(new OnApiResponseListener())
                        .setOnFailedListener(new OnApiResponseListener())
                        .setRenderListener(new NetworkAsyncTask.OnPostRenderListener() {
                            @Override
                            public void onRender(NetworkRequest s) {
                                if (s.getApiStatus() != NetworkRequest.ApiStatus.SUCCEED) {
                                    if (s.getApiStatus() == NetworkRequest.ApiStatus.INTERNAL_ERROR)
                                        Toast.makeText(RegisterActivity.this, s.getApiMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(RegisterActivity.this, getString(R.string.request_failed),
                                                Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Toast.makeText(RegisterActivity.this, getString(R.string.register_email_sent),
                                        Toast.LENGTH_LONG).show();
                                finish();
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
