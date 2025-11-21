package com.kung.dppf.ui.main.fragment;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.kung.dppf.entity.ProductBean;

import me.goldze.mvvmhabit.base.ItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;

public class ProductViewItem extends ItemViewModel<ProductViewModel> {
    public ObservableField<ProductBean> entity = new ObservableField<>();
    public ProductViewItem(@NonNull ProductViewModel viewModel, ProductBean entity) {
        super(viewModel);
        this.entity.set(entity);
    }

    //删除事件
    public BindingCommand onItemDeleteClick = new BindingCommand(() -> {
        viewModel.uc.eventDeleteItem.setValue(entity.get());
    });
    //修改事件
    public BindingCommand onItemModifyClick = new BindingCommand(() -> {
        viewModel.uc.eventModifyItem.setValue(entity.get());
    });
}
