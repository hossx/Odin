package com.coinport.odin.util;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Hashtable;

/**
 * @author Ryan Tang
 *
 */
public final class EncodingHandler {
	private static final int BLACK = 0xff000000;
    private static final int WHITE = 0xffffffff;

	public static Bitmap createQRCode(String str,int widthAndHeight) throws WriterException {
//		Hashtable<EncodeHintType, String> hints = new Hashtable<>();
//        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight);
        int minX = matrix.getWidth(), minY = matrix.getHeight(), maxX = 0, maxY = 0;
		
        int h = matrix.getHeight();
        int w = matrix.getWidth();
        
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if (matrix.get(x, y)) {
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
				}
			}
		}
        
        int width = maxX - minX + 1;
        int height = maxY - minY + 1;
        int[] pixels = new int[width * height];
        
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                if (matrix.get(minX + x, minY + y))
                    pixels[y * width + x] = BLACK;
                else
                    pixels[y * width + x] = WHITE;
            }
        }
        
		Bitmap bitmap = Bitmap.createBitmap(width + 20, height + 20, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.WHITE);
		bitmap.setPixels(pixels, 0, width, 10, 10, width, height);
		return bitmap;
	}
}
