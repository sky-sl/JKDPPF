package com.kung.dppf.ui.main.fragment;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.kung.dppf.entity.WeighRecord;

import me.goldze.mvvmhabit.base.ItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;

public class RecordViewItem extends ItemViewModel<RecordViewModel> {
    public ObservableField<WeighRecord> entity = new ObservableField<>();
    public RecordViewItem(@NonNull RecordViewModel viewModel, WeighRecord bean) {
        super(viewModel);
        entity.set(bean);
    }

    //删除记录
    public BindingCommand onCommandDelete = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            viewModel.uc.eventDeleteItem.setValue(entity.get());
        }
    });

    //打印记录
    public BindingCommand onCommandPrint = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            viewModel.uc.eventPrintItem.setValue(entity.get());
        }
    });
    //查看详情
    public BindingCommand onCommandDetail = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            viewModel.uc.eventDetailItem.setValue(entity.get());
        }
    });
}
