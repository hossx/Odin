package com.coinport.odin.network;

import android.os.AsyncTask;

import com.coinport.odin.util.Constants.HttpMethod;

import org.apache.http.protocol.HTTP;

import java.util.Map;

/**
 * Created by hoss on 14-12-13.
 */
public class NetworkAsyncTask extends AsyncTask<Map<String, String>, Void, String> {
    private String url;
    private HttpMethod method;

    private OnHttpPrepareRequestListener prepareRequestListener = null;
    private OnHttpResponseListener onSucceedListener = null;
    private OnHttpResponseListener onFailedListener = null;
    private OnPostRenderListener renderListener = null;

    public NetworkAsyncTask(String url, HttpMethod method) {
        this.url = url;
        this.method = method;
    }

    @Override
    protected String doInBackground(final Map<String, String>... params) {
        NetworkRequest request = new NetworkRequest();
        request.setCharset(HTTP.UTF_8).setConnectionTimeout(5000).setSoTimeout(10000).setOnHttpRequestListener(
            new NetworkRequest.OnHttpRequestListener() {
                @Override
                public void onRequest(NetworkRequest request) throws Exception {
                    if (params != null && params.length > 0)
                        request.addRequestParameters(params[0]);
                    if (prepareRequestListener != null)
                        prepareRequestListener.onRequest(request);
                }

                @Override
                public String onSucceed(int statusCode, NetworkRequest request) throws Exception {
                    if (onSucceedListener != null)
                        return onSucceedListener.onResponse(statusCode, request);
                    else
                        return request.getInputStream();
                }

                @Override
                public String onFailed(int statusCode, NetworkRequest request) throws Exception {
                    if (onFailedListener != null)
                        return onFailedListener.onResponse(statusCode, request);
                    else
                        return null;
                }
            });

        String result = null;
        try {
            switch (method) {
                case GET:
                    result = request.get(url);
                    break;
                case POST:
                    result = request.post(url);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        if (renderListener != null)
            renderListener.onRender(s);
        else
            super.onPostExecute(s);
    }

    public interface OnHttpPrepareRequestListener {
        public void onRequest(NetworkRequest request);
    }

    public interface OnHttpResponseListener {
        public String  onResponse(int statusCode, NetworkRequest request);
    }

    public interface OnPostRenderListener {
        public void onRender(String s);
    }
}
