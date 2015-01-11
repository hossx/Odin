package com.coinport.odin.network;

import android.os.AsyncTask;

import com.coinport.odin.util.Constants.HttpMethod;

import java.util.Map;

public class NetworkAsyncTask extends AsyncTask<Map<String, String>, Void, NetworkRequest> {
    private String url;
    private HttpMethod method;
    private long delay = 0;

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

    public NetworkAsyncTask(String url, HttpMethod method, long delay) {
        this.url = url;
        this.method = method;
        this.delay = delay;
    }

    @SafeVarargs
    @Override
    protected final NetworkRequest doInBackground(final Map<String, String>... params) {
        if (delay != 0)
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        String m;
        if (method == HttpMethod.GET)
            m = NetworkRequest.HTTP_GET;
        else
            m = NetworkRequest.HTTP_POST;

        NetworkRequest request = new NetworkRequest(url, m);
        request.setOnHttpRequestListener(
            new NetworkRequest.OnHttpRequestListener() {
                @Override
                public void onRequest(NetworkRequest request) throws Exception {
                    if (params != null && params.length > 0)
                        request.addRequestParameters(params[0]);
                    if (prepareRequestListener != null)
                        prepareRequestListener.onRequest(request);
                }

                @Override
                public void onSucceed(int statusCode, NetworkRequest request) throws Exception {
                    if (onSucceedListener != null)
                        onSucceedListener.onResponse(statusCode, request);
                }

                @Override
                public void onFailed(int statusCode, NetworkRequest request) throws Exception {
                    if (onFailedListener != null)
                        onFailedListener.onResponse(statusCode, request);
                }
            });

        try {
            request.execute();
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
