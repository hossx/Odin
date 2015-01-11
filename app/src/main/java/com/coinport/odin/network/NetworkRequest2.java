package com.coinport.odin.network;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
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

public class NetworkRequest2 {
    public static final String HTTP_GET = "GET";
    public static final String HTTP_POST = "POST";

    private HttpClientContext context = HttpClientContext.create();
    private String result = null;

    private String uri = null;
    private String type = HTTP_GET;
    private List<NameValuePair> requestParams = new ArrayList<>();
    protected OnHttpRequestListener onHttpRequestListener = null;

    private ApiStatus apiStatus = null;
    private JSONObject apiResult = null;
    private String apiMessage = null;

    public NetworkRequest2(String uri, String type) {
        this.uri = uri;
        this.type = type;
    }

    public void addRequestParameters(Map<String, String> params) {
        Set<String> keySet = params.keySet();
        for(String key : keySet) {
            requestParams.add(new BasicNameValuePair(key, params.get(key)));
        }
    }

    public String getResult() {
        return result;
    }

    public void execute() throws Exception {
        HttpRequestBase request;
        if (isPost()) {
            request = new HttpPost(uri);
            if (onHttpRequestListener != null)
                onHttpRequestListener.onRequest(this);
            if (!requestParams.isEmpty()) {
                try {
                    ((HttpPost) request).setEntity(new UrlEncodedFormEntity(requestParams, "UTF-8"));
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
            request = new HttpGet(uri);
        }

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

    public NetworkRequest2 setOnHttpRequestListener(OnHttpRequestListener listener)
    {
        this.onHttpRequestListener = listener;
        return this;
    }

    public interface OnHttpRequestListener {
        /**
         * 初始化 HTTP GET 或 POST 请求之前的 header 信息配置 或 其他数据配置等操作
         */
        public void onRequest(NetworkRequest2 request) throws Exception;

        /**
         * 当 HTTP 请求响应成功时的回调方法
         */
        public void onSucceed(int statusCode, NetworkRequest2 request) throws Exception;

        /**
         * 当 HTTP 请求响应失败时的回调方法
         */
        public void onFailed(int statusCode, NetworkRequest2 request) throws Exception;
    }

    public enum ApiStatus {
        SUCCEED, UNAUTH, INTERNAL_ERROR, NETWORK_ERROR, BAD_FORMAT
    }

    public String getApiMessage() {
        return apiMessage;
    }

    public NetworkRequest2 setApiMessage(String apiMessage) {
        this.apiMessage = apiMessage;
        return this;
    }

    public JSONObject getApiResult() {
        return apiResult;
    }

    public NetworkRequest2 setApiResult(JSONObject apiResult) {
        this.apiResult = apiResult;
        return this;
    }

    public ApiStatus getApiStatus() {
        return apiStatus;
    }

    public NetworkRequest2 setApiStatus(ApiStatus status) {
        this.apiStatus = status;
        return this;
    }
}
