package com.coinport.odin.network;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class NetworkRequest {
    public final String HTTP_GET = "GET";
    public final String HTTP_POST = "POST";

    protected String url = "";
    protected String requsetType = HTTP_GET;
    protected int connectionTimeout = 5000;
    protected int soTimeout = 10000;
    protected int statusCode = -1;
    protected String charset = HTTP.UTF_8;
    protected HttpRequestBase httpRequest = null;
    protected HttpParams httpParameters = null;
    protected List<NameValuePair> requestParams = new ArrayList<>();
    protected HttpResponse httpResponse = null;
    protected AbstractHttpClient httpClient = null;
    protected MultipartEntityBuilder multipartEntityBuilder = null;
    protected OnHttpRequestListener onHttpRequestListener = null;

    public NetworkRequest(){}

    public NetworkRequest(OnHttpRequestListener listener) {
        this.setOnHttpRequestListener(listener);
    }

    public NetworkRequest setUrl(String url)
    {
        this.url = url;
        return this;
    }

    public NetworkRequest setConnectionTimeout(int timeout)
    {
        this.connectionTimeout = timeout;
        return this;
    }

    public NetworkRequest setSoTimeout(int timeout)
    {
        this.soTimeout = timeout;
        return this;
    }

    public NetworkRequest setCharset(String charset)
    {
        this.charset = charset;
        return this;
    }

    public String getRequestType()
    {
        return this.requsetType;
    }

    public boolean isGet()
    {
        return this.requsetType.equals(HTTP_GET);
    }

    public boolean isPost()
    {
        return this.requsetType.equals(HTTP_POST);
    }

    public HttpResponse getHttpResponse()
    {
        return this.httpResponse;
    }

    public HttpClient getHttpClient()
    {
        return this.httpClient;
    }

    public NetworkRequest addHeader(String name, String value)
    {
        this.httpRequest.addHeader(name, value);
        return this;
    }

    public HttpGet getHttpGet()
    {
        return (HttpGet) this.httpRequest;
    }

    public HttpPost getHttpPost()
    {
        return (HttpPost) this.httpRequest;
    }

    public int getStatusCode()
    {
        return this.statusCode;
    }

    public String get(String url) throws Exception
    {
        this.requsetType = HTTP_GET;
        // 设置当前请求的链接
        this.setUrl(url);
        // 新建 HTTP GET 请求
        this.httpRequest = new HttpGet(this.url);
        // 执行客户端请求
        this.httpClientExecute();
        // 监听服务端响应事件并返回服务端内容
        return this.checkStatus();
    }

    public MultipartEntityBuilder getMultipartEntityBuilder()
    {
        if (this.multipartEntityBuilder == null) {
            this.multipartEntityBuilder = MultipartEntityBuilder.create();
            // 设置为浏览器兼容模式
            multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            // 设置请求的编码格式
            multipartEntityBuilder.setCharset(Charset.forName(this.charset));
        }
        return this.multipartEntityBuilder;
    }

    public void buildPostEntity()
    {
        // 生成 HTTP POST 实体
        HttpEntity httpEntity = this.multipartEntityBuilder.build();
        this.getHttpPost().setEntity(httpEntity);
    }

    public void addRequestParameters(Map<String, String> params) {
        Set<String> keySet = params.keySet();
        for(String key : keySet) {
            requestParams.add(new BasicNameValuePair(key, params.get(key)));
        }
    }

    public String post(String url) throws Exception
    {
        this.requsetType = HTTP_POST;
        // 设置当前请求的链接
        this.setUrl(url);
        // 新建 HTTP POST 请求
        this.httpRequest = new HttpPost(this.url);
        // 执行客户端请求
        this.httpClientExecute();
        // 监听服务端响应事件并返回服务端内容
        return this.checkStatus();
    }

    protected void httpClientExecute() throws Exception
    {
        // 配置 HTTP 请求参数
        this.httpParameters = new BasicHttpParams();
        this.httpParameters.setParameter("charset", this.charset);
        // 设置 连接请求超时时间
        HttpConnectionParams.setConnectionTimeout(this.httpParameters, this.connectionTimeout);
        // 设置 socket 读取超时时间
        HttpConnectionParams.setSoTimeout(this.httpParameters, this.soTimeout);
        // 开启一个客户端 HTTP 请求
        this.httpClient = new DefaultHttpClient(this.httpParameters);
        this.httpClient.setCookieStore(new CustomCookieStore());
        // 启动 HTTP POST 请求执行前的事件监听回调操作(如: 自定义提交的数据字段或上传的文件等)
        this.getOnHttpRequestListener().onRequest(this);

        if (!requestParams.isEmpty()) {
            if (isPost()) {
                ((HttpPost) httpRequest).setEntity(new UrlEncodedFormEntity(requestParams));
            } else {
                URI oldUri = httpRequest.getURI();
                String query = "";
                if (oldUri.getQuery() != null && !oldUri.getQuery().equals(""))
                    query += oldUri.getQuery() + "&";
                query += URLEncodedUtils.format(requestParams, "UTF-8");
                URI newUri = URIUtils.createURI(oldUri.getScheme(), oldUri.getHost(), oldUri.getPort(),
                    oldUri.getPath(), query, null);
                this.httpRequest.setURI(newUri);
            }
        }
        // 发送 HTTP 请求并获取服务端响应状态
        this.httpResponse = this.httpClient.execute(this.httpRequest);
        // 获取请求返回的状态码
        this.statusCode = this.httpResponse.getStatusLine().getStatusCode();
    }

    public String getInputStream() throws Exception
    {
        // 接收远程输入流
        InputStream inStream = this.httpResponse.getEntity().getContent();
        // 分段读取输入流数据
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len;
        while ((len = inStream.read(buf)) != -1) {
            baos.write(buf, 0, len);
        }
        // 数据接收完毕退出
        inStream.close();
        // 将数据转换为字符串保存
        return new String(baos.toByteArray(), this.charset);
    }

    protected void shutdownHttpClient()
    {
        if (this.httpClient != null && this.httpClient.getConnectionManager() != null) {
            this.httpClient.getConnectionManager().shutdown();
        }
    }

    protected String checkStatus() throws Exception
    {
        OnHttpRequestListener listener = this.getOnHttpRequestListener();
        String content;
        if (this.statusCode == HttpStatus.SC_OK) {
            // 请求成功, 回调监听事件
            content = listener.onSucceed(this.statusCode, this);
        } else {
            // 请求失败或其他, 回调监听事件
            content = listener.onFailed(this.statusCode, this);
        }
        // 关闭连接管理器释放资源
        this.shutdownHttpClient();
        return content;
    }

    public interface OnHttpRequestListener
    {
        /**
         * 初始化 HTTP GET 或 POST 请求之前的 header 信息配置 或 其他数据配置等操作
         */
        public void onRequest(NetworkRequest request) throws Exception;

        /**
         * 当 HTTP 请求响应成功时的回调方法
         */
        public String onSucceed(int statusCode, NetworkRequest request) throws Exception;

        /**
         * 当 HTTP 请求响应失败时的回调方法
         */
        public String onFailed(int statusCode, NetworkRequest request) throws Exception;
    }

    public NetworkRequest setOnHttpRequestListener(OnHttpRequestListener listener)
    {
        this.onHttpRequestListener = listener;
        return this;
    }

    public OnHttpRequestListener getOnHttpRequestListener()
    {
        return this.onHttpRequestListener;
    }
}
