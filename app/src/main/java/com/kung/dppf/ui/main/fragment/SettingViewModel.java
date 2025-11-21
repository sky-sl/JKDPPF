package com.kung.dppf.ui.main.fragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.kung.dppf.R;
import com.kung.dppf.data.KungRepository;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;

public class SettingViewModel extends BaseViewModel<KungRepository> {
    public SettingViewModel(@NonNull Application application, KungRepository model) {
        super(application, model);
    }

    public ObservableField<String> versionName = new ObservableField<>("");
    public ObservableField<Integer> versionCode = new ObservableField<>(0);
    public ObservableField<String> forgName = new ObservableField<>(getApplication().getString(R.string.forg_name));
    public ObservableField<String> equipmentNo = new ObservableField<>("");

    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();
    public class UIChangeObservable {
        //返回
        public SingleLiveEvent eventToBack = new SingleLiveEvent<>();
        //退出登录
        public SingleLiveEvent eventToLogout = new SingleLiveEvent<>();
        //产品管理页面
        public SingleLiveEvent eventToManageProduct = new SingleLiveEvent<>();
        //产品类型管理页面
        public SingleLiveEvent eventToManageType = new SingleLiveEvent<>();
    }

    public BindingCommand onCommandManageProduct = new BindingCommand(() -> {
        uc.eventToManageProduct.call();
    });

    public BindingCommand onCommandManageType = new BindingCommand(() -> {
        uc.eventToManageType.call();
    });

    public BindingCommand onCommandExitApp = new BindingCommand(() -> {
        uc.eventToLogout.call();
    });

    public BindingCommand onCommandBack = new BindingCommand(() -> {
        uc.eventToBack.call();
    });
}
