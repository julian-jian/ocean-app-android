package com.sky.lamp.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.sky.lamp.MyApplication;
import com.sky.lamp.R;
import com.sky.lamp.http.AppService;

import java.io.File;

/**
 * Created by sky on 2015/7/24.
 */
public class ImageLoadUtils {
    private static DisplayImageOptions options;
    private static DisplayImageOptions optionsWithNoLoading;
    private static DisplayImageOptions headerOptions;
    private static DisplayImageOptions infoOptions;
    public static ImageLoader imageLoader = ImageLoader.getInstance();
    private static DisplayImageOptions headerSquareOptions;

    /**
     * @param path
     * @param imageView
     */
    public static void loadImageFromSdCard(String path, ImageView imageView) {
        if (TAStringUtils.isNotEmpty(path)) {
            if (path.startsWith("file:/") && !path.startsWith("file:///")) {
                //uri.toString();
                path = path.replace("file:/", "file:///");
            } else {// mnt/sdcard
                File file = new File(path);
                if (file.exists()) {
                    path = "file://" + path;
                }
            }
        }
        loadImage(path, imageView);
    }

    /**
     * @param path
     * @param imageView
     */
    public static void loadImageFromSdCard(String path, ImageView imageView, ImageLoadingListener imageLoadingListener) {
        if (TAStringUtils.isNotEmpty(path)) {
            if (path.startsWith("file:/") && !path.startsWith("file:///")) {
                //uri.toString();
                path = path.replace("file:/", "file:///");
            } else {// mnt/sdcard
                File file = new File(path);
                if (file.exists()) {
                    path = "file://" + path;
                }
            }
        }
        loadImage(path, imageView, imageLoadingListener);
    }

    public static void loadImage(String uri, ImageView imageView) {

        loadImage(uri, imageView, getDisplayImageOptions(), null);
    }

    public static void loadHeaderImage(String uri, ImageView imageView) {
        loadImage(uri, imageView, getDisplayHeaderImageOptions(), null);
    }

    public static void loadInfoImage(String uri, ImageView imageView) {

        loadImage(uri, imageView, getDisplayInfoOptions(), null);
    }

    public static void loadHeaderSquareImage(String uri, ImageView imageView) {

        loadImage(uri, imageView, getSquareHeaderImageOptions(), null);
    }


    /**
     * @param uri
     * @param imageView
     */
    public static void loadImage(String uri, ImageView imageView, ImageLoadingListener listener) {

        loadImage(uri, imageView, getDisplayImageOptions(), listener);
    }

    public static void loadImage(String uri, ImageView imageView, DisplayImageOptions options) {
        loadImage(uri, imageView, options, null);
    }

    public static void loadImage(String uri, ImageView imageView, DisplayImageOptions options, ImageLoadingListener listener) {

        if (uri.startsWith("file") || uri.startsWith("http")) {
            imageLoader.displayImage(uri, imageView, options, listener);
        } else {
            imageLoader.displayImage(AppService.BASETESTURL + uri, imageView, options, listener);
        }

    }

    public static void loadImageProgress(String uri, ImageView imageView, ImageLoadingListener imageLoadingListener, ImageLoadingProgressListener imageLoadingProgressListener) {
        loadImageProgress(uri, imageView, getDisplayImageOptions(), imageLoadingListener, imageLoadingProgressListener);
    }

    public static void loadImageProgress(String uri, ImageView imageView, DisplayImageOptions options, ImageLoadingListener listener, ImageLoadingProgressListener imageLoadingProgressListener) {
        imageLoader.displayImage(uri, imageView, options, listener, imageLoadingProgressListener);
    }

    public static DisplayImageOptions getDisplayImageOptions() {
        if (options == null) {
            options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.mbm_default_photo).showImageOnFail(R.drawable.mbm_default_photo).showImageForEmptyUri(R.drawable.mbm_default_photo).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
        }
        return options;
    }

    public static DisplayImageOptions getDisplayImageOptionsNoLoadingImage() {
        if (optionsWithNoLoading == null) {
            optionsWithNoLoading = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.mbm_default_photo).showImageForEmptyUri(R.drawable.mbm_default_photo).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
        }
        return optionsWithNoLoading;
    }

    public static DisplayImageOptions getDisplayHeaderImageOptions() {
        if (headerOptions == null) {
            headerOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.portrait_default).showImageOnFail(R.drawable.portrait_default).showImageForEmptyUri(R.drawable.portrait_default).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
        }
        return headerOptions;
    }

    public static DisplayImageOptions getDisplayInfoOptions() {
        ColorDrawable cd = new ColorDrawable(MyApplication.getInstance().getResources().getColor(R.color.empty_image));
        if (infoOptions == null) {
            infoOptions = new DisplayImageOptions.Builder()
                    .showImageOnLoading(cd)
                    .showImageOnFail(cd)
                    .showImageForEmptyUri(cd)
                    .cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
        }
        return infoOptions;
    }


    /**
     * 方形头像
     *
     * @return
     */
    public static DisplayImageOptions getSquareHeaderImageOptions() {
        if (headerSquareOptions == null) {
            headerSquareOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.portrait_default).showImageOnFail(R.drawable.portrait_default).showImageForEmptyUri(R.drawable.portrait_default).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
        }
        return headerSquareOptions;
    }

    public static DisplayImageOptions getDisplayWithOutDefaultImageOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
        return options;
    }

    public String getRealUrl(String srcUrl) {
        String string = "";
        return string;
    }


}
