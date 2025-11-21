package com.kung.dppf.ui.login;

import android.app.Application;

import androidx.annotation.NonNull;

import com.kung.dppf.data.KungRepository;

import me.goldze.mvvmhabit.base.BaseViewModel;


public class SplashViewModel extends BaseViewModel<KungRepository> {
    public SplashViewModel(@NonNull Application application) {
        super(application);
    }

    public SplashViewModel(@NonNull Application application, KungRepository model) {
        super(application, model);
    }
}
