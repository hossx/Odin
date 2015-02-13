package com.coinport.odin.obj;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;

public class AccountInfo {
    public String username = null;
    public String nickname = null;
    public String realname = null;
    public String mobile = null;
    public boolean mobileVerified = false;
    public String securePreference = null;
    public String googleAuthSecureCode = null;
    // sp, rn, gas
    public String uid = null;

    public AccountInfo() {}

    public AccountInfo(String s) {
        // 35cdbfb5888ddc0d8c24922e8ab5edeee22a540a-COINPORT_COOKIE_REAL_NAME=&username=c%40coinport.com&COINPORT_COOKIE_MOBILE=&COINPORT_COOKIE_MOBILE_VERIFIED=false&CP_SP=01&U_RN=&uid=1000000000&CP_GAS=
        String session = s.replace("\"", "");
        String[] items = session.split("-")[1].split("&");
        for (int i = 0; i < items.length; ++i) {
            String[] kv = items[i].split("=");
            try {
                if (kv.length > 1) {
                    if (kv[0].equals("COINPORT_COOKIE_REAL_NAME"))
                        nickname = java.net.URLDecoder.decode(kv[1], "UTF-8");
                    else if (kv[0].equals("username"))
                        username = java.net.URLDecoder.decode(kv[1], "UTF-8");
                    else if (kv[0].equals("COINPORT_COOKIE_MOBILE"))
                        mobile = java.net.URLDecoder.decode(kv[1], "UTF-8");
                    else if (kv[0].equals("COINPORT_COOKIE_MOBILE_VERIFIED"))
                        mobileVerified = Boolean.parseBoolean(kv[1]);
                    else if (kv[0].equals("U_RN"))
                        realname = java.net.URLDecoder.decode(kv[1], "UTF-8");
                    else if (kv[0].equals("uid"))
                        uid = java.net.URLDecoder.decode(kv[1], "UTF-8");
                    else if (kv[0].equals("CP_SP"))
                        securePreference = java.net.URLDecoder.decode(kv[1], "UTF-8");
                    else if (kv[0].equals("CP_GAS"))
                        googleAuthSecureCode = java.net.URLDecoder.decode(kv[1], "UTF-8");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean needSms() {
        return (securePreference != null && securePreference.charAt(0) == '1');
    }
    
    public boolean needEmail() {
        return (securePreference != null && securePreference.charAt(1) == '1');
    }

    public boolean needGoogleAuth() {
        return (googleAuthSecureCode != null && !googleAuthSecureCode.equals(""));
    }
}
