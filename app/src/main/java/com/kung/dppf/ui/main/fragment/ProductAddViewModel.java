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
import com.kung.dppf.entity.ProductNutrition;
import com.kung.dppf.entity.database.SqlManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.tatarka.bindingcollectionadapter2.ItemBinding;

public class ProductAddViewModel extends BaseViewModel<KungRepository> {
    public ProductAddViewModel(@NonNull Application application, KungRepository model) {
        super(application, model);
    }

    //PLU编号
    public ObservableField<String> productCode = new ObservableField<>("");
    //产品名称
    public ObservableField<String> productName = new ObservableField<>("");
    //产品品类
    public ObservableField<String> productTypeName = new ObservableField<>("");
    //配料表
    public ObservableField<String> productIngredients = new ObservableField<>("");
    //保质期
    public ObservableField<String> productShelfLife = new ObservableField<>("");
    //贮存方法
    public ObservableField<String> productStorageMethod = new ObservableField<>("");
    //食用方法
    public ObservableField<String> productEdibleMethod = new ObservableField<>("");
    //注意事项
    public ObservableField<String> productAttention = new ObservableField<>("");
    //执行标准
    public ObservableField<String> productStandard = new ObservableField<>("");
    //生产许可证编号
    public ObservableField<String> productLicense = new ObservableField<>("");
    //生产厂家
    public ObservableField<String> productManufacturer = new ObservableField<>("");
    //地址
    public ObservableField<String> productAddress = new ObservableField<>("");
    //委托商
    public ObservableField<String> productEntrust = new ObservableField<>("");
    //委托商地址
    public ObservableField<String> productEntrustAddress = new ObservableField<>("");
    //产地
    public ObservableField<String> productOrigin = new ObservableField<>("");
    //联系方式
    public ObservableField<String> productContact = new ObservableField<>("");

    public ProductBean productBean;

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置日期格式
    List<ProductNutrition> nutritionList;

    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();
    public class UIChangeObservable {
        //返回
        public SingleLiveEvent eventToBack = new SingleLiveEvent<>();
        //保存
        public SingleLiveEvent eventSave = new SingleLiveEvent();
        //选择品类
        public SingleLiveEvent eventSelectType = new SingleLiveEvent();
        //添加营养成分
        public SingleLiveEvent eventAddNutrition = new SingleLiveEvent();
        //编辑营养成分
        public SingleLiveEvent<ProductNutrition> eventEditNutrition = new SingleLiveEvent();
        //删除营养成分
        public SingleLiveEvent<ProductNutrition> eventDeleteNutrition = new SingleLiveEvent();
    }

    //给RecyclerView添加ObservableList
    public ObservableList<ProductAddViewModelItem> observableList = new ObservableArrayList<>();
    //给RecyclerView添加ItemBinding
    public ItemBinding<ProductAddViewModelItem> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_nutrition);

    public BindingCommand onCommandSave = new BindingCommand(() -> {
        funSaveProductCheck();
    });

    public BindingCommand onCommandBack = new BindingCommand(() -> {
        uc.eventToBack.call();
    });

    //选择品类点击事件
    public BindingCommand onCommandSelectType = new BindingCommand(() -> {
        uc.eventSelectType.call();
    });

    public BindingCommand onCommandAddNutrition = new BindingCommand(() -> {
        //添加营养成分
        uc.eventAddNutrition.call();
    });

    //保存
    private void funSaveProductCheck() {
        //保存产品
        //判断是否为空
        if (productCode.get().isEmpty()) {
            ToastUtils.showShort("产品编号不能为空");
            return;
        }
        if (productName.get().isEmpty()) {
            ToastUtils.showShort("产品名称不能为空");
            return;
        }
        if (productTypeName.get().isEmpty()) {
            ToastUtils.showShort("产品品类不能为空");
            return;
        }

        if (productIngredients.get().isEmpty()) {
            ToastUtils.showShort("配料表不能为空");
            return;
        }
        if (productShelfLife.get().isEmpty()) {
            ToastUtils.showShort("保质期不能为空");
            return;
        }
        if (productStorageMethod.get().isEmpty()) {
            ToastUtils.showShort("贮存方法不能为空");
            return;
        }
        if (productEdibleMethod.get().isEmpty()) {
            ToastUtils.showShort("食用方法不能为空");
            return;
        }
        if (productAttention.get().isEmpty()) {
            ToastUtils.showShort("注意事项不能为空");
            return;
        }
        if (productStandard.get().isEmpty()) {
            ToastUtils.showShort("执行标准不能为空");
            return;
        }
        if (productLicense.get().isEmpty()) {
            ToastUtils.showShort("生产许可证编号不能为空");
            return;
        }
        if (productManufacturer.get().isEmpty()) {
            ToastUtils.showShort("生产厂家不能为空");
            return;
        }
        if (productAddress.get().isEmpty()) {
            ToastUtils.showShort("地址不能为空");
            return;
        }
        if (productEntrust.get().isEmpty()) {
            ToastUtils.showShort("委托商不能为空");
            return;
        }
        if (productEntrustAddress.get().isEmpty()) {
            ToastUtils.showShort("委托商地址不能为空");
            return;
        }
        if (productOrigin.get().isEmpty()) {
            ToastUtils.showShort("产地不能为空");
            return;
        }
        if (productContact.get().isEmpty()) {
            ToastUtils.showShort("联系方式不能为空");
            return;
        }
        //营养成分
        if (nutritionList == null || nutritionList.size() == 0) {
            ToastUtils.showShort("请添加营养成分");
            return;
        }

        //保存
        uc.eventSave.call();
    }

    public void funSaveProduct() {
        //保存产品
        if (productBean == null) {
            productBean = new ProductBean();
            productBean.set_id(null);
        }
        productBean.setProductCode(productCode.get());
        productBean.setProductName(productName.get());
        productBean.setTypeName(productTypeName.get());
        productBean.setIngredientContent(productIngredients.get());
        productBean.setShelfLife(productShelfLife.get());
        productBean.setStorageMethod(productStorageMethod.get());
        productBean.setEdibleMethod(productEdibleMethod.get());
        productBean.setPrecautions(productAttention.get());
        productBean.setStandard(productStandard.get());
        productBean.setProductionLicense(productLicense.get());
        productBean.setManufacturer(productManufacturer.get());
        productBean.setAddress(productAddress.get());
        productBean.setEntrust(productEntrust.get());
        productBean.setEntrustAddress(productEntrustAddress.get());
        productBean.setOrigin(productOrigin.get());
        productBean.setPhone(productContact.get());

        productBean.setUpdateTime(df.format(new Date()));
        if (SqlManager.insertOrReplaceProductBean(productBean) > 0) {
            //保存营养成分
            if (nutritionList != null && nutritionList.size() > 0) {
                for (ProductNutrition nutrition : nutritionList) {
                    nutrition.setProductCode(productBean.getProductCode());
                    SqlManager.insertOrReplaceNutritionBean(nutrition);
                }
            }
            ToastUtils.showShort("保存成功");
            uc.eventToBack.call();
        } else {
            ToastUtils.showShort("保存失败");
        }
    }

    //获取营养成分
    public void funGetNutrition() {
        observableList.clear();
        if (productBean != null) {
            nutritionList = SqlManager.queryNutritionBeanByProductCode(productBean.getProductCode());
            if (nutritionList == null) {
                nutritionList = new ArrayList<>();
            }
            for (ProductNutrition productNutrition : nutritionList) {
                ProductAddViewModelItem item = new ProductAddViewModelItem(this, productNutrition);
                observableList.add(item);
            }
        } else {
            nutritionList = new ArrayList<>();
        }
    }

    public boolean addNutritionToList(ProductNutrition productNutrition) {
        //先判断是否存在名称一样的营养成分
        if (nutritionList == null) {
            nutritionList = new ArrayList<>();
        }
        for (ProductNutrition nutrition : nutritionList) {
            if (nutrition.getName().equals(productNutrition.getName())) {
                ToastUtils.showShort("营养成分名称已存在");
                return false;
            }
        }
        nutritionList.add(productNutrition);
        //按照sort排序
        nutritionList.sort((o1, o2) -> o1.getSort() - o2.getSort());
        observableList.clear();
        for (ProductNutrition nutrition : nutritionList) {
            ProductAddViewModelItem item = new ProductAddViewModelItem(this, nutrition);
            observableList.add(item);
        }
        return true;
    }

    public void editNutritionToList(ProductNutrition productNutrition) {
        if (nutritionList == null) {
            nutritionList = new ArrayList<>();
        }
        for (ProductNutrition nutrition : nutritionList) {
            if (nutrition.getName().equals(productNutrition.getName())) {
                nutrition.set_id(productNutrition.get_id());
                nutrition.setContent(productNutrition.getContent());
                nutrition.setSort(productNutrition.getSort());
                break;
            }
        }
        //按照sort排序
        nutritionList.sort((o1, o2) -> o1.getSort() - o2.getSort());
        observableList.clear();
        for (ProductNutrition nutrition : nutritionList) {
            ProductAddViewModelItem item = new ProductAddViewModelItem(this, nutrition);
            observableList.add(item);
        }
    }

    //获取排序序号
    public int getSort() {
        int sort = 0;
        if (nutritionList == null) {
            return 1;
        }
        for (ProductNutrition nutrition : nutritionList) {
            if (nutrition.getSort() > sort) {
                sort = nutrition.getSort();
            }
        }
        return sort + 1;
    }

    //保存营养成分
    public void funSaveNutrition(ProductNutrition productNutrition) {
        if (productNutrition.get_id() == null) {
            productNutrition.set_id(null);
            productNutrition.setProductCode(productBean.getProductCode());
        }

        if (SqlManager.insertOrReplaceNutritionBean(productNutrition) > 0) {
            ToastUtils.showShort("保存成功");
            funGetNutrition();
        } else {
            ToastUtils.showShort("保存失败");
        }
    }
}
