package com.coinport.odin.library.charts.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;

import com.coinport.odin.library.charts.entity.ColoredStickEntity;

public class ColoredMASlipStickChart extends MASlipStickChart {
    public static final int DEFAULT_COLORED_STICK_STYLE_WITH_BORDER = 0;
    public static final int DEFAULT_COLORED_STICK_STYLE_NO_BORDER = 1;
    public static final int DEFAULT_COLORED_STICK_STYLE = DEFAULT_COLORED_STICK_STYLE_NO_BORDER;

    private int coloredStickStyle = DEFAULT_COLORED_STICK_STYLE_NO_BORDER;

    public ColoredMASlipStickChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ColoredMASlipStickChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColoredMASlipStickChart(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void drawSticks(Canvas canvas) {
        if (null == stickData) {
            return;
        }
        if (stickData.size() == 0) {
            return;
        }

        float stickWidth = dataQuadrant.getQuadrantPaddingWidth() / displayNumber - stickSpacing;
        float stickX = dataQuadrant.getQuadrantPaddingStartX();

        Paint mPositivePaintStick = new Paint();
        mPositivePaintStick.setColor(Color.GREEN);
        mPositivePaintStick.setStyle(Paint.Style.STROKE);
        Paint mNegativePaintStick = new Paint();
        mNegativePaintStick.setColor(Color.RED);
        for (int i = displayFrom; i < displayFrom + displayNumber; i++) {
            ColoredStickEntity entity = (ColoredStickEntity) stickData.get(i);

            float highY = (float) ((1f - (entity.getHigh() - minValue) / (maxValue - minValue))
                    * (dataQuadrant.getQuadrantPaddingHeight()) + dataQuadrant.getQuadrantPaddingStartY());
            float lowY = (float) ((1f - (entity.getLow() - minValue) / (maxValue - minValue))
                    * (dataQuadrant.getQuadrantPaddingHeight()) + dataQuadrant.getQuadrantPaddingStartY());

            Paint paint;
            if (entity.getColor() == Color.GREEN)
                paint = mPositivePaintStick;
            else
                paint = mNegativePaintStick;

            // stick or line?
            if (stickWidth >= 2f) {
                canvas.drawRect(stickX, highY, stickX + stickWidth, lowY, paint);
            } else {
                canvas.drawLine(stickX, highY, stickX, lowY, paint);
            }

            // next x
            stickX = stickX + stickSpacing + stickWidth;
        }
    }

    /**
     * @return the coloredStickStyle
     */
    public int getColoredStickStyle() {
        return coloredStickStyle;
    }

    /**
     * @param coloredStickStyle the coloredStickStyle to set
     */
    public void setColoredStickStyle(int coloredStickStyle) {

    }
}
