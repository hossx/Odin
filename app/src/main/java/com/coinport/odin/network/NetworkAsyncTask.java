package com.coinport.odin.network;

import android.os.AsyncTask;

import com.coinport.odin.util.Constants.HttpMethod;

import org.apache.http.HttpResponse;
import org.apache.http.protocol.HTTP;

import java.util.Map;

public class NetworkAsyncTask extends AsyncTask<Map<String, String>, Void, NetworkRequest> {
    private String url;
    private HttpMethod method;

    private OnHttpPrepareRequestListener prepareRequestListener = null;
    private OnHttpResponseListener onSucceedListener = null;
    private OnHttpResponseListener onFailedListener = null;
    private OnPostRenderListener renderListener = null;

    public NetworkAsyncTask setPrepareRequestListener(OnHttpPrepareRequestListener prepareRequestListener) {
        this.prepareRequestListener = prepareRequestListener;
        return this;
    }

    public NetworkAsyncTask setOnSucceedListener(OnHttpResponseListener onSucceedListener) {
        this.onSucceedListener = onSucceedListener;
        return this;
    }

    public NetworkAsyncTask setOnFailedListener(OnHttpResponseListener onFailedListener) {
        this.onFailedListener = onFailedListener;
        return this;
    }

    public NetworkAsyncTask setRenderListener(OnPostRenderListener renderListener) {
        this.renderListener = renderListener;
        return this;
    }

    public NetworkAsyncTask(String url, HttpMethod method) {
        this.url = url;
        this.method = method;
    }

    @Override
    protected NetworkRequest doInBackground(final Map<String, String>... params) {
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
                public NetworkRequest onSucceed(int statusCode, NetworkRequest request) throws Exception {
                    if (onSucceedListener != null)
                        return onSucceedListener.onResponse(statusCode, request);
                    else
                        return request;
                }

                @Override
                public NetworkRequest onFailed(int statusCode, NetworkRequest request) throws Exception {
                    if (onFailedListener != null)
                        return onFailedListener.onResponse(statusCode, request);
                    else
                        return request;
                }
            });

        try {
            switch (method) {
                case GET:
                    request.get(url);
                    break;
                case POST:
                    request.post(url);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return request;
    }

    @Override
    protected void onPostExecute(NetworkRequest s) {
        if (renderListener != null)
            renderListener.onRender(s);
        else
            super.onPostExecute(s);
    }

    public interface OnHttpPrepareRequestListener {
        public void onRequest(NetworkRequest request);
    }

    public interface OnHttpResponseListener {
        public NetworkRequest onResponse(int statusCode, NetworkRequest request);
    }

    public interface OnPostRenderListener {
        public void onRender(NetworkRequest s);
    }
}
