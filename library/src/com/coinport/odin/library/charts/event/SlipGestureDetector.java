/*
 * SlipGestureDetector.java
 * Android-Charts
 *
 * Created by limc on 2014.
 *
 * Copyright 2011 limc.cn All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.coinport.odin.library.charts.event;

import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;

/**
 * <p>
 * en
 * </p>
 * <p>
 * jp
 * </p>
 * <p>
 * cn
 * </p>
 * 
 * @author limc
 * @version v1.0 2014/06/23 16:48:07
 * 
 */
public class SlipGestureDetector<T extends ISlipable> extends ZoomGestureDetector<IZoomable> {
    private PointF singlePoint;
    private float MIN_MOVE_DISTANCE = 10;

	private OnSlipGestureListener onSlipGestureListener;

	public SlipGestureDetector(ISlipable slipable){
		super(slipable);
		if (slipable != null) {
			this.onSlipGestureListener = slipable.getOnSlipGestureListener();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @param event
	 * 
	 * @return
	 * 
	 * @see
	 * cn.limc.androidcharts.event.IGestureDetector#onTouchEvent(android.view
	 * .MotionEvent)
	 */
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		// 设置拖拉模式
		case MotionEvent.ACTION_DOWN:
            singlePoint = new PointF(event.getX(0), event.getY(0));
			break;
		case MotionEvent.ACTION_UP:
            singlePoint = null;
			break;
		case MotionEvent.ACTION_POINTER_UP:
		case MotionEvent.ACTION_POINTER_DOWN:
			olddistance = calcDistance(event);
			if (olddistance > MIN_DISTANCE) {
				touchMode = TOUCH_MODE_MULTI;
			}
			return true;
			//break;
		case MotionEvent.ACTION_MOVE:
			if (touchMode == TOUCH_MODE_MULTI) {
				newdistance = calcDistance(event);
                float distance = newdistance - olddistance;
				if (Math.abs(distance) > MIN_DISTANCE) {
                    if (onZoomGestureListener != null) {
                        if (distance > 0) {
                            onZoomGestureListener.onZoomIn((IZoomable)instance,event);
                        } else {
                            onZoomGestureListener.onZoomOut((IZoomable)instance,event);
                        }
                    }
                    olddistance = newdistance;
                    return true;
				}
			} else {
                float distance = singlePoint.x - event.getX(0);
                if (Math.abs(distance) > MIN_MOVE_DISTANCE) {
                    if (distance > 0f && onSlipGestureListener != null) {
                        onSlipGestureListener.onMoveRight((ISlipable) instance, event, Math.abs(distance));
                    } else if (distance < 0f && onSlipGestureListener != null) {
                        onSlipGestureListener.onMoveLeft((ISlipable) instance, event, Math.abs(distance));
                    }
                    singlePoint = new PointF(event.getX(0), event.getY(0));
                }
            }
			break;
		}
		return super.onTouchEvent(event);
	}
}
