/*
 * Copyright (c) 2017.
 *
 * Anthony Ngure
 *
 * Email : anthonyngure25@gmail.com
 */

package ke.co.toshngure.basecode.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;

import ke.co.toshngure.basecode.R;
import ke.co.toshngure.basecode.log.BeeLog;
import ke.co.toshngure.basecode.utils.BaseUtils;

/**
 * Created by Anthony Ngure on 18/09/2017.
 * Email : anthonyngure25@gmail.com.
 */

public class BaseAppActivity extends AppCompatActivity {


    protected static int sessionDepth = 0;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    protected BaseProgressDialog mProgressDialog;
    private Toolbar toolbar;


    public BaseAppActivity() {
    }

    @Override
    protected void onStart() {
        super.onStart();
        sessionDepth++;
    }


    @Override
    protected void onPause() {
        try {
            if ((mProgressDialog != null) && mProgressDialog.isShowing()) {
                hideProgressDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (sessionDepth > 0) {
            sessionDepth--;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        setupToolbar();
        setUpStatusBarColor();
    }

    protected void setUpStatusBarColor() {
        StatusBarUtil.setColor(this, BaseUtils.getColorAttr(this, R.attr.colorPrimaryDark), 1);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.slide_left_in, R.anim.hold);
    }


    public void startNewTaskActivity(Intent intent){
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public AppCompatActivity getThis() {
        return this;
    }

    protected void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitleTextColor(Color.WHITE);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setElevation(0);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_base, menu);
        BaseUtils.tintMenu(this, menu, Color.WHITE);
        return super.onCreateOptionsMenu(menu);
    }

    public void exitActivity() {
        super.onBackPressed();
        overridePendingTransition(R.anim.hold, R.anim.slide_right_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                exitActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void hideKeyboardFrom(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void showProgressDialog() {
        showProgressDialog(R.string.message_waiting);
    }

    public void showProgressDialog(@StringRes int message) {
        showProgressDialog(getString(message));
    }

    public void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new BaseProgressDialog(this)
                    .setMessage(message)
                    .setCancelable(false);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if ((mProgressDialog != null) && (mProgressDialog.isShowing())) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public void toast(String message) {
        try {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toastDebug(String msg) {
        if (BeeLog.DEBUG) {
            toast(msg);
        }
    }

    public void toast(@StringRes int string) {
        toast(getString(string));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.hold, R.anim.slide_right_out);
    }
}
