package com.kung.dppf.ui.main.fragment;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.kung.dppf.entity.ProductBean;
import com.kung.dppf.entity.ProductNutrition;

import me.goldze.mvvmhabit.base.ItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;

public class MainViewNutritionItem extends ItemViewModel<MainViewModel> {
    public ObservableField<ProductNutrition> entity = new ObservableField<>();
    public MainViewNutritionItem(@NonNull MainViewModel viewModel, ProductNutrition productBean) {
        super(viewModel);
        this.entity.set(productBean);
    }
}
