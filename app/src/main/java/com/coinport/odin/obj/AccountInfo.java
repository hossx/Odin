package com.coinport.odin.obj;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;

/**
 * Created by hoss on 14-12-15.
 */
public class AccountInfo {
    public String username = null;
    public String nickname = null;
    public String realname = null;
    public String mobile = null;
    public boolean mobileVerified = false;
    // sp, rn, gas
    public String uid = null;

    public AccountInfo() {}

    public AccountInfo(String session) {
        // 35cdbfb5888ddc0d8c24922e8ab5edeee22a540a-COINPORT_COOKIE_REAL_NAME=&username=c%40coinport.com&COINPORT_COOKIE_MOBILE=&COINPORT_COOKIE_MOBILE_VERIFIED=false&CP_SP=01&U_RN=&uid=1000000000&CP_GAS=
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
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}
