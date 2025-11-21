package com.kung.dppf.ui.main.fragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;

import com.kung.dppf.BR;
import com.kung.dppf.R;
import com.kung.dppf.data.KungRepository;
import com.kung.dppf.entity.WeighRecord;
import com.kung.dppf.entity.database.SqlManager;

import java.text.SimpleDateFormat;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.StringUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.tatarka.bindingcollectionadapter2.ItemBinding;

public class RecordViewModel extends BaseViewModel<KungRepository> {
    public RecordViewModel(@NonNull Application application, KungRepository model) {
        super(application, model);
    }

    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        public SingleLiveEvent eventDateStart = new SingleLiveEvent();
        public SingleLiveEvent eventDateEnd = new SingleLiveEvent();
        public SingleLiveEvent eventKeyValue = new SingleLiveEvent();
        //下拉刷新完成
        public SingleLiveEvent finishRefreshing = new SingleLiveEvent<>();
        //上拉加载完成
        public SingleLiveEvent finishLoadmore = new SingleLiveEvent<>();
        //删除数据
        public SingleLiveEvent<WeighRecord> eventDeleteItem = new SingleLiveEvent();
        //打印数据
        public SingleLiveEvent<WeighRecord> eventPrintItem = new SingleLiveEvent();
        //查看详情
        public SingleLiveEvent<WeighRecord> eventDetailItem = new SingleLiveEvent();
        //导出数据
        public SingleLiveEvent eventExportData = new SingleLiveEvent();
    }


    public ObservableField<String> mStartDate = new ObservableField<>();
    public ObservableField<String> mEndDate = new ObservableField<>();
    //配方关键字
    public ObservableField<String> mKey = new ObservableField<>();
    public ObservableField<Integer> pageSizes = new ObservableField<>(50);
    public ObservableField<Integer> pageIndex = new ObservableField<>(1);
    //记录条数
    public ObservableField<String> mCurrentRecordNum = new ObservableField<>("0");
    //记录总重量
    public ObservableField<String> mCurrentTotalWeight = new ObservableField<>("0.00");

    //总重量
    public ObservableField<String> mTotalWeight = new ObservableField<>("0.00");
    //记录条数
    public ObservableField<String> mTotalRecordNum = new ObservableField<>("0");
    public ObservableField<String> mTotalProductNum = new ObservableField<>("0");
    //总毛重
    public ObservableField<String> mTotalGrossWeight = new ObservableField<>("0.00");

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  //设置日期格式

    //给RecyclerView添加ObservableList
    public ObservableList<RecordViewItem> observableList = new ObservableArrayList<>();
    //给RecyclerView添加ItemBinding
    public ItemBinding<RecordViewItem> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_weigh_record);

    @Override
    public void onCreate() {
        super.onCreate();
        //获取当前日期
        mStartDate.set(dateFormat.format(new java.util.Date()));
        mEndDate.set(dateFormat.format(new java.util.Date()));
    }

    public BindingCommand onCommandDateStart = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            uc.eventDateStart.call();
        }
    });

    public BindingCommand onCommandDateEnd = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            uc.eventDateEnd.call();
        }
    });

    ///查询
    public BindingCommand onCommandSearch = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            refreshData();
        }
    });

    public BindingCommand onCommandSearchKey = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            uc.eventKeyValue.call();
        }
    });

    //下拉刷新
    public BindingCommand onRefreshCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            pageIndex.set(1);
            uc.finishRefreshing.call();
        }
    });
    //上拉加载
    public BindingCommand onLoadMoreCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            uc.finishLoadmore.call();
        }
    });

    ///导出数据
    public BindingCommand onExportData = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            //判断是否有数据
            if (observableList.size() > 0) {
                uc.eventExportData.call();
            } else {
                ToastUtils.showShort("没有数据可以导出");
            }
        }
    });

    public void refreshData() {
        pageIndex.set(1);
        funLoadWeighRecords();
        funLoadWeighRecordCount();
    }

    public void funLoadWeighRecords() {
        String beginDate;
        String endDate;
        if (StringUtils.isTrimEmpty(mStartDate.get())) {
            beginDate = "";
        } else {
            beginDate = mStartDate.get() + " 00:00:00";
        }

        if (StringUtils.isTrimEmpty(mEndDate.get())) {
            endDate = "";
        } else {
            endDate = mEndDate.get() + " 23:59:59";
        }

        List<WeighRecord> weighRecordList = SqlManager.queryWeighRecords(beginDate, endDate, mKey.get(), pageIndex.get(),
                pageSizes.get());
        if (weighRecordList != null && weighRecordList.size() > 0) {
            double totalWeight = 0;
            if (pageIndex.get() == 1) {
                observableList.clear();
            } else {
                totalWeight = Double.parseDouble(mCurrentTotalWeight.get());
            }
            pageIndex.set(pageIndex.get() + 1);
            for (WeighRecord bean : weighRecordList) {
                bean.setIndex((observableList.size() + 1) + "");
//                totalWeight += Double.parseDouble(bean.getWeight());
                RecordViewItem item = new RecordViewItem(this, bean);
                observableList.add(item);
            }
            mCurrentRecordNum.set(String.valueOf(observableList.size()));
            mCurrentTotalWeight.set(String.format("%.2f", totalWeight));
        } else {
            if (pageIndex.get() == 1) {
                observableList.clear();
                mCurrentRecordNum.set("0");
                mCurrentTotalWeight.set("0.00");
            }
        }
    }

    private void funLoadWeighRecordCount() {
        String beginDate;
        String endDate;
        if (StringUtils.isTrimEmpty(mStartDate.get())) {
            beginDate = "";
        } else {
            beginDate = mStartDate.get() + " 00:00:00";
        }

        if (StringUtils.isTrimEmpty(mEndDate.get())) {
            endDate = "";
        } else {
            endDate = mEndDate.get() + " 23:59:59";
        }
        long count = SqlManager.queryWeighRecordCount(beginDate, endDate, mKey.get());
        mTotalRecordNum.set(String.valueOf(count));
        List<Double> weights = SqlManager.queryWeighRecordTotalWeight(beginDate, endDate, mKey.get());
        mTotalWeight.set(String.format("%.2f", weights.get(0)));
//        mTotalGrossWeight.set(String.format("%.2f", weights.get(1)));
//        mTotalProductNum.set(weights.get(2).intValue() + "");
    }
}
