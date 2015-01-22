package com.coinport.odin.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.coinport.odin.R;
import com.coinport.odin.library.charts.entity.DateValueEntity;
import com.coinport.odin.library.charts.entity.IStickEntity;
import com.coinport.odin.library.charts.entity.LineEntity;
import com.coinport.odin.library.charts.entity.ListChartData;
import com.coinport.odin.library.charts.entity.OHLCEntity;
import com.coinport.odin.library.charts.event.IZoomable;
import com.coinport.odin.library.charts.view.GridChart;
import com.coinport.odin.library.charts.view.MASlipCandleStickChart;
import com.coinport.odin.util.Util;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class KLineActivity extends FragmentActivity {
    private MASlipCandleStickChart candlestickchart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kline);
        initCandleStickChart();
    }
    
    private void initCandleStickChart() {
        candlestickchart = (MASlipCandleStickChart) findViewById(R.id.candlestickchart);

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

        List<LineEntity<DateValueEntity>> lines = new ArrayList<LineEntity<DateValueEntity>>();

        LineEntity<DateValueEntity> MA7 = new LineEntity<DateValueEntity>();
        MA7.setTitle("MA7");
        MA7.setLineColor(Color.WHITE);
        MA7.setLineData(computeMA(datas, 7));
        lines.add(MA7);
        
        LineEntity<DateValueEntity> MA30 = new LineEntity<DateValueEntity>();
        MA30.setTitle("MA30");
        MA30.setLineColor(Color.YELLOW);
        MA30.setLineData(computeMA(datas, 30));
        lines.add(MA30);

        candlestickchart.setLinesData(lines);
        candlestickchart.setStickData(new ListChartData<IStickEntity>(datas));
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

    private long standerlize(long date, long interval) {
        return date / interval * interval;
    }
}
