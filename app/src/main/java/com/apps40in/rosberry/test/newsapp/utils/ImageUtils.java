package com.apps40in.rosberry.test.newsapp.utils;

/**
 * Created by sergeysorokin on 7/9/14.
 */
public class ImageUtils {

    static {
        System.loadLibrary("ImgToGray");
    }

    public static native int[] ImgToGray(int[] buf, int w, int h);

}
