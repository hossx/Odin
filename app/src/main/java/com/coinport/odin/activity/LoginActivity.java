package com.coinport.odin.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.coinport.odin.App;
import com.coinport.odin.R;
import com.coinport.odin.dialog.CustomProgressDialog;
import com.coinport.odin.network.CustomCookieStore;
import com.coinport.odin.network.NetworkAsyncTask;
import com.coinport.odin.network.NetworkRequest;
import com.coinport.odin.network.OnApiResponseListener;
import com.coinport.odin.obj.AccountInfo;
import com.coinport.odin.util.Constants;
import com.coinport.odin.util.Util;

import org.apache.http.cookie.Cookie;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

/**登陆界面activity*/
public class LoginActivity extends Activity implements OnClickListener {
    private CheckBox rememberPw;
    private SharedPreferences settings;
//    private TextView forgotPw;

	public static final int MENU_PWD_BACK = 1;
	public static final int MENU_HELP = 2;
	public static final int MENU_EXIT = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
        EditText usernameEt = (EditText) findViewById(R.id.user_name);
        EditText passwordEt = (EditText) findViewById(R.id.password);
        rememberPw = (CheckBox) findViewById(R.id.remember_pw);

        settings = getSharedPreferences("SETTING_Infos", 0);
        String strJudge = settings.getString("judgeText", "no");
        String strUserName = settings.getString("userNameText", "");
        String strPassword = settings.getString("passwordText", "");
        if (strJudge.equals("yes")) {
            rememberPw.setChecked(true);
            usernameEt.setText(strUserName);
            passwordEt.setText(strPassword);
        } else {
            rememberPw.setChecked(false);
            usernameEt.setText("");
            passwordEt.setText("");
        }

		initView();
	}
	
	private void initView(){
        Button btn_login_regist = (Button) findViewById(R.id.btn_login_regist);
		btn_login_regist.setOnClickListener(this);
        Button btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
//        forgotPw = (TextView) findViewById(R.id.forgot_pw);
//        forgotPw.setOnClickListener(this);
	}
	
	
	@Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent intent;
        final Activity self = this;
        switch (v.getId()) {
            case R.id.btn_login_regist:
                intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_login:
                final CustomProgressDialog cpd = CustomProgressDialog.createDialog(this);
                cpd.setCancelable(false);
                cpd.show();
                final TextView tv = (TextView) LoginActivity.this.findViewById(R.id.login_fail_message);
                tv.setVisibility(View.GONE);
                final String username = ((EditText) (self.findViewById(R.id.user_name))).getText().toString();
                final String pw = ((EditText) (self.findViewById(R.id.password))).getText().toString();
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", Util.sha256base64(pw));
                NetworkAsyncTask task = new NetworkAsyncTask(Constants.LOGIN_URL, Constants.HttpMethod.POST)
                    .setOnSucceedListener(new OnApiResponseListener())
                    .setOnFailedListener(new OnApiResponseListener())
                    .setRenderListener(new NetworkAsyncTask.OnPostRenderListener() {
                        @Override
                        public void onRender(NetworkRequest s) {
                            cpd.dismiss();
                            if (s.getApiStatus() != null && s.getApiStatus() == NetworkRequest.ApiStatus.SUCCEED) {
                                Cookie session = ((CustomCookieStore) s.getHttpClient().getCookieStore()).getCookie(
                                    Constants.PLAY_SESSION);
                                if (session != null) {
                                    App.setAccount(new AccountInfo(session.getValue()));
                                }
                                if (App.isMainActivityCreated()) {
                                    App.destoryMainActivity();
                                }
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                if (rememberPw.isChecked()) {
                                    settings.edit().putString("judgeText", "yes")
                                            .putString("userNameText", username)
                                            .putString("passwordText", pw)
                                            .apply();
                                } else {
                                    settings.edit().putString("judgeText", "no")
                                            .putString("userNameText", "")
                                            .putString("passwordText", "")
                                            .apply();
                                }
                                finish();
                            } else if (s.getApiStatus() == NetworkRequest.ApiStatus.INTERNAL_ERROR) {
                                tv.setVisibility(View.VISIBLE);
                                try {
                                    String times = s.getApiResult().getString("data");
                                    tv.setText(String.format(getString(R.string.login_fail), times));
                                } catch (JSONException e) {
                                    tv.setText(R.string.request_failed);
                                    e.printStackTrace();
                                }
                            } else {
                                tv.setVisibility(View.VISIBLE);
                                tv.setText(R.string.request_failed);
                            }
                        }
                    });
                task.execute(params);
                break;
//            case R.id.forgot_pw:
//                intent = new Intent(LoginActivity.this, ResetPwActivity.class);
//                startActivity(intent);
//                break;

        }
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {//创建系统功能菜单
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case MENU_PWD_BACK:
			break;
		case MENU_HELP:
			break;
		case MENU_EXIT:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
