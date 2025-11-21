package com.kung.dppf.ui.main.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.kung.dppf.BR;
import com.kung.dppf.R;
import com.kung.dppf.app.AppViewModelFactory;
import com.kung.dppf.databinding.FragRecordDetailBinding;
import com.kung.dppf.entity.WeighRecord;

import me.goldze.mvvmhabit.base.BaseFragment;

public class RecordDetailFrag extends BaseFragment<FragRecordDetailBinding, RecordDetailViewModel> {
    private WeighRecord mWeighRecord;
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.frag_record_detail;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public RecordDetailViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return ViewModelProviders.of(getActivity(), factory).get(RecordDetailViewModel.class);
    }

    @Override
    public void initParam() {
        super.initParam();
        if (getArguments() != null) {
            mWeighRecord = (WeighRecord) getArguments().getSerializable("weighRecord");
        }
    }

    @Override
    public void initData() {
        super.initData();
        if (mWeighRecord != null) {
            viewModel.setWeighRecord(mWeighRecord);
        }
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.uc.eventToBack.observe(this, aVoid -> {
            Navigation.findNavController(getView()).navigateUp();
        });
    }
}
