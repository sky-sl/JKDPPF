package com.kung.dppf.ui.main.fragment;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.kung.dppf.entity.ProductNutrition;

import me.goldze.mvvmhabit.base.ItemViewModel;

public class RecordDetailViewItem extends ItemViewModel<RecordDetailViewModel> {
    public ObservableField<ProductNutrition> entity = new ObservableField<>();
    public RecordDetailViewItem(@NonNull RecordDetailViewModel viewModel, ProductNutrition productBean) {
        super(viewModel);
        this.entity.set(productBean);
    }
}
