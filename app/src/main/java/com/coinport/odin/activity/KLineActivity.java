package com.coinport.odin.activity;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;

import com.coinport.odin.R;
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
import com.coinport.odin.util.Util;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class KLineActivity extends FragmentActivity {
    private MASlipCandleStickChart candlestickchart;
    private ColoredMASlipStickChart volumechart;
    private List<IStickEntity> sticks = new ArrayList<IStickEntity>();
    private List<IStickEntity> vols = new ArrayList<IStickEntity>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kline);
        candlestickchart = (MASlipCandleStickChart) findViewById(R.id.candlestickchart);
        volumechart = (ColoredMASlipStickChart) findViewById(R.id.volumechart);
        initData();
        
        initCandleStickChart();
        initVolumeChart();
    }
    
    private void initData() {
        sticks = new ArrayList<IStickEntity>();
        vols = new ArrayList<IStickEntity>();
        JSONArray ja =  Util.getJsonArrayByPath(Util.getJsonObjectFromFile(this, "kline_mock.json"), "data.candles");

        if (ja != null) {
            for (int i = 0; i < ja.length(); ++i) {
                try {
                    JSONArray arr = ja.getJSONArray(i);
                    long ts = arr.getLong(0);
                    double open = arr.getDouble(1);
                    double high = arr.getDouble(2);
                    double low = arr.getDouble(3);
                    double close = arr.getDouble(4);
                    double amount = arr.getDouble(5);
                    sticks.add(new OHLCEntity(open, high, low, close, standerlize(ts, 60 * 1000)));
                    if (open > close)
                        vols.add(new ColoredStickEntity(amount, 0, standerlize(ts, 60 * 1000), Color.RED));
                    else
                        vols.add(new ColoredStickEntity(amount, 0, standerlize(ts, 60 * 1000), Color.GREEN));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
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
        candlestickchart.setDisplayNumber(52);
        candlestickchart.setDisplayFrom(sticks.size() - 52);
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

        List<LineEntity<DateValueEntity>> lines = new ArrayList<LineEntity<DateValueEntity>>();

        LineEntity<DateValueEntity> MA7 = new LineEntity<DateValueEntity>();
        MA7.setTitle("MA7");
        MA7.setLineColor(Color.WHITE);
        MA7.setLineData(computeMA(sticks, 7));
        lines.add(MA7);
        
        LineEntity<DateValueEntity> MA30 = new LineEntity<DateValueEntity>();
        MA30.setTitle("MA30");
        MA30.setLineColor(Color.YELLOW);
        MA30.setLineData(computeMA(sticks, 30));
        lines.add(MA30);

        candlestickchart.setLinesData(lines);
        candlestickchart.setStickData(new ListChartData<IStickEntity>(sticks));
        
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
                volumechart.touchUp(new PointF(event.getX(), event.getY()));
            }
        });
    }

    private void initVolumeChart() {
        List<LineEntity<DateValueEntity>> lines = new ArrayList<LineEntity<DateValueEntity>>();

        LineEntity<DateValueEntity> MA7 = new LineEntity<DateValueEntity>();
        MA7.setTitle("MA7");
        MA7.setLineColor(Color.WHITE);
        MA7.setLineData(computeVMA(vols, 7));
        lines.add(MA7);

        LineEntity<DateValueEntity> VMA10 = new LineEntity<DateValueEntity>();
        VMA10.setTitle("MA10");
        VMA10.setLineColor(Color.RED);
        VMA10.setLineData(computeVMA(vols, 10));
        lines.add(VMA10);
        
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

        volumechart.setDisplayNumber(52);
        volumechart.setDisplayFrom(vols.size() - 52);
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
        
        volumechart.setLineData(lines);
        volumechart.setStickData(new ListChartData<IStickEntity>(vols));
        
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
                candlestickchart.touchUp(new PointF(event.getX(), oriY));
            }

        });
    }

    private List<DateValueEntity> computeMA(List<IStickEntity> ohlc, int days) {

        if (days < 2) {
            return null;
        }

        List<DateValueEntity> MAValues = new ArrayList<DateValueEntity>();

        float sum = 0;
        float avg = 0;
        for (int i = 0; i < ohlc.size(); i++) {
            float close = (float) ((OHLCEntity) ohlc.get(i)).getClose();
            if (i < days) {
                sum = sum + close;
                avg = sum / (i + 1f);
            } else {
                sum = sum + close
                        - (float) ((OHLCEntity) ohlc.get(i - days)).getClose();
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
}
