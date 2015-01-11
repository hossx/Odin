package com.coinport.odin.network;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGetHC4;
import org.apache.http.client.methods.HttpPostHC4;
import org.apache.http.client.methods.HttpRequestBaseHC4;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NetworkRequest {
    public static final String HTTP_GET = "GET";
    public static final String HTTP_POST = "POST";

    private HttpClientContext context = HttpClientContext.create();
    private String result = null;

    private String uri = null;
    private String type = HTTP_GET;
    private List<NameValuePair> requestParams = new ArrayList<>();
    private OnHttpRequestListener onHttpRequestListener = null;

    private int socketTimeout = 5000;
    private int connectTimeout = 5000;
    private int connectionRequestTimeout = 10000;

    private ApiStatus apiStatus = null;
    private JSONObject apiResult = null;
    private String apiMessage = null;

    public NetworkRequest(String uri, String type) {
        this.uri = uri;
        this.type = type;
    }

    public void addRequestParameters(Map<String, String> params) {
        Set<String> keySet = params.keySet();
        for(String key : keySet) {
            requestParams.add(new BasicNameValuePair(key, params.get(key)));
        }
    }

    public NetworkRequest setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
        return this;
    }

    public NetworkRequest setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public NetworkRequest setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
        return this;
    }

    public String getResult() {
        return result;
    }

    public CustomCookieStore getCookieStore() {
        return (CustomCookieStore) context.getCookieStore();
    }

    public void execute() throws Exception {
        HttpRequestBaseHC4 request;
        if (isPost()) {
            request = new HttpPostHC4(uri);
            if (onHttpRequestListener != null)
                onHttpRequestListener.onRequest(this);
            if (!requestParams.isEmpty()) {
                try {
                    ((HttpPostHC4) request).setEntity(new UrlEncodedFormEntity(requestParams, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (onHttpRequestListener != null)
                onHttpRequestListener.onRequest(this);
            if (!requestParams.isEmpty()) {
                try {
                    URI oldUri = new URI(uri);
                    String query = "";
                    if (oldUri.getQuery() != null && !oldUri.getQuery().equals(""))
                        query += oldUri.getQuery() + "&";
                    query += URLEncodedUtils.format(requestParams, "UTF-8");
                    URI newUri = URIUtils.createURI(oldUri.getScheme(), oldUri.getHost(), oldUri.getPort(),
                            oldUri.getPath(), query, null);
                    uri = newUri.toString();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
            request = new HttpGetHC4(uri);
        }

        RequestConfig rc = RequestConfig.custom()
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .build();
        request.setConfig(rc);

        try {
            CloseableHttpResponse response = CpHttpClient.getHttpClient().execute(request, context);
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                response.getEntity().writeTo(baos);
                // 将数据转换为字符串保存
                String respCharset = EntityUtils.getContentCharSet(response.getEntity());
                if (respCharset != null)
                    result = new String(baos.toByteArray(), respCharset);
                else
                    result = new String(baos.toByteArray(), "UTF-8");
                if (onHttpRequestListener != null) {
                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == HttpStatus.SC_OK)
                        onHttpRequestListener.onSucceed(statusCode, this);
                    else
                        onHttpRequestListener.onFailed(statusCode, this);
                }
            } finally {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            CpHttpClient.shutDown();
        }
    }

    public boolean isGet()
    {
        return type.equals(HTTP_GET);
    }

    public boolean isPost()
    {
        return type.equals(HTTP_POST);
    }

    public NetworkRequest setOnHttpRequestListener(OnHttpRequestListener listener)
    {
        this.onHttpRequestListener = listener;
        return this;
    }

    public interface OnHttpRequestListener {
        /**
         * 初始化 HTTP GET 或 POST 请求之前的 header 信息配置 或 其他数据配置等操作
         */
        public void onRequest(NetworkRequest request) throws Exception;

        /**
         * 当 HTTP 请求响应成功时的回调方法
         */
        public void onSucceed(int statusCode, NetworkRequest request) throws Exception;

        /**
         * 当 HTTP 请求响应失败时的回调方法
         */
        public void onFailed(int statusCode, NetworkRequest request) throws Exception;
    }

    public enum ApiStatus {
        SUCCEED, UNAUTH, INTERNAL_ERROR, NETWORK_ERROR, BAD_FORMAT
    }

    public String getApiMessage() {
        return apiMessage;
    }

    public NetworkRequest setApiMessage(String apiMessage) {
        this.apiMessage = apiMessage;
        return this;
    }

    public JSONObject getApiResult() {
        return apiResult;
    }

    public NetworkRequest setApiResult(JSONObject apiResult) {
        this.apiResult = apiResult;
        return this;
    }

    public ApiStatus getApiStatus() {
        return apiStatus;
    }

    public NetworkRequest setApiStatus(ApiStatus status) {
        this.apiStatus = status;
        return this;
    }
}
