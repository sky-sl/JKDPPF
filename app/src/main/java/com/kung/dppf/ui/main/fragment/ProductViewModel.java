package com.kung.dppf.ui.main.fragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;

import com.kung.dppf.BR;
import com.kung.dppf.R;
import com.kung.dppf.data.KungRepository;
import com.kung.dppf.entity.ProductBean;
import com.kung.dppf.entity.database.SqlManager;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.tatarka.bindingcollectionadapter2.ItemBinding;

public class ProductViewModel extends BaseViewModel<KungRepository> {
    public ProductViewModel(@NonNull Application application, KungRepository model) {
        super(application, model);
    }

    public ObservableField<String> mKey = new ObservableField<>("");
    //记录数量
    public ObservableField<String> mRecordCount = new ObservableField<>("0");
    //总数量
    public ObservableField<String> mTotalCount = new ObservableField<>("0");

    public ObservableField<Integer> pageSizes = new ObservableField<>(100);
    public ObservableField<Integer> pageIndex = new ObservableField<>(1);

    //给RecyclerView添加ObservableList
    public ObservableList<ProductViewItem> observableList = new ObservableArrayList<>();
    //给RecyclerView添加ItemBinding
    public ItemBinding<ProductViewItem> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_product);

    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        //下拉刷新完成
        public SingleLiveEvent finishRefreshing = new SingleLiveEvent<>();
        //上拉加载完成
        public SingleLiveEvent finishLoadMore = new SingleLiveEvent<>();
        //跳转到添加页面
        public SingleLiveEvent eventToProductAdd = new SingleLiveEvent();
        //修改
        public SingleLiveEvent<ProductBean> eventDeleteItem = new SingleLiveEvent();
        public SingleLiveEvent<ProductBean> eventModifyItem = new SingleLiveEvent();
        //输入关键字
        public SingleLiveEvent eventSearchKey = new SingleLiveEvent();
        //返回
        public SingleLiveEvent<Void> eventBack = new SingleLiveEvent();
        //导入Excel
        public SingleLiveEvent<Void> eventImportExcel = new SingleLiveEvent();
    }

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
            uc.finishLoadMore.call();
        }
    });

    public BindingCommand onCommandAdd = new BindingCommand(() -> {
        //新增产品
        uc.eventToProductAdd.call();
    });

    //导入
    public BindingCommand onCommandImport = new BindingCommand(() -> {
        //导入
        uc.eventImportExcel.call();
    });

    public BindingCommand onCommandQuery = new BindingCommand(() -> {
        //查询产品列表
        pageIndex.set(1);
        funQueryProductList();
        funQueryProductCount();
    });

    public BindingCommand onCommandSearchKey = new BindingCommand(() -> {
        //输入关键字
        uc.eventSearchKey.call();
    });

    public BindingCommand onCommandPageBack = new BindingCommand(() -> {
        //返回
        uc.eventBack.call();
    });

    public void funQueryProductList() {
        //查询配方列表
        List<ProductBean> list = SqlManager.queryAllProductBean(mKey.get(), pageIndex.get(), pageSizes.get());
        if (list != null && list.size() > 0) {
            if (pageIndex.get() == 1) {
                observableList.clear();
            }
            pageIndex.set(pageIndex.get() + 1);
            for (ProductBean bean : list) {
                bean.setIndex((observableList.size() + 1) + "");
                ProductViewItem item = new ProductViewItem(this, bean);
                observableList.add(item);
            }
            mRecordCount.set(String.valueOf(observableList.size()));
        } else {
            if (pageIndex.get() == 1) {
                observableList.clear();
                mRecordCount.set("0");
            }
        }
    }

    public void funQueryProductCount() {
        //查询配方数量
        mTotalCount.set(String.valueOf(SqlManager.queryProductCount(mKey.get())));
    }
}
