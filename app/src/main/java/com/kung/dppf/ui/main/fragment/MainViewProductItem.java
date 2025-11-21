package com.kung.dppf.ui.main.fragment;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.kung.dppf.entity.ProductBean;

import me.goldze.mvvmhabit.base.ItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;

public class MainViewProductItem extends ItemViewModel<MainViewModel> {
    public ObservableField<ProductBean> entity = new ObservableField<>();
    public MainViewProductItem(@NonNull MainViewModel viewModel, ProductBean productBean) {
        super(viewModel);
        this.entity.set(productBean);
    }

    public BindingCommand itemClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            //这里可以通过一个标识,做出判断，已达到跳入不同界面的逻辑
            viewModel.setProductSelectState(entity.get(), true);
        }
    });
}
