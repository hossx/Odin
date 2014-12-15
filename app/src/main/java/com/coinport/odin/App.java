package com.coinport.odin;

import android.app.Application;
import android.content.Context;

import com.coinport.odin.obj.AccountInfo;

public class App extends Application {
    private static AccountInfo account = null;
    private static Context context;

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
