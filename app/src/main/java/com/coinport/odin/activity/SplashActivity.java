package com.coinport.odin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import com.coinport.odin.App;
import com.coinport.odin.R;
import com.coinport.odin.network.CookieDBManager;
import com.coinport.odin.obj.AccountInfo;
import com.coinport.odin.util.Constants;

import org.apache.http.cookie.Cookie;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                goHome();
            }
        }, 3000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
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

    private void goHome() {
        Intent intent;
        Cookie session = CookieDBManager.getInstance().getCookie(Constants.PLAY_SESSION);
        if (session != null) {
            App.setAccount(new AccountInfo(session.getValue()));
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
    }
}
