package com.coinport.odin.network;

import org.apache.http.Consts;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.nio.charset.CodingErrorAction;

public class CpHttpClient {
    private static final int CONNECT_TIME_OUT = 5000;
    private static final int SOCKET_TIME_OUT = 5000;
    private static final int CONNECTION_REQUEST_TIMEOUT = 20000;

    private static CloseableHttpClient client = null;

    public static synchronized CloseableHttpClient getHttpClient() {
        if (client != null)
            return client;

        MessageConstraints mc = MessageConstraints.custom()
                .setMaxHeaderCount(200)
                .setMaxLineLength(2000)
                .build();


        ConnectionConfig cc = ConnectionConfig.custom()
                .setMalformedInputAction(CodingErrorAction.IGNORE)
                .setUnmappableInputAction(CodingErrorAction.IGNORE)
                .setCharset(Consts.UTF_8)
                .setMessageConstraints(mc)
                .build();

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setDefaultConnectionConfig(cc);

        RequestConfig rc = RequestConfig.custom()
                .setSocketTimeout(SOCKET_TIME_OUT)
                .setConnectTimeout(CONNECT_TIME_OUT)
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                .build();

        CloseableHttpClient c = HttpClients.custom()
                .setConnectionManager(cm)
                .setDefaultCookieStore(new CustomCookieStore())
                .setDefaultRequestConfig(rc)
                .addInterceptorLast(new HttpRequestInterceptor() {
                    @Override
                    public void process(HttpRequest httpRequest,
                        HttpContext httpContext) throws HttpException, IOException {
                        CustomCookieStore ccs = (CustomCookieStore) HttpClientContext.adapt(httpContext).getCookieStore();
                        Cookie cookie = ccs.getCookie("XSRF-TOKEN");
                        if (cookie != null) {
                            String token = cookie.getValue();
                            httpRequest.addHeader("X-XSRF-TOKEN", token);
                            httpRequest.addHeader(CoreProtocolPNames.USER_AGENT, System.getProperty("http.agent"));
                        }
                    }
                })
                .build();
        return c;
    }

    public static void shutDown() {
        if (client == null)
            return;
        CloseableHttpClient chc = client;
        client = null;
        try {
            chc.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
