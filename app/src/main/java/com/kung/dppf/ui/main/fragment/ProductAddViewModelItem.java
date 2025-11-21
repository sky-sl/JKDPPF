package com.kung.dppf.ui.main.fragment;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.kung.dppf.entity.ProductNutrition;

import me.goldze.mvvmhabit.base.ItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;

public class ProductAddViewModelItem extends ItemViewModel<ProductAddViewModel> {
    public ObservableField<ProductNutrition> entity = new ObservableField<>();
    public ProductAddViewModelItem(@NonNull ProductAddViewModel viewModel, ProductNutrition entity) {
        super(viewModel);
        this.entity.set(entity);
    }

    //删除事件
    public BindingCommand onItemDeleteClick = new BindingCommand(() -> {
        viewModel.uc.eventDeleteNutrition.setValue(entity.get());
    });

    //修改事件
    public BindingCommand onItemModifyClick = new BindingCommand(() -> {
        viewModel.uc.eventEditNutrition.setValue(entity.get());
    });
}
