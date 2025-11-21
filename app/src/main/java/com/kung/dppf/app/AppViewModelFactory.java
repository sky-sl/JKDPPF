package com.kung.dppf.app;

import android.annotation.SuppressLint;
import android.app.Application;

import com.kung.dppf.data.KungRepository;
import com.kung.dppf.ui.login.LoginViewModel;
import com.kung.dppf.ui.login.SplashViewModel;
import com.kung.dppf.ui.main.HomeViewModel;
import com.kung.dppf.ui.main.fragment.MainViewModel;
import com.kung.dppf.ui.main.fragment.ProductAddViewModel;
import com.kung.dppf.ui.main.fragment.ProductTypeModel;
import com.kung.dppf.ui.main.fragment.ProductViewModel;
import com.kung.dppf.ui.main.fragment.RecordDetailViewModel;
import com.kung.dppf.ui.main.fragment.RecordViewModel;
import com.kung.dppf.ui.main.fragment.SettingViewModel;
import com.kung.dppf.ui.network.NetWorkViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * Created by goldze on 2019/3/26.
 */
public class AppViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    @SuppressLint("StaticFieldLeak")
    private static volatile AppViewModelFactory INSTANCE;
    private final Application mApplication;
    private final KungRepository mRepository;

    public static AppViewModelFactory getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (AppViewModelFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppViewModelFactory(application, Injection.provideDemoRepository());
                }
            }
        }
        return INSTANCE;
    }

    @VisibleForTesting
    public static void destroyInstance() {
        INSTANCE = null;
    }

    private AppViewModelFactory(Application application, KungRepository repository) {
        this.mApplication = application;
        this.mRepository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(NetWorkViewModel.class)) {
            return (T) new NetWorkViewModel(mApplication, mRepository);
        } else if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(mApplication, mRepository);
        } else if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(mApplication, mRepository);
        } else if (modelClass.isAssignableFrom(SplashViewModel.class)) {
            return (T) new SplashViewModel(mApplication, mRepository);
        } else if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(mApplication, mRepository);
        } else if (modelClass.isAssignableFrom(SettingViewModel.class)) {
            return (T) new SettingViewModel(mApplication, mRepository);
        } else if (modelClass.isAssignableFrom(ProductViewModel.class)) {
            return (T) new ProductViewModel(mApplication, mRepository);
        } else if (modelClass.isAssignableFrom(ProductAddViewModel.class)) {
            return (T) new ProductAddViewModel(mApplication, mRepository);
        } else if (modelClass.isAssignableFrom(RecordViewModel.class)) {
            return (T) new RecordViewModel(mApplication, mRepository);
        } else if (modelClass.isAssignableFrom(ProductTypeModel.class)) {
            return (T) new ProductTypeModel(mApplication, mRepository);
        } else if (modelClass.isAssignableFrom(RecordDetailViewModel.class)) {
            return (T) new RecordDetailViewModel(mApplication, mRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
