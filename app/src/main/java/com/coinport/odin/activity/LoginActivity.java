package com.coinport.odin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.coinport.odin.R;
import com.coinport.odin.util.Constants;
import com.coinport.odin.network.NetworkRequest;
import com.coinport.odin.util.Util;

import org.apache.http.protocol.HTTP;

import java.util.HashMap;
import java.util.Map;

/**登陆界面activity*/
public class LoginActivity extends Activity implements OnClickListener {
	private Button btn_login_regist;//注册按钮
    private Button btn_login;
    private TextView forgotPw;

	public static final int MENU_PWD_BACK = 1;
	public static final int MENU_HELP = 2;
	public static final int MENU_EXIT = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		
		initView();
	}
	
	private void initView(){
		btn_login_regist = (Button) findViewById(R.id.btn_login_regist);
		btn_login_regist.setOnClickListener(this);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        forgotPw = (TextView) findViewById(R.id.forgot_pw);
        forgotPw.setOnClickListener(this);
	}
	
	
	@Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent intent = null;
        final Activity self = this;
        switch (v.getId()) {
            case R.id.btn_login_regist:
//                intent = new Intent(LoginActivity.this, RegisterActivity.class);
//                startActivity(intent);
                break;
            case R.id.btn_login:
                String username = ((EditText) (self.findViewById(R.id.user_name))).getText().toString();
                String pw = ((EditText) (self.findViewById(R.id.password))).getText().toString();
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", Util.sha256base64(pw));
                new LoginTask().execute(params);
//                intent = new Intent(LoginActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
                break;
            case R.id.forgot_pw:
                intent = new Intent(LoginActivity.this, ResetPwActivity.class);
                startActivity(intent);
                break;

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

    private class LoginTask extends AsyncTask<Map<String, String>, Void, String> {

        @Override
        protected String doInBackground(final Map<String, String>... params) {
            final Map<String, String> p = params[0];
            NetworkRequest post = new NetworkRequest();
            try {
                return post.setCharset(HTTP.UTF_8).setConnectionTimeout(5000).setSoTimeout(10000).setOnHttpRequestListener(
                        new NetworkRequest.OnHttpRequestListener() {
                            @Override
                            public void onRequest(NetworkRequest request) throws Exception {
                                request.addRequestParameters(p);
                            }

                            @Override
                            public String onSucceed(int statusCode, NetworkRequest request) throws Exception {
                                request.getHttpResponse().getHeaders("Cookie");
                                return request.getInputStream();
                            }

                            @Override
                            public String onFailed(int statusCode, NetworkRequest request) throws Exception {
                                System.out.println(statusCode);
                                return null;
                            }
                        }
                ).post(Constants.loginUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s == null)
                Log.d("login activity:", "error");
            else
                Log.d("login activity:", s);
        }
    }
}
