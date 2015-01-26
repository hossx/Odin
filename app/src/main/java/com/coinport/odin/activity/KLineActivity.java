package com.coinport.odin.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.coinport.odin.R;
import com.coinport.odin.dialog.CustomProgressDialog;
import com.coinport.odin.library.charts.common.ICrossLines;
import com.coinport.odin.library.charts.common.IDataCursor;
import com.coinport.odin.library.charts.entity.ColoredStickEntity;
import com.coinport.odin.library.charts.entity.DateValueEntity;
import com.coinport.odin.library.charts.entity.IStickEntity;
import com.coinport.odin.library.charts.entity.LineEntity;
import com.coinport.odin.library.charts.entity.ListChartData;
import com.coinport.odin.library.charts.entity.OHLCEntity;
import com.coinport.odin.library.charts.entity.StickEntity;
import com.coinport.odin.library.charts.event.IDisplayCursorListener;
import com.coinport.odin.library.charts.event.ITouchable;
import com.coinport.odin.library.charts.event.IZoomable;
import com.coinport.odin.library.charts.event.OnTouchGestureListener;
import com.coinport.odin.library.charts.view.ColoredMASlipStickChart;
import com.coinport.odin.library.charts.view.GridChart;
import com.coinport.odin.library.charts.view.MASlipCandleStickChart;
import com.coinport.odin.network.NetworkRequest;
import com.coinport.odin.util.Constants;
import com.coinport.odin.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class KLineActivity extends FragmentActivity {
    static private final Map<String, Integer> PERIOD = new HashMap<>();
    static private final int MAX_STICK_NUM = 181;
    
    private MASlipCandleStickChart candlestickchart;
    private ColoredMASlipStickChart volumechart;
    private List<IStickEntity> sticks = new ArrayList<>();
    private List<IStickEntity> vols = new ArrayList<>();

    private int period = 6;
    private long cursor = -1;
    private TextView open;
    private TextView close;
    private TextView high;
    private TextView low;
    private TextView volume;

    private String inCurrency;
    private String outCurrency;

    private Timer timer = null;
    private TimerTask fetchKlineTask = null;

    private boolean isTouched = false;
    private final Handler handler = new Handler();
    private CustomProgressDialog cpd = null;

    static {
        PERIOD.put("1分", 1);
        PERIOD.put("3分", 2);
        PERIOD.put("5分", 3);
        PERIOD.put("15分", 4);
        PERIOD.put("30分", 5);
        PERIOD.put("1小时", 6);
        PERIOD.put("2小时", 7);
        PERIOD.put("4小时", 8);
        PERIOD.put("6小时", 9);
        PERIOD.put("12小时", 10);
        PERIOD.put("1天", 11);
        PERIOD.put("3天", 12);
        PERIOD.put("1周", 13);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kline);
        Intent intent = this.getIntent();
        inCurrency = intent.getStringExtra("inCurrency");
        outCurrency = intent.getStringExtra("outCurrency");
        candlestickchart = (MASlipCandleStickChart) findViewById(R.id.candlestickchart);
        volumechart = (ColoredMASlipStickChart) findViewById(R.id.volumechart);

        TextView title = (TextView) findViewById(R.id.kline_title);
        title.setText(inCurrency + "\n" + outCurrency);

        open = (TextView) findViewById(R.id.open);
        close = (TextView) findViewById(R.id.close);
        high = (TextView) findViewById(R.id.high);
        low = (TextView) findViewById(R.id.low);
        volume = (TextView) findViewById(R.id.volume);

        Spinner periodSelector = (Spinner) findViewById(R.id.period_selector);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.period_array,
                R.layout.white_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        periodSelector.setAdapter(spinnerAdapter);
        periodSelector.setSelection(5);
        periodSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                period = PERIOD.get(parent.getItemAtPosition(position).toString());
                cursor = -1;
                if (cpd == null) {
                    cpd = CustomProgressDialog.createDialog(KLineActivity.this);
                    cpd.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        initCandleStickChart();
        initVolumeChart();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startFetchKlineData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopFetchKlineData();
    }

    private void startFetchKlineData() {
        if (timer != null)
            timer.cancel();
        if (fetchKlineTask != null)
            fetchKlineTask.cancel();
        fetchKlineTask = new FetchKlineTask();
        timer = new Timer();
        timer.schedule(fetchKlineTask, 0, 5000);

    }

    private void stopFetchKlineData() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (fetchKlineTask != null) {
            fetchKlineTask.cancel();
            fetchKlineTask = null;
        }
    }

    private void initCandleStickChart() {

        candlestickchart.setAxisXColor(Color.LTGRAY);
        candlestickchart.setAxisYColor(Color.LTGRAY);
        candlestickchart.setLatitudeColor(Color.GRAY);
        candlestickchart.setLongitudeColor(Color.GRAY);
        candlestickchart.setBorderColor(Color.LTGRAY);
        candlestickchart.setLongitudeFontSize(40);
        candlestickchart.setLongitudeFontColor(Color.WHITE);
        candlestickchart.setLatitudeFontSize(40);
        candlestickchart.setLatitudeFontColor(Color.WHITE);

        // 最大价格
        candlestickchart.setMaxValue(8000);
        // 最小价格
        candlestickchart.setMinValue(0);
        candlestickchart.setMinDisplayNumber(10);
        candlestickchart.setZoomBaseLine(IZoomable.ZOOM_BASE_LINE_CENTER);
        candlestickchart.setStickSpacing(5);

        candlestickchart.setDisplayLongitudeTitle(false);
        candlestickchart.setDisplayLatitudeTitle(true);
        candlestickchart.setDisplayLatitude(true);
        candlestickchart.setDisplayLongitude(false);
        candlestickchart.setBackgroundColor(Color.BLACK);

        candlestickchart.setDataQuadrantPaddingTop(50);
        candlestickchart.setDataQuadrantPaddingTop(5);
        candlestickchart.setDataQuadrantPaddingLeft(10);
        candlestickchart.setDataQuadrantPaddingRight(10);
        candlestickchart.setAxisYTitleQuadrantWidth(150);
//        candlestickchart.setAxisXTitleQuadrantHeight(80);
        candlestickchart.setAxisXTitleQuadrantHeight(0);
        candlestickchart.setAxisXPosition(GridChart.AXIS_X_POSITION_BOTTOM);
        candlestickchart.setAxisYPosition(GridChart.AXIS_Y_POSITION_RIGHT);
        candlestickchart.setBindCrossLinesToStick(ICrossLines.BIND_TO_TYPE_HIRIZIONAL);

        candlestickchart.setOnDisplayCursorListener(new IDisplayCursorListener() {
            @Override
            public void onCursorChanged(IDataCursor dataCursor, int displayFrom, int displayNumber) {
                volumechart.setDisplayFrom(displayFrom);
                volumechart.setDisplayNumber(displayNumber);
                volumechart.postInvalidate();
            }
        });
        
        candlestickchart.setOnTouchGestureListener(new OnTouchGestureListener(){

            @Override
            public void onTouchDown(ITouchable touchable, MotionEvent event) {
                super.onTouchDown(touchable, event);
                volumechart.touchDown(new PointF(event.getX(), event.getY()));
            }

            @Override
            public void onTouchMoved(ITouchable touchable, MotionEvent event) {
                super.onTouchMoved(touchable, event);
                volumechart.touchMoved(new PointF(event.getX(), event.getY()));
            }
            
            @Override
            public void onTouchUp(ITouchable touchable, MotionEvent event) {
                super.onTouchUp(touchable, event);
                updateMetrics(candlestickchart.getSelectedIndex());
                isTouched = true;
                volumechart.touchUp(new PointF(event.getX(), event.getY()));
            }
        });
    }

    private void updateMetrics(int index) {
        if (index < 0 || index >= sticks.size())
            return;
        OHLCEntity ohlc = (OHLCEntity) sticks.get(index);
        open.setText(Util.autoDisplayDouble(ohlc.getOpen()));
        close.setText(Util.autoDisplayDouble(ohlc.getClose()));
        high.setText(Util.autoDisplayDouble(ohlc.getHigh()));
        low.setText(Util.autoDisplayDouble(ohlc.getLow()));

        StickEntity se = (StickEntity) vols.get(index);
        volume.setText(Util.autoDisplayDouble(se.getHigh()));
    }
    
    private void initVolumeChart() {
        volumechart.setAxisXColor(Color.LTGRAY);
        volumechart.setAxisYColor(Color.LTGRAY);
        volumechart.setLatitudeColor(Color.GRAY);
        volumechart.setLongitudeColor(Color.GRAY);
        volumechart.setBorderColor(Color.LTGRAY);
        volumechart.setLongitudeFontColor(Color.WHITE);
        volumechart.setLongitudeFontSize(40);
        volumechart.setLatitudeFontColor(Color.WHITE);
        volumechart.setLatitudeFontSize(40);

        // 最大价格
        volumechart.setMaxValue(900000000);
        // 最小价格
        volumechart.setMinValue(0);
        volumechart.setLatitudeNum(3);

        volumechart.setMinDisplayNumber(10);
        volumechart.setZoomBaseLine(IZoomable.ZOOM_BASE_LINE_CENTER);
        volumechart.setStickSpacing(5);

        volumechart.setZoomBaseLine(IZoomable.ZOOM_BASE_LINE_CENTER);

        volumechart.setDisplayLatitudeTitle(true);
        volumechart.setDisplayLatitude(false);
        volumechart.setDisplayLongitude(false);
        volumechart.setDisplayLongitudeTitle(true);
        volumechart.setDisplayCrossXOnTouch(false);
        volumechart.setBackgroundColor(Color.BLACK);
        volumechart.setBindCrossLinesToStick(ICrossLines.BIND_TO_TYPE_HIRIZIONAL);

        volumechart.setDataQuadrantPaddingTop(50);
        volumechart.setDataQuadrantPaddingBottom(5);
        volumechart.setDataQuadrantPaddingLeft(10);
        volumechart.setDataQuadrantPaddingRight(10);
        volumechart.setAxisYTitleQuadrantWidth(150);
        volumechart.setAxisXTitleQuadrantHeight(80);
        volumechart.setAxisXPosition(GridChart.AXIS_X_POSITION_BOTTOM);
        volumechart.setAxisYPosition(GridChart.AXIS_Y_POSITION_RIGHT);
        
        volumechart.setOnDisplayCursorListener(new IDisplayCursorListener() {
            @Override
            public void onCursorChanged(IDataCursor dataCursor, int displayFrom, int displayNumber) {
                candlestickchart.setDisplayFrom(displayFrom);
                candlestickchart.setDisplayNumber(displayNumber);
                candlestickchart.postInvalidate();
            }
        });
        
        volumechart.setOnTouchGestureListener(new OnTouchGestureListener() {
            @Override
            public void onTouchDown(ITouchable touchable, MotionEvent event) {
                super.onTouchDown(touchable, event);
                float oriY = 0;
                if (candlestickchart.getTouchPoint() != null)
                    oriY = candlestickchart.getTouchPoint().y;
                candlestickchart.touchDown(new PointF(event.getX(), oriY));
            }

            @Override
            public void onTouchMoved(ITouchable touchable, MotionEvent event) {
                super.onTouchMoved(touchable, event);
                float oriY = 0;
                if (candlestickchart.getTouchPoint() != null)
                    oriY = candlestickchart.getTouchPoint().y;
                candlestickchart.touchMoved(new PointF(event.getX(), oriY));
            }

            @Override
            public void onTouchUp(ITouchable touchable, MotionEvent event) {
                super.onTouchUp(touchable, event);
                float oriY = 0;
                if (candlestickchart.getTouchPoint() != null)
                    oriY = candlestickchart.getTouchPoint().y;
                updateMetrics(volumechart.getSelectedIndex());
                isTouched = true;
                candlestickchart.touchUp(new PointF(event.getX(), oriY));
            }

        });
    }

    private List<DateValueEntity> computeMA(List<IStickEntity> ohlc, int days) {

        if (days < 2) {
            return null;
        }

        List<DateValueEntity> MAValues = new ArrayList<>();

        float sum = 0;
        float avg = 0;
        for (int i = 0; i < ohlc.size(); i++) {
            float close = (float) ((OHLCEntity) ohlc.get(i)).getClose();
            if (i < days) {
                sum = sum + close;
                avg = sum / (i + 1f);
            } else {
                sum = sum + close - (float) ((OHLCEntity) ohlc.get(i - days)).getClose();
                avg = sum / days;
            }
            MAValues.add(new DateValueEntity(avg, ohlc.get(i).getDate()));
        }

        return MAValues;
    }

    private List<DateValueEntity> computeVMA(List<IStickEntity> vol, int days) {
        if (days < 2) {
            return null;
        }

        List<DateValueEntity> MAValues = new ArrayList<DateValueEntity>();

        float sum = 0;
        float avg = 0;
        for (int i = 0; i < vol.size(); i++) {
            float close = (float) vol.get(i).getHigh();
            if (i < days) {
                sum = sum + close;
                avg = sum / (i + 1f);
            } else {
                sum = sum + close - (float) vol.get(i - days).getHigh();
                avg = sum / days;
            }
            MAValues.add(new DateValueEntity(avg, vol.get(i).getDate()));
        }

        return MAValues;
    }

    private long standerlize(long date, long interval) {
        return date / interval * interval;
    }

    private class FetchKlineTask extends TimerTask {

        @Override
        public void run() {
            try {
                if (cursor == -1 && cpd == null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (cpd == null) {
                                cpd = CustomProgressDialog.createDialog(KLineActivity.this);
                                cpd.show();
                            }
                        }
                    });
                }
                String url = String.format(Constants.KLINE_URL,
                    inCurrency.toLowerCase() + "-" + outCurrency.toLowerCase());
                NetworkRequest get = new NetworkRequest(url, NetworkRequest.HTTP_GET);
                get.setOnHttpRequestListener(
                        new NetworkRequest.OnHttpRequestListener() {
                            @Override
                            public void onRequest(NetworkRequest request) throws Exception {
                                Map<String, String> params = new HashMap<>();
                                params.put("period", String.valueOf(period));
                                if (cursor != -1)
                                    params.put("from", String.valueOf(cursor));
                                else
                                    params.put("from", "0");
                                request.addRequestParameters(params);
                            }

                            @Override
                            public void onSucceed(int statusCode, NetworkRequest request) throws Exception {
                                JSONObject klineResult = new JSONObject(request.getResult());
                                JSONArray ja = Util.getJsonArrayByPath(klineResult, "data.candles");

                                if (ja != null) {
                                    if (cursor == -1) {
                                        sticks.clear();
                                        vols.clear();
                                    }
                                    for (int i = 0; i < ja.length(); ++i) {
                                        try {
                                            JSONArray arr = ja.getJSONArray(i);
                                            long ts = arr.getLong(0);
                                            double open = arr.getDouble(1);
                                            double high = arr.getDouble(2);
                                            double low = arr.getDouble(3);
                                            double close = arr.getDouble(4);
                                            double amount = arr.getDouble(5);
                                            if (!sticks.isEmpty() && sticks.get(sticks.size() - 1).getDate() >= ts)
                                                continue;
                                            sticks.add(new OHLCEntity(open, high, low, close, standerlize(ts, 60 * 1000)));
                                            if (open > close)
                                                vols.add(new ColoredStickEntity(amount, 0, standerlize(ts, 60 * 1000), Color.RED));
                                            else
                                                vols.add(new ColoredStickEntity(amount, 0, standerlize(ts, 60 * 1000), Color.GREEN));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (sticks.size() > MAX_STICK_NUM) {
                                        int removeNum = sticks.size() - MAX_STICK_NUM;
                                        for (int i = 0; i < removeNum; ++i) {
                                            sticks.remove(0);
                                            vols.remove(0);
                                        }
                                    }
                                    if (cursor == -1) {
                                        if (sticks.size() >= 52) {
                                            candlestickchart.setDisplayNumber(52);
                                            volumechart.setDisplayNumber(52);
                                            candlestickchart.setDisplayFrom(sticks.size() - 52);
                                            volumechart.setDisplayFrom(sticks.size() - 52);
                                        } else {
                                            candlestickchart.setDisplayNumber(sticks.size());
                                            volumechart.setDisplayNumber(sticks.size());
                                            candlestickchart.setDisplayFrom(0);
                                            volumechart.setDisplayFrom(0);
                                        }
                                    }
                                    cursor = sticks.get(sticks.size() - 1).getDate() + 1;
                                    if (!isTouched || cpd != null) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (!isTouched) {
                                                    updateMetrics(sticks.size() - 1);
                                                }
                                                if (cpd != null) {
                                                    cpd.dismiss();
                                                    cpd = null;
                                                }
                                            }
                                        });
                                    }
                                    List<LineEntity<DateValueEntity>> cslines = new ArrayList<LineEntity<DateValueEntity>>();

                                    LineEntity<DateValueEntity> csMA7 = new LineEntity<DateValueEntity>();
                                    csMA7.setTitle("MA7");
                                    csMA7.setLineColor(Color.WHITE);
                                    csMA7.setLineData(computeMA(sticks, 7));
                                    cslines.add(csMA7);

                                    LineEntity<DateValueEntity> csMA30 = new LineEntity<DateValueEntity>();
                                    csMA30.setTitle("MA30");
                                    csMA30.setLineColor(Color.YELLOW);
                                    csMA30.setLineData(computeMA(sticks, 30));
                                    cslines.add(csMA30);

                                    candlestickchart.setLinesData(cslines);
                                    candlestickchart.setStickData(new ListChartData<IStickEntity>(sticks));
                                    candlestickchart.postInvalidate();

                                    List<LineEntity<DateValueEntity>> vlines = new ArrayList<LineEntity<DateValueEntity>>();

                                    LineEntity<DateValueEntity> vMA7 = new LineEntity<DateValueEntity>();
                                    vMA7.setTitle("MA7");
                                    vMA7.setLineColor(Color.WHITE);
                                    vMA7.setLineData(computeVMA(vols, 7));
                                    vlines.add(vMA7);

                                    LineEntity<DateValueEntity> vMA30 = new LineEntity<DateValueEntity>();
                                    vMA30.setTitle("MA30");
                                    vMA30.setLineColor(Color.YELLOW);
                                    vMA30.setLineData(computeVMA(vols, 30));
                                    vlines.add(vMA30);

                                    volumechart.setLineData(vlines);
                                    volumechart.setStickData(new ListChartData<IStickEntity>(vols));
                                    volumechart.postInvalidate();
                                }
                            }

                            @Override
                            public void onFailed(int statusCode, NetworkRequest request) throws Exception {
                            }
                        }).execute();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
