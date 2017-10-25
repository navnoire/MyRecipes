package com.navnoire.myrecipes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by navnoire on 20/10/17 in MyRecipes project
 */

public class ImageDownloader<T> extends HandlerThread {
    private static final String TAG = "ImageDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;
    private static final int MESSAGE_DOWNLOAD_MAIN = 1;

    private boolean mHasQuit = false;
    private Handler mRequestHandler;
    private Handler mResponseHandler;
    private imageDownloadListener<T> mDownloadListener;
    private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap<>();
    private LruCache<String, Bitmap> mCache = new LruCache<>(100);

    public interface imageDownloadListener<T> {
        void onImageDownloaded (Bitmap image, T target );
    }

    public void setImageDownloadListener (imageDownloadListener listener) {
        mDownloadListener = listener;
    }


    public ImageDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }

    @Override
    protected void onLooperPrepared() {
        mRequestHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    T target = (T) msg.obj;
                    handleRequest(target);
                }
            }
        };
    }

    @Override
    public boolean quit() {
        Log.d(TAG, "quit: downloader thread finished");
        mHasQuit = true;
        return super.quit();
    }

    public void cleanQueue(){
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD_MAIN);
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
        mRequestMap.clear();
    }

    public void requestImageDownload(String url, T target) {
        if (url == null) {
            mRequestMap.remove(target);
        } else {
            mRequestMap.put(target, url);
        }

        mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget();
    }

    private void handleRequest(final T target) {
        //обработка поступившего запроса
        //собственно - загрузка изображений
        final String url = mRequestMap.get(target);
        if (url == null) {
            Log.d(TAG, "handleRequest: url null");
            return;
        }
        Bitmap imageBitmap = null;

        if(mCache.get(url) == null) {
            try {
                byte[] bitmapBytes = new RecipeFetcher().getUrlBytes(url);
                imageBitmap = BitmapFactory.decodeByteArray(bitmapBytes,0, bitmapBytes.length);
                mCache.put(url, imageBitmap);
                Log.d(TAG, "handleRequest: image downloaded");

            } catch (IOException ioe) {
                Log.e(TAG, "handleRequest: error downloading image", ioe );
            }
        } else {
            imageBitmap = mCache.get(url);
        }

        final Bitmap image = imageBitmap;

        //отправка ответа слушателю
        mResponseHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mRequestMap.get(target) != url || mHasQuit) return;
                mRequestMap.remove(target);
                mDownloadListener.onImageDownloaded(image, target);
                Log.d(TAG, "run: image");
            }
        });
    }
}
