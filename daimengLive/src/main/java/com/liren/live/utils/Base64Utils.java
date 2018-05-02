package com.liren.live.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2018-03-07.
 */

public class Base64Utils {
    // 加密
    public static String getBase64(String str) {
        String result = "";
        if (str != null) {
            try {
                result = new String(Base64.encode(str.getBytes("utf-8"), Base64.NO_WRAP), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    // 解密
    public static String getFromBase64(String str) {
        String result = "";
        if (str != null) {
            try {
                result = new String(Base64.decode(str, Base64.NO_WRAP), "utf-8");
//                result = new String(Base64.decode(str.getBytes(), Base64.DEFAULT));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    /**
     * base64转为bitmap
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
