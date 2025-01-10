/*
 * Copyright (c) 2017.
 *
 * Anthony Ngure
 *
 * Email : anthonyngure25@gmail.com
 */

package ke.co.toshngure.basecode.dataloading;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaychang.srv.OnLoadMoreListener;
import com.jaychang.srv.SimpleCell;
import com.jaychang.srv.SimpleRecyclerView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import ke.co.toshngure.basecode.R;
import ke.co.toshngure.basecode.database.BaseLoader;
import ke.co.toshngure.basecode.ptr.PtrClassicFrameLayout;
import ke.co.toshngure.basecode.ptr.PtrFrameLayout;
import ke.co.toshngure.basecode.ptr.PtrHandler;
import ke.co.toshngure.basecode.utils.BaseUtils;

/**
 * Created by Anthony Ngure on 04/06/2017.
 * Email : anthonyngure25@gmail.com.
 *
 */

public abstract class ModelListFragment<M, C extends SimpleCell<M, ?>> extends Fragment implements
        LoaderManager.LoaderCallbacks<List<C>>,
        OnLoadMoreListener, PtrHandler {

    protected static final String ARG_POSITION = "arg_position";
    private static final String SHARED_PREFS_NAME = "data_loading_prefs";
    private static final String TAG = ModelListFragment.class.getSimpleName();
    private static final String META = "meta";
    private static final String DATA = "data";
    private static final String AFTER = "after";
    private static final String CURSORS = "cursors";
    private static final String BEFORE = "before";
    private static final String RECENT = "recent";
    private static final String PER_PAGE = "perPage";
    private static SharedPreferences mSharedPreferences;
    protected int mPosition = 0;
    private SimpleRecyclerView mSimpleRecyclerView;
    private PtrClassicFrameLayout mPtrClassicFrameLayout;
    private View mFreshLoadView;
    private DataLoadingConfig mDataLoadingConfig;
    private boolean hasMoreToBottom = true;
    private boolean hasMoreToTop = true;
    private boolean isLoadingMore = false;
    private ModelCursor mTempModelCursors;

    public ModelListFragment() {
    }

    private SharedPreferences getSharedPreferences() {
        if (mSharedPreferences == null) {
            mSharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        }
        return mSharedPreferences;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("onCreate");
    }

    @Override
    public void onStart() {
        log("onStart");
        super.onStart();
        //Load cache data
        if (mDataLoadingConfig.isCacheEnabled()) {
            getActivity().getSupportLoaderManager().initLoader(mDataLoadingConfig.getLoaderId(), null, this);
        } else if (mDataLoadingConfig.isAutoRefreshEnabled()) {
            mTempModelCursors = new ModelCursor(0, 0);
            mPtrClassicFrameLayout.autoRefresh();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mDataLoadingConfig = getDataLoadingConfig();
        log("DataLoadingConfig = " + mDataLoadingConfig.toString());
        log("onCreateView");
        View view = inflater.inflate(R.layout.fragment_model_list, container, false);
        mSimpleRecyclerView = view.findViewById(R.id.baseapp_simpleRecyclerView);
        mPtrClassicFrameLayout = view.findViewById(R.id.ptrClassicFrameLayout);
        mFreshLoadView = view.findViewById(R.id.freshLoadView);
        setUpSimpleRecyclerView(mSimpleRecyclerView);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        log("onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        mPtrClassicFrameLayout.setPtrHandler(this);
        mSimpleRecyclerView.setAutoLoadMoreThreshold(mDataLoadingConfig.getLoadMoreThreshold());
        mSimpleRecyclerView.setOnLoadMoreListener(this);
        mPtrClassicFrameLayout.setHeaderView(View.inflate(getActivity(), R.layout.view_load_fresh, null));
    }

    protected void setUpSimpleRecyclerView(SimpleRecyclerView simpleRecyclerView) {
    }

    /*Override this method to read cached items*/
    protected List<M> onLoadCaches() {
        return new ArrayList<>();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<List<C>> onCreateLoader(int id, Bundle args) {
        log("onCreateLoader");
        return new BaseLoader<C>(getActivity()) {
            @Override
            public List<C> onLoad() {
                List<M> mList = onLoadCaches();
                List<C> cList = new ArrayList<>();
                for (M item : mList) {
                    cList.add(onCreateCell(item));
                }
                return cList;
            }
        };
    }

    protected abstract C onCreateCell(M item);

    protected abstract DataLoadingConfig getDataLoadingConfig();

    protected abstract Class<M> getModelClass();

    protected AsyncHttpClient getClient() {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<C>> loader, List<C> data) {
        log("onLoadFinished");
        log("Data Size = " + data.size());

        mPtrClassicFrameLayout.setVisibility(View.VISIBLE);
        mFreshLoadView.setVisibility(View.GONE);

        mSimpleRecyclerView.removeAllCells();
        mSimpleRecyclerView.addCells(data);

        if (mSimpleRecyclerView.isEmpty()) {
            //mSimpleRecyclerView.showEmptyStateView();
        } else {
            //mSimpleRecyclerView.hideEmptyStateView();
        }

        if (!mDataLoadingConfig.isConnectionEnabled()) {
            mPtrClassicFrameLayout.setVisibility(View.VISIBLE);
            mFreshLoadView.setVisibility(View.GONE);
            if (mSimpleRecyclerView.isEmpty()) {
                mSimpleRecyclerView.showEmptyStateView();
            } else {
                mSimpleRecyclerView.hideEmptyStateView();
            }
        }

        //Auto refresh if refresh is enabled
        if (mDataLoadingConfig.isAutoRefreshEnabled()) {
            mPtrClassicFrameLayout.autoRefresh();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<C>> loader) {
        log("onLoaderReset");
    }

    @Override
    public void onLoadMore(@NonNull SimpleRecyclerView simpleRecyclerView) {
        //Should not load more when empty and when loading fresh
        if (!mPtrClassicFrameLayout.isRefreshing()
                && !mSimpleRecyclerView.isEmpty()
                && hasMoreToBottom
                && !isLoadingMore
                && mDataLoadingConfig.isLoadingMoreEnabled()) {
            log("onLoadMore");
            mSimpleRecyclerView.setLoadingMore(true);
            isLoadingMore = true;
            connect();
        } else {
            log("LoadMore Halted");
            log("mPtrClassicFrameLayout.isRefreshing() " + mPtrClassicFrameLayout.isRefreshing());
            log("mSimpleRecyclerView.isEmpty() " + mSimpleRecyclerView.isEmpty());
            log("hasMoreToBottom " + hasMoreToBottom);
            log("isLoadingMore " + isLoadingMore);
            log("mDataLoadingConfig.isLoadingMoreEnabled() " + mDataLoadingConfig.isLoadingMoreEnabled());
        }
    }

    public void connect() {
        log("connect");
        ModelCursor modelCursor = getModelCursor();

        RequestParams requestParams = new RequestParams();
        requestParams.put(AFTER, modelCursor.getAfter());
        requestParams.put(BEFORE, modelCursor.getBefore());
        requestParams.put(RECENT, !isLoadingMore);
        requestParams.put(PER_PAGE, mDataLoadingConfig.getPerPage());
        log("Params : " + requestParams.toString());
        log("Url : " + mDataLoadingConfig.getUrl());
        getClient().get(getActivity(), mDataLoadingConfig.getUrl(), requestParams, new ResponseHandler());
    }

    @SuppressLint("StaticFieldLeak")
    private void parseData(JSONObject response) {
        log("parseData");
        new AsyncTask<JSONObject, Void, List<C>>() {

            @Override
            protected List<C> doInBackground(JSONObject... params) {
                log("doInBackground");
                List<C> cList = new ArrayList<C>();
                try {

                    JSONObject meta = params[0].getJSONObject(META);
                    long after = meta.getJSONObject(CURSORS).getLong(AFTER);
                    long before = meta.getJSONObject(CURSORS).getLong(BEFORE);
                    ModelCursor modelCursor = new ModelCursor();
                    modelCursor.setAfter(after);
                    modelCursor.setBefore(before);
                    updateModelCursor(modelCursor);

                    JSONArray data = params[0].getJSONArray(DATA);

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject itemObject = data.getJSONObject(i);
                        M item = BaseUtils.getSafeGson().fromJson(itemObject.toString(), getModelClass());
                        onSaveItem(item);
                        cList.add(onCreateCell(item));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return cList;
            }

            @Override
            protected void onPostExecute(List<C> cs) {
                super.onPostExecute(cs);
                log("onPostExecute");
                log("Data Size : " + cs.size());

                mFreshLoadView.setVisibility(View.GONE);
                mPtrClassicFrameLayout.setVisibility(View.VISIBLE);

                if (mPtrClassicFrameLayout.isRefreshing() || mPtrClassicFrameLayout.isAutoRefresh()) {
                    Collections.reverse(cs);
                    mSimpleRecyclerView.addCells(0, cs);
                    mSimpleRecyclerView.smoothScrollToPosition(0);
                    mPtrClassicFrameLayout.refreshComplete();
                    hasMoreToTop = !(cs.size() == 0);
                } else {
                    mSimpleRecyclerView.addCells(cs);
                    mSimpleRecyclerView.setLoadingMore(false);
                    isLoadingMore = false;
                    hasMoreToBottom = !(cs.size() == 0);
                }

                //In case of cursor problems
                if (mSimpleRecyclerView.isEmpty() && mDataLoadingConfig.isCacheEnabled()) {
                    resetCursors();
                }

                // TODO: 28/07/2017 Change the shown empty view
                if (mSimpleRecyclerView.isEmpty()) {
                    //mSimpleRecyclerView.showEmptyStateView();
                } else {
                    //mSimpleRecyclerView.hideEmptyStateView();
                }
            }
        }.execute(response);
    }

    //Called in the background after the item has be parsed
    protected void onSaveItem(M item) {
        log("onSaveItem");

    }

    private String getCursorKeyPrefix() {
        log("getCursorKeyPrefix");
        return getModelClass().getSimpleName().toLowerCase() + "_" + addUniqueCacheKey();
    }

    private void updateModelCursor(ModelCursor modelCursor) {
        log("updateModelCursor");
        if (mDataLoadingConfig.isCacheEnabled()) {
            getSharedPreferences()
                    .edit()
                    .putLong(getCursorKeyPrefix() + "_" + AFTER, modelCursor.getAfter())
                    .putLong(getCursorKeyPrefix() + "_" + BEFORE, modelCursor.getBefore())
                    .apply();
            mSharedPreferences = null;
        } else {
            mTempModelCursors = modelCursor;
        }
    }

    private ModelCursor getModelCursor() {
        log("getModelCursor");
        ModelCursor modelCursor = new ModelCursor();
        if (mDataLoadingConfig.isCacheEnabled()) {
            modelCursor.setAfter(getSharedPreferences().getLong(getCursorKeyPrefix() + "_" + AFTER, 0));
            modelCursor.setBefore(getSharedPreferences().getLong(getCursorKeyPrefix() + "_" + BEFORE, 0));
        } else {
            if (mTempModelCursors == null) {
                mTempModelCursors = new ModelCursor();
            }
            modelCursor = mTempModelCursors;
        }
        return modelCursor;
    }


    protected int addUniqueCacheKey() {
        return 0;
    }

    protected void log(String msg) {
        if ((mDataLoadingConfig != null) && mDataLoadingConfig.isDebugEnabled()) {
            Log.v(TAG, String.valueOf(msg));
        }
    }

    private void showErrorAlertDialog(String message) {
        if (!mDataLoadingConfig.isDebugEnabled()){
            return;
        }
        if (mDataLoadingConfig.isDebugEnabled()) {
            try {
                new AlertDialog.Builder(getActivity())
                        .setMessage(message)
                        .create().show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public SimpleRecyclerView getSimpleRecyclerView() {
        return mSimpleRecyclerView;
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return ((mSimpleRecyclerView.isEmpty() || !mSimpleRecyclerView.canScrollVertically(-1))
                && !isLoadingMore
                && mDataLoadingConfig.isRefreshEnabled());
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        log("onRefreshBegin");
        if (hasMoreToTop) {
            connect();
        } else {
            log("Refresh Halted");
            if (mDataLoadingConfig.isDebugEnabled()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPtrClassicFrameLayout.refreshComplete();
                    }
                }, 3000);
            } else {
                mPtrClassicFrameLayout.refreshComplete();
            }
        }
    }

    protected void resetCursors() {
        log("resetCursors");
        ModelCursor modelCursor = new ModelCursor(0, 0);
        updateModelCursor(modelCursor);
    }

    protected void onError(int statusCode, String responseString) {
        showErrorAlertDialog(String.valueOf(responseString));
    }

    protected void onError(int statusCode, JSONArray errorResponse) {
        showErrorAlertDialog(String.valueOf(errorResponse));
    }

    protected void onError(int statusCode, JSONObject errorResponse) {
        showErrorAlertDialog(String.valueOf(errorResponse));
    }

    private final class ResponseHandler extends JsonHttpResponseHandler {
        @Override
        public void onStart() {
            super.onStart();
            if (mSimpleRecyclerView.isEmpty()) {
                mFreshLoadView.setVisibility(View.VISIBLE);
                mPtrClassicFrameLayout.setVisibility(View.GONE);
            } else {
                mFreshLoadView.setVisibility(View.GONE);
                mPtrClassicFrameLayout.setVisibility(View.VISIBLE);
            }
            //mSimpleRecyclerView.hideEmptyStateView();
        }

        @Override
        public void onProgress(long bytesWritten, long totalSize) {
            super.onProgress(bytesWritten, totalSize);
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            log("onSuccess, "+String.valueOf(response));
            parseData(response);
        }

        @Override
        public void onRetry(int retryNo) {
            super.onRetry(retryNo);
            log("onRetry");
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            super.onFailure(statusCode, headers, responseString, throwable);
            log("***********************************************************************************");
            log("onFailure");
            log("StatusCode : " + statusCode);
            log("ResponseString : " + responseString);
            log("***********************************************************************************");
            //showErrorAlertDialog(String.valueOf(responseString));
            onError(statusCode, responseString);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            log("***********************************************************************************");
            log("onFailure");
            log("StatusCode : " + statusCode);
            log("ResponseString : " + errorResponse);
            log("***********************************************************************************");
            //showErrorAlertDialog(String.valueOf(errorResponse));
            onError(statusCode, errorResponse);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            log("***********************************************************************************");
            log("onFailure");
            log("StatusCode : " + statusCode);
            log("ResponseString : " + errorResponse);
            log("***********************************************************************************");
            showErrorAlertDialog(String.valueOf(errorResponse));
            onError(statusCode, errorResponse);
        }

    }

}
