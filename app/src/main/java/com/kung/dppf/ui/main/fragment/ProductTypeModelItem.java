package com.kung.dppf.ui.main.fragment;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.kung.dppf.entity.ProductType;

import me.goldze.mvvmhabit.base.ItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;

public class ProductTypeModelItem extends ItemViewModel<ProductTypeModel> {
    public ObservableField<ProductType> entity = new ObservableField<>();
    public ProductTypeModelItem(@NonNull ProductTypeModel viewModel, ProductType entity) {
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
    //选择事件
    public BindingCommand onItemSelectClick = new BindingCommand(() -> {
        viewModel.uc.eventSelectItem.setValue(entity.get());
    });
}
