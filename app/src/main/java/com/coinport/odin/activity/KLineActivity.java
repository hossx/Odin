package com.coinport.odin.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.coinport.odin.R;
import com.coinport.odin.library.charts.entity.IStickEntity;
import com.coinport.odin.library.charts.entity.ListChartData;
import com.coinport.odin.library.charts.entity.OHLCEntity;
import com.coinport.odin.library.charts.event.IZoomable;
import com.coinport.odin.library.charts.view.GridChart;
import com.coinport.odin.library.charts.view.SlipCandleStickChart;
import com.coinport.odin.util.Util;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class KLineActivity extends FragmentActivity {
    private SlipCandleStickChart candlestickchart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kline);
        initCandleStickChart();
    }
    
    private void initCandleStickChart() {
        candlestickchart = (SlipCandleStickChart) findViewById(R.id.candlestickchart);

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
        candlestickchart.setZoomBaseLine(IZoomable.ZOOM_BASE_LINE_CENTER);
        candlestickchart.setStickSpacing(5);

        candlestickchart.setDisplayLongitudeTitle(true);
        candlestickchart.setDisplayLatitudeTitle(true);
        candlestickchart.setDisplayLatitude(true);
        candlestickchart.setDisplayLongitude(false);
        candlestickchart.setBackgroundColor(Color.BLACK);

        candlestickchart.setDataQuadrantPaddingTop(50);
        candlestickchart.setDataQuadrantPaddingBottom(50);
        candlestickchart.setDataQuadrantPaddingLeft(10);
        candlestickchart.setDataQuadrantPaddingRight(10);
        candlestickchart.setAxisYTitleQuadrantWidth(150);
        candlestickchart.setAxisXTitleQuadrantHeight(80);
        candlestickchart.setAxisXPosition(GridChart.AXIS_X_POSITION_BOTTOM);
        candlestickchart.setAxisYPosition(GridChart.AXIS_Y_POSITION_RIGHT);

        List<IStickEntity> datas = new ArrayList<IStickEntity>();
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
                    datas.add(new OHLCEntity(open, high, low, close, standerlize(ts, 60 * 1000)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        
        candlestickchart.setStickData(new ListChartData<IStickEntity>(datas));
    }
    
    private long standerlize(long date, long interval) {
        return date / interval * interval;
    }
}
