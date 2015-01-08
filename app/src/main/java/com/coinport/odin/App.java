package com.coinport.odin;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.coinport.odin.lock.LockPatternUtils;
import com.coinport.odin.obj.AccountInfo;

public class App extends Application {
    private static AccountInfo account = new AccountInfo();
    private static Context context;
    private static boolean mainActivityCreated = false;
    private static Activity mainActivity = null;
    private static LockPatternUtils mLockPatternUtils = null;

    private static boolean setGesturePw = false;

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
        mLockPatternUtils = new LockPatternUtils(this);
    }

    public static Context getAppContext() {
        return App.context;
    }

    public static LockPatternUtils getLockPatternUtils() {
        return mLockPatternUtils;
    }

    public static boolean isSetGesturePw() {
        SharedPreferences sp = context.getSharedPreferences("SETTING_Gesture", 0);
        return sp.getBoolean("isSet", false);
    }

    public static void setSetGesturePw(boolean setGesturePw) {
        SharedPreferences sp = context.getSharedPreferences("SETTING_Gesture", 0);
        sp.edit().putBoolean("isSet", setGesturePw).apply();
    }

}
