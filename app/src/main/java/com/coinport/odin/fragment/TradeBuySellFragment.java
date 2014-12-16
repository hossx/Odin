package com.coinport.odin.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.coinport.odin.App;
import com.coinport.odin.R;
import com.coinport.odin.activity.TradeActivity;
import com.coinport.odin.adapter.DepthAdapter;
import com.coinport.odin.network.NetworkAsyncTask;
import com.coinport.odin.network.OnApiResponseListener;
import com.coinport.odin.obj.AccountInfo;
import com.coinport.odin.obj.DepthItem;
import com.coinport.odin.util.Constants;
import com.coinport.odin.network.NetworkRequest;
import com.coinport.odin.util.Util;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TradeBuySellFragment extends Fragment implements View.OnClickListener {
    private ListView buyListView;
    private DepthAdapter buyAdapter;
    private DepthAdapter sellAdapter;
    private String inCurrency, outCurrency;

    private Timer timer = new Timer();
    private TimerTask fetchDepthTask = null;
    ArrayList<DepthItem> buyItems = new ArrayList<>();
    ArrayList<DepthItem> sellItems = new ArrayList<>();
    private final Handler depthHandler = new Handler();
    private TextView lastPriceView;
    private String lastPrice;

    private TextView inValidView;
    private TextView outValidView;

    EditText buyPrice;
    EditText buyQuantity;
    EditText buyAmount;
    EditText sellPrice;
    EditText sellQuantity;
    EditText sellAmount;

    private View buySellView = null;
    public TradeBuySellFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inCurrency = ((TradeActivity)getActivity()).getInCurrency();
        outCurrency = ((TradeActivity)getActivity()).getOutCurrency();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopFetchData();
    }

    @Override
    public void onStart() {
        super.onStart();
        startFetchData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        buySellView = inflater.inflate(R.layout.trade_buy_sell_fragment, container, false);
        buyListView = (ListView) buySellView.findViewById(R.id.buy_depth);
        ListView sellListView = (ListView) buySellView.findViewById(R.id.sell_depth);
        buyAdapter = new DepthAdapter(getActivity(), buySellView);
        buyListView.setAdapter(buyAdapter);
        sellAdapter = new DepthAdapter(getActivity(), buySellView);
        sellListView.setAdapter(sellAdapter);
        lastPriceView = (TextView) buySellView.findViewById(R.id.last_price);
        lastPriceView.setOnClickListener(this);
        Button buyBtn = (Button) buySellView.findViewById(R.id.buy);
        buyBtn.setOnClickListener(this);
        Button sellBtn = (Button) buySellView.findViewById(R.id.sell);
        sellBtn.setOnClickListener(this);
        inValidView = (TextView) buySellView.findViewById(R.id.in_valid_amount);
        inValidView.setOnClickListener(this);
        outValidView = (TextView) buySellView.findViewById(R.id.out_valid_amount);
        outValidView.setOnClickListener(this);

        buyPrice = (EditText) buySellView.findViewById(R.id.buy_price_edit);
        buyPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {  }

            @Override
            public void afterTextChanged(Editable s) {
                buyInputGroupChanged(s, R.id.buy_price_edit);
            }
        });
        buyQuantity = (EditText) buySellView.findViewById(R.id.buy_quantity_edit);
        buyQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {  }

            @Override
            public void afterTextChanged(Editable s) {
                buyInputGroupChanged(s, R.id.buy_quantity_edit);
            }
        });
        buyAmount = (EditText) buySellView.findViewById(R.id.buy_amount_edit);
        buyAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {  }

            @Override
            public void afterTextChanged(Editable s) {
                buyInputGroupChanged(s, R.id.buy_amount_edit);
            }
        });
        sellPrice = (EditText) buySellView.findViewById(R.id.sell_price_edit);
        sellPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {  }

            @Override
            public void afterTextChanged(Editable s) {
                sellInputGroupChanged(s, R.id.sell_price_edit);
            }
        });
        sellQuantity = (EditText) buySellView.findViewById(R.id.sell_quantity_edit);
        sellQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {  }

            @Override
            public void afterTextChanged(Editable s) {
                sellInputGroupChanged(s, R.id.sell_quantity_edit);
            }
        });
        sellAmount = (EditText) buySellView.findViewById(R.id.sell_amount_edit);
        sellAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {  }

            @Override
            public void afterTextChanged(Editable s) {
                sellInputGroupChanged(s, R.id.sell_amount_edit);
            }
        });

        return buySellView;
    }

    private void startFetchData() {
        timer.cancel();
        if (fetchDepthTask != null)
            fetchDepthTask.cancel();
        fetchDepthTask = new FetchDepthTask();
        timer = new Timer();
        timer.schedule(fetchDepthTask, 0, 5000);
        fetchAsset();
    }

    private void stopFetchData() {
        if (timer != null)
            timer.cancel();
        if (fetchDepthTask != null)
            fetchDepthTask.cancel();
    }

    private void fetchAsset() {
        AccountInfo ai = App.getAccount();
        String url = String.format(Constants.ASSET_URL, ai.uid);
        NetworkAsyncTask task = new NetworkAsyncTask(url, Constants.HttpMethod.GET)
            .setOnSucceedListener(new OnApiResponseListener())
            .setOnFailedListener(new OnApiResponseListener())
            .setRenderListener(new NetworkAsyncTask.OnPostRenderListener() {
                @Override
                public void onRender(NetworkRequest s) {
                    JSONObject inCurrencyObj = Util.getJsonObjectByPath(s.getApiResult(),
                        "data.accounts." + inCurrency);
                    JSONObject outCurrencyObj = Util.getJsonObjectByPath(s.getApiResult(),
                            "data.accounts." + outCurrency);
                    if (buySellView == null)
                        return;
                    try {
                        String inValid, outValid, inPending, outPending;
                        double inPendingV, outPendingV;
                        if (inCurrencyObj != null) {
                            inValid = inCurrencyObj.getJSONObject("available").getString("display");
                            inPendingV = inCurrencyObj.getJSONObject("locked").getDouble("value") +
                                inCurrencyObj.getJSONObject("pendingWithdrawal").getDouble("value");
                            inPending = displayDouble(inPendingV);
                        } else {
                            inValid = "0";
                            inPending = "0";
                        }
                        if (outCurrencyObj != null) {
                            outValid = outCurrencyObj.getJSONObject("available").getString("display");
                            outPendingV = outCurrencyObj.getJSONObject("locked").getDouble("value") +
                                outCurrencyObj.getJSONObject("pendingWithdrawal").getDouble("value");
                            outPending = displayDouble(outPendingV);
                        } else {
                            outValid = "0";
                            outPending = "0";
                        }
                        inValidView.setText(inValid);
                        TextView inFrozenView = (TextView) buySellView.findViewById(R.id.in_frozen_amount);
                        inFrozenView.setText(inPending);
                        outValidView.setText(outValid);
                        TextView outFrozenView = (TextView) buySellView.findViewById(R.id.out_frozen_amount);
                        outFrozenView.setText(outPending);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        task.execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buy:
                Double buyPriceV = Double.parseDouble(buyPrice.getText().toString());
                Double buyQuantityV = Double.parseDouble(buyQuantity.getText().toString());
                Double buyAmountV = Double.parseDouble(buyAmount.getText().toString());
//                Toast.makeText(getActivity(), getString(R.string.exit_hint), Toast.LENGTH_SHORT).show();
                break;
            case R.id.sell:
                break;
            case R.id.last_price:
                buyPrice.setText(lastPrice);
                sellPrice.setText(lastPrice);
                break;
            case R.id.in_valid_amount:
                sellQuantity.setText(inValidView.getText());
                break;
            case R.id.out_valid_amount:
                buyAmount.setText(outValidView.getText());
                break;
        }
    }

    private class FetchDepthTask extends TimerTask {

        @Override
        public void run() {
            try {
                String url = String.format(Constants.DEPTH_URL, inCurrency.toLowerCase(), outCurrency.toLowerCase());
                NetworkRequest get = new NetworkRequest();
                get.setCharset(HTTP.UTF_8).setConnectionTimeout(5000).setSoTimeout(5000).setOnHttpRequestListener(
                        new NetworkRequest.OnHttpRequestListener() {
                    @Override
                    public void onRequest(NetworkRequest request) throws Exception {

                    }

                    @Override
                    public NetworkRequest onSucceed(int statusCode, NetworkRequest request) throws Exception {
                        JSONObject depthResult = new JSONObject(request.getInputStream());
                        JSONArray buyJsonList = Util.getJsonArrayByPath(depthResult, "data.b");
                        buyItems.clear();
                        for (int i = 0; i < buyJsonList.length(); ++i) {
                            JSONObject jsonObj = buyJsonList.getJSONObject(i);
                            buyItems.add(DepthItem.DepthItemBuilder.generateFromJson(jsonObj, true));
                        }
                        JSONArray sellJsonList = Util.getJsonArrayByPath(depthResult, "data.a");
                        sellItems.clear();
                        for (int i = 0; i < sellJsonList.length(); ++i) {
                            JSONObject jsonObj = sellJsonList.getJSONObject(i);
                            sellItems.add(0, DepthItem.DepthItemBuilder.generateFromJson(jsonObj, false));
                        }
                        return request;
                    }

                    @Override
                    public NetworkRequest onFailed(int statusCode, NetworkRequest request) throws Exception {
                        return request;
//                        return "GET 请求失败：statusCode "+ statusCode;
                    }
                }).get(url);

                url = String.format(Constants.TX_URL, inCurrency.toLowerCase(), outCurrency.toLowerCase());
                NetworkRequest getTx = new NetworkRequest();
                getTx.setCharset(HTTP.UTF_8).setConnectionTimeout(5000).setSoTimeout(5000).setOnHttpRequestListener(
                        new NetworkRequest.OnHttpRequestListener() {
                            @Override
                            public void onRequest(NetworkRequest request) throws Exception {
                                Map<String, String> params = new HashMap<>();
                                params.put("limit", "1");
                                params.put("skip", "0");
                                request.addRequestParameters(params);
                            }

                            @Override
                            public NetworkRequest onSucceed(int statusCode, NetworkRequest request) throws Exception {
                                JSONObject txResult = new JSONObject(request.getInputStream());
                                lastPrice = Util.getJsonObjectByPath(Util.getJsonArrayByPath(txResult, "data.items")
                                    .getJSONObject(0), "price").getString("display");
                                return request;
                            }

                            @Override
                            public NetworkRequest onFailed(int statusCode, NetworkRequest request) throws Exception {
                                return request;
//                                return "GET 请求失败：statusCode "+ statusCode;
                            }
                        }).get(url);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            depthHandler.post(new Runnable() {
                @Override
                public void run() {
                    buyAdapter.setDepthItems(buyItems);
                    sellAdapter.setDepthItems(sellItems);
                    buyAdapter.notifyDataSetChanged();
                    sellAdapter.notifyDataSetChanged();
                    lastPriceView.setText(lastPrice);
                }
            });
        }
    }

    private void inputGroupChanged(Editable s, int id, int priceId, int quantityId, int amountId,
        TextView priceView, TextView quantityView, TextView amountView) {
        if (s.toString().equals(""))
            return;
        String priceStr = priceView.getText().toString();
        String quantityStr = quantityView.getText().toString();
        String amountStr = amountView.getText().toString();
        double price, quantity, amount;
        String tmpStr;
        if (id == priceId) {
            if (quantityStr.equals("")) {
                return;
            }
            price = Double.valueOf(s.toString());
            quantity = Double.valueOf(quantityStr);
            amount = price * quantity;
            tmpStr = displayDouble(amount);
            if (!amountStr.equals(tmpStr))
                amountView.setText(tmpStr);
        } else if (id == quantityId) {
            if (priceStr.equals("")) {
                return;
            }
            price = Double.valueOf(priceStr);
            quantity = Double.valueOf(s.toString());
            amount = price * quantity;
            tmpStr = displayDouble(amount);
            if (!amountStr.equals(tmpStr))
                amountView.setText(tmpStr);
        } else if (id == amountId) {
            if (priceStr.equals(""))
                return;
            price = Double.valueOf(priceStr);
            if (price < 0.000000001)
                return;
            amount = Double.valueOf(s.toString());
            quantity = amount / price;
            tmpStr = displayDouble(quantity);
            if (!quantityStr.equals(tmpStr))
                quantityView.setText(tmpStr);
        }
    }

    private void buyInputGroupChanged(Editable s, int id) {
        inputGroupChanged(
            s, id, R.id.buy_price_edit, R.id.buy_quantity_edit, R.id.buy_amount_edit, buyPrice, buyQuantity, buyAmount);
    }

    private void sellInputGroupChanged(Editable s, int id) {
        inputGroupChanged(s, id, R.id.sell_price_edit, R.id.sell_quantity_edit, R.id.sell_amount_edit, sellPrice,
            sellQuantity, sellAmount);
    }
    private String displayDouble(double v) {
        return (new BigDecimal(v).setScale(4, RoundingMode.CEILING)).toPlainString();
    }
}
