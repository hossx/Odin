package com.coinport.odin;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import com.coinport.odin.activity.UnlockGesturePasswordActivity;
import com.coinport.odin.lock.LockPatternUtils;
import com.coinport.odin.obj.AccountInfo;
import com.coinport.odin.util.CrashHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App extends Application {
    private static AccountInfo account = new AccountInfo();
    private static Context context;
    private static boolean mainActivityCreated = false;
    private static Activity mainActivity = null;
    private static LockPatternUtils mLockPatternUtils = null;
    private static Map<Integer, String> errorMessage = new HashMap<>();
    private static Typeface tf = null;

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

    public void onCreate() {
        super.onCreate();
        App.context = getApplicationContext();
        mLockPatternUtils = new LockPatternUtils(this);
        initErrorMessage(App.context);
        this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            private boolean isActive = true;

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                if (!isActive && isSetGesturePw()) {
                    isActive = true;
                    Intent intent = new Intent(activity, UnlockGesturePasswordActivity.class);
                    activity.startActivity(intent);
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                if (!isAppOnForeground()) {
                    isActive = false;
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }

            private boolean isAppOnForeground() {
                ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
                String packageName = getApplicationContext().getPackageName();

                List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
                if (appProcesses == null)
                    return false;

                for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                    // The name of the process that this object is associated with.
                    if (appProcess.processName.equals(packageName)
                            && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        return true;
                    }
                }

                return false;
            }
        });

        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(App.context);

        tf = Typeface.createFromAsset(App.context.getAssets(), "coinport.ttf");
    }

    public static Typeface getIconTf() {
        return tf;
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

    public static String getErrorMessage(int code) {
        if (errorMessage.containsKey(code))
            return errorMessage.get(code);
        else
            return context.getString(R.string.general_error);
    }

    private void initErrorMessage(Context context) {
        String[] stringArray = context.getResources().getStringArray(R.array.be_message_array);
        errorMessage.clear();
        for (String entry : stringArray) {
            String[] splitResult = entry.split("-", 2);
            errorMessage.put(Integer.valueOf(splitResult[0]), splitResult[1]);
        }
    }
}
