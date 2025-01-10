/*
 * Copyright (c) 2017.
 *
 * Anthony Ngure
 *
 * Email : anthonyngure25@gmail.com
 */

package ke.co.toshngure.basecode.networking;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import ke.co.toshngure.basecode.log.BeeLog;
import ke.co.toshngure.basecode.utils.BaseUtils;

/**
 * Created by Anthony Ngure on 27/07/2017.
 * Email : anthonyngure25@gmail.com.
 *
 */

public class ResponseHandler extends JsonHttpResponseHandler {

    public static final String TAG = ResponseHandler.class.getSimpleName();

    private ConnectionListener mListener;

    public ResponseHandler(ConnectionListener listener) {
        this.mListener = listener;
    }


    public void onStart() {
        super.onStart();
        if (BaseUtils.canConnect(mListener.getListenerContext())) {
            try {
                mListener.onConnectionStarted();
            } catch (Exception e) {
                BeeLog.e(TAG, e);
            }
        } else {
            try {
                mListener.onConnectionFailed(0, null);
            } catch (Exception e) {
                BeeLog.e(TAG, e);
            }
        }
    }

    @Override
    public void onProgress(long bytesWritten, long totalSize) {
        super.onProgress(bytesWritten, totalSize);
        try {
            /*if (bytesWritten == totalSize){
                long currentBytes = PrefUtils.getInstance().getLong(R.string.pref_bytes_count, 0);
                long newBytes = currentBytes+totalSize;
                PrefUtils.getInstance().writeLong(R.string.pref_bytes_count, newBytes);
            }*/
            double progress = (totalSize > 0) ? (bytesWritten * 1.0 / totalSize) * 100 : -1;
            mListener.onConnectionProgress((int) progress);
        } catch (Exception e) {
            BeeLog.e(TAG, e);
        }
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        super.onSuccess(statusCode, headers, response);
        try {
            mListener.onConnectionSuccess(response);
        } catch (Exception e) {
            BeeLog.e(TAG, e);
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        super.onFailure(statusCode, headers, responseString, throwable);
        try {
            BeeLog.e(TAG, responseString);
            mListener.onConnectionFailed(statusCode, null);
        } catch (Exception e) {
            BeeLog.e(TAG, e);
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        super.onFailure(statusCode, headers, throwable, errorResponse);
        try {
            mListener.onConnectionFailed(statusCode, errorResponse);
            BeeLog.e(TAG, String.valueOf(errorResponse));
        } catch (Exception e) {
            BeeLog.e(TAG, e);
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        super.onFailure(statusCode, headers, throwable, errorResponse);
        try {
            mListener.onConnectionFailed(statusCode, null);
            BeeLog.e(TAG, String.valueOf(errorResponse));
        } catch (Exception e) {
            BeeLog.e(TAG, e);
        }
    }
}
