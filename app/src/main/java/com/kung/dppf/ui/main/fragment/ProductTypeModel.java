package com.kung.dppf.ui.main.fragment;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;

import com.kung.dppf.BR;
import com.kung.dppf.R;
import com.kung.dppf.data.KungRepository;
import com.kung.dppf.entity.ProductBean;
import com.kung.dppf.entity.ProductType;
import com.kung.dppf.entity.database.SqlManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.tatarka.bindingcollectionadapter2.ItemBinding;

public class ProductTypeModel extends BaseViewModel<KungRepository> {
    public ProductTypeModel(@NonNull Application application, KungRepository model) {
        super(application, model);
    }

    public ObservableField<String> mKey = new ObservableField<>("");
    //记录数量
    public ObservableField<String> mRecordCount = new ObservableField<>("0");
    public ObservableField<Integer> pageSizes = new ObservableField<>(100);
    public ObservableField<Integer> pageIndex = new ObservableField<>(1);

    //操作类型
    public String mOperationType = "";

    //给RecyclerView添加ObservableList
    public ObservableList<ProductTypeModelItem> observableList = new ObservableArrayList<>();
    //给RecyclerView添加ItemBinding
    public ItemBinding<ProductTypeModelItem> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_product_type);

    public UIChangeObservable uc = new UIChangeObservable();

    public class UIChangeObservable {
        //下拉刷新完成
        public SingleLiveEvent finishRefreshing = new SingleLiveEvent<>();
        //上拉加载完成
        public SingleLiveEvent finishLoadMore = new SingleLiveEvent<>();
        //跳转到添加页面
        public SingleLiveEvent eventToProductTypeAdd = new SingleLiveEvent();
        //修改
        public SingleLiveEvent<ProductType> eventDeleteItem = new SingleLiveEvent();
        public SingleLiveEvent<ProductType> eventModifyItem = new SingleLiveEvent();
        //选择
        public SingleLiveEvent<ProductType> eventSelectItem = new SingleLiveEvent();
        //输入关键字
        public SingleLiveEvent eventSearchKey = new SingleLiveEvent();
        //返回
        public SingleLiveEvent<Void> eventBack = new SingleLiveEvent();
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
        uc.eventToProductTypeAdd.call();
    });

    public BindingCommand onCommandQuery = new BindingCommand(() -> {
        //查询产品列表
        pageIndex.set(1);
        funQueryProductTypeList();
    });

    public BindingCommand onCommandSearchKey = new BindingCommand(() -> {
        //输入关键字
        uc.eventSearchKey.call();
    });

    public BindingCommand onCommandPageBack = new BindingCommand(() -> {
        //返回
        uc.eventBack.call();
    });

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //保存品类
    public void funSaveProductType(String typeName) {
        if (typeName.isEmpty()) {
            ToastUtils.showShort("产品类型不能为空");
            return;
        }
        ProductType productType = SqlManager.queryProductTypeBeanByName(typeName);
        if (productType != null) {
            ToastUtils.showShort("产品类型已存在");
            return;
        }
        productType = new ProductType();
        productType.setTypeName(typeName);
        productType.setUpdateTime(df.format(new Date()));
        if (SqlManager.insertOrReplaceProductTypeBean(productType) > 0) {
            ToastUtils.showShort("保存成功");
            resetData();
        } else {
            ToastUtils.showShort("保存失败");
        }
    }

    //查询产品列表
    public void funQueryProductTypeList() {
        //查询配方列表
        List<ProductType> list = SqlManager.queryAllProductTypeBean(mKey.get(), pageIndex.get(), pageSizes.get());
        if (list != null && list.size() > 0) {
            if (pageIndex.get() == 1) {
                observableList.clear();
            }
            pageIndex.set(pageIndex.get() + 1);
            for (ProductType bean : list) {
                bean.setIndex((observableList.size() + 1) + "");
                if (mOperationType.equals("select")) {
                    bean.setShowSelect(View.VISIBLE);
                    bean.setShowModify(View.GONE);
                } else {
                    bean.setShowSelect(View.GONE);
                    bean.setShowModify(View.VISIBLE);
                }
                ProductTypeModelItem item = new ProductTypeModelItem(this, bean);
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

    //重新查询
    public void resetData() {
        pageIndex.set(1);
        funQueryProductTypeList();
    }
}
