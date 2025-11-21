package com.kung.dppf.ui.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.lifecycle.ViewModelProviders;


import com.kung.dppf.BR;
import com.kung.dppf.R;
import com.kung.dppf.databinding.ActivitySplashBinding;
import com.kung.dppf.app.AppViewModelFactory;
import com.kung.dppf.ui.main.HomeActivity;

import me.goldze.mvvmhabit.base.BaseActivity;

public class SplashActivity extends BaseActivity<ActivitySplashBinding, SplashViewModel> {

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_splash;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public SplashViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getApplication());
        return ViewModelProviders.of(this, factory).get(SplashViewModel.class);
    }

    @Override
    public void initData() {
        super.initData();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                        startActivity(intent);
                        // 结束当前 Activity
                        SplashActivity.this.finish();
                    }
                });
            }
        }).start();
    }
}
