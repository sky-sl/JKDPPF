package com.kung.dppf.ui.main.fragment;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.kung.dppf.BR;
import com.kung.dppf.R;
import com.kung.dppf.app.AppViewModelFactory;
import com.kung.dppf.databinding.FragSettingBinding;

import me.goldze.mvvmhabit.base.BaseFragment;

public class SettingFrag extends BaseFragment<FragSettingBinding, SettingViewModel> {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.frag_setting;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public SettingViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return ViewModelProviders.of(getActivity(), factory).get(SettingViewModel.class);
    }

    @Override
    public void initData() {
        super.initData();
        PackageManager manager = getActivity().getPackageManager();
        PackageInfo info;
        try {
            info = manager.getPackageInfo(getActivity().getPackageName(), 0);
            viewModel.versionName.set(info.versionName);
            viewModel.versionCode.set(info.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();

        viewModel.uc.eventToBack.observe(this, new Observer() {
            @Override
            public void onChanged(Object o) {
                //关闭页面，返回首页
                Navigation.findNavController(getView()).navigateUp();
            }
        });

        viewModel.uc.eventToLogout.observe(this, new Observer() {
            @Override
            public void onChanged(Object o) {
                //退出登录
                getActivity().finish();
            }
        });

        viewModel.uc.eventToManageProduct.observe(this, new Observer() {
            @Override
            public void onChanged(Object o) {
                //跳转到产品管理页面
                Navigation.findNavController(getView()).navigate(R.id.productFrag);
            }
        });

        viewModel.uc.eventToManageType.observe(this, new Observer() {
            @Override
            public void onChanged(Object o) {
                //跳转到产品类型管理页面
                Navigation.findNavController(getView()).navigate(R.id.productTypeFrag);
            }
        });
    }
}
