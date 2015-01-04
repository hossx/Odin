package com.coinport.odin;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.coinport.odin.obj.AccountInfo;

public class App extends Application {
    private static AccountInfo account = new AccountInfo();
    private static Context context;
    private static boolean mainActivityCreated = false;
    private static Activity mainActivity = null;

    public static boolean isMainActivityCreated() {
        return mainActivityCreated;
    }

    public static void setMainActivity(Activity mainActivity) {
        App.mainActivityCreated = true;
        App.mainActivity = mainActivity;
    }

    public static void destoryMainActivity() {
        App.mainActivityCreated = false;
        if (App.mainActivity != null)
            App.mainActivity.finish();
    }

    public static AccountInfo getAccount() {
        return account;
    }

    public static void setAccount(AccountInfo account) {
        App.account = account;
    }

    public void onCreate(){
        super.onCreate();
        App.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return App.context;
    }
}
