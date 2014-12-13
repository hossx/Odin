package com.coinport.odin.network;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class CustomCookieStore extends BasicCookieStore {
    public CustomCookieStore() {
        super();
        try {
            List<Cookie> cookies = CookieDBManager.getInstance().getAllCookies();
//            CookieDBManager.getInstance().clear();
            Cookie[] cookiesArr = new Cookie[cookies.size()];
            for (int i = cookies.size() - 1; i >= 0; i--) {
                cookiesArr[i] = cookies.get(i);
            }
            addCookies(cookiesArr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void addCookie(Cookie cookie) {
        super.addCookie(cookie);
        try {
            CookieDBManager.getInstance().saveCookie(cookie);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void addCookies(Cookie[] cookies) {
        super.addCookies(cookies);
        try {
            CookieDBManager.getInstance().saveCookies(cookies);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void clear() {
        super.clear();
        try {
            CookieDBManager.getInstance().clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized boolean clearExpired(Date date) {
        try {
            CookieDBManager.getInstance().clearExpired();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.clearExpired(date);
    }

    @Override
    public synchronized List<Cookie> getCookies() {
        return super.getCookies();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public Cookie getCookie(String name) {
        Iterator it = getCookies().iterator();
        while (it.hasNext()) {
            Cookie cookie = (Cookie) it.next();
            if (cookie.getName().equals(name)) {
                return cookie;
            }
        }
        return null;
    }
}
