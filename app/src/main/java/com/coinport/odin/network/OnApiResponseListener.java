package com.coinport.odin.network;

import com.coinport.odin.network.NetworkRequest;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

public class OnApiResponseListener implements NetworkAsyncTask.OnHttpResponseListener {
    @Override
    public NetworkRequest onResponse(int statusCode, NetworkRequest request) {
        if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
            request.setApiStatus(NetworkRequest.ApiStatus.UNAUTH);
        } else if (statusCode != HttpStatus.SC_OK) {
            request.setApiStatus(NetworkRequest.ApiStatus.NETWORK_ERROR);
        } else {
            try {
                JSONObject json = new JSONObject(request.getResult());
                request.setApiResult(json);
                request.setApiMessage(json.getString("message"));
                if (json.getBoolean("success")) {
                    request.setApiStatus(NetworkRequest.ApiStatus.SUCCEED);
                } else {
                    request.setApiStatus(NetworkRequest.ApiStatus.INTERNAL_ERROR);
                }
            } catch (Exception e) {
                request.setApiStatus(NetworkRequest.ApiStatus.BAD_FORMAT);
                e.printStackTrace();
            }
        }
        return request;
    }
}
