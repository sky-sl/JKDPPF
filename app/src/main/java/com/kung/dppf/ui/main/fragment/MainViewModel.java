package com.kung.dppf.ui.main.fragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;

import com.alibaba.fastjson.JSON;
import com.kung.dppf.BR;
import com.kung.dppf.R;
import com.kung.dppf.data.KungRepository;
import com.kung.dppf.entity.ProductBean;
import com.kung.dppf.entity.ProductNutrition;
import com.kung.dppf.entity.ProductType;
import com.kung.dppf.entity.WeighRecord;
import com.kung.dppf.entity.database.SqlManager;
import com.kung.dppf.utils.CommonUtils;
import com.kung.dppf.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.StringUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.goldze.mvvmhabit.utils.Utils;
import me.tatarka.bindingcollectionadapter2.ItemBinding;

public class MainViewModel extends BaseViewModel<KungRepository> {
    public MainViewModel(@NonNull Application application, KungRepository model) {
        super(application, model);
    }

    //总净重
    public ObservableField<String> totalWeight = new ObservableField<>("0.00");
    //总称重次数
    public ObservableField<String> totalWeightCount = new ObservableField<>("0");
    //标签抬头
//    public ObservableField<String> tagTitle = new ObservableField<>(model.getLabelTitle());
    public ObservableField<ProductBean> mSelectedProductBean = new ObservableField<>();
//    //产品编号
//    public ObservableField<String> productCode = new ObservableField<>("");
//    //产品名称
//    public ObservableField<String> productName = new ObservableField<>("");
    //营养成分
    public List<ProductNutrition> mProductNutritionList;
    //净重
    public ObservableField<String> productWeight = new ObservableField<>("0.00");
    public ObservableField<String> lastWeight = new ObservableField<>("");    //上次称重
    //毛重
    public ObservableField<String> productGrossWeight = new ObservableField<>("0.00");
    //重量稳定时间
    public ObservableField<String> weightStableTime = new ObservableField<>("");

    //二维码信息
    public String qrCode;

    private SimpleDateFormat sdf_show = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置日期格式
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  //设置日期格式

    public ObservableField<String> mTareWeight = new ObservableField<>(model.getTare());    //皮重

    //标签设置的颜色值：黑、白、透、彩
    public ObservableField<String> mTagColor = new ObservableField<>("黑");
    public ObservableField<Integer> mPrintSelected = new ObservableField<>(model.getPrintCount());

    public ObservableField<String> searchKey = new ObservableField<>("");

    //生产日期
    public ObservableField<String> mProductionDate = new ObservableField<>(sdf.format(new Date()));

    //批次号
    public String mBatchNo = ""; //批次号



    //垃圾分类
    public ObservableList<MainViewProductItem> observableListProduct = new ObservableArrayList<>();
    public ItemBinding<MainViewProductItem> itemBindingProduct = ItemBinding.of(BR.viewModel, R.layout.item_type);

    public ObservableList<MainViewNutritionItem> observableListNutrition = new ObservableArrayList<>();
    public ItemBinding<MainViewNutritionItem> itemBindingNutrition = ItemBinding.of(BR.viewModel, R.layout.item_nutrition_main);

    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();
    public class UIChangeObservable {
        //显示输入皮重弹窗
        public SingleLiveEvent<Void> eventToInputTare = new SingleLiveEvent<>();
        //跳转设置页面
        public SingleLiveEvent<Void> eventToSettingPage = new SingleLiveEvent<>();
        public SingleLiveEvent eventToInputTag = new SingleLiveEvent();

        public SingleLiveEvent eventSelectProductType = new SingleLiveEvent<>();
        //刷新二维码
        public SingleLiveEvent<Void> eventRefreshQrCode = new SingleLiveEvent<>();
        public SingleLiveEvent<Void> eventToRecordPage = new SingleLiveEvent<>();
        //打印
        public SingleLiveEvent<WeighRecord> eventPrint = new SingleLiveEvent<>();
        //发送去皮指令
        public SingleLiveEvent<Void> eventTare = new SingleLiveEvent<>();
        //发送置零指令
        public SingleLiveEvent<Void> eventZero = new SingleLiveEvent<>();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBatchNo = model.getBatchNumber();
    }

    public BindingCommand onEditTagTitleClick = new BindingCommand(() -> {
        //修改标签抬头
        uc.eventToInputTag.call();
    });

    public BindingCommand onEditTare = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            uc.eventToInputTare.call();
        }
    });

    public BindingCommand<String> onRadioGroupCheckColorChanged = new BindingCommand<String>(new BindingConsumer<String>() {
        @Override
        public void call(String s) {
            if ("黑".equals(s)) {
                mTagColor.set("黑");
            } else if ("白".equals(s)){
                mTagColor.set("白");
            } else if ("透".equals(s)){
                mTagColor.set("透");
            } else {
                mTagColor.set("彩");
            }
        }
    });

    public BindingCommand<String> onRadioGroupCheckChanged = new BindingCommand<String>(new BindingConsumer<String>() {
        @Override
        public void call(String s) {
            if("不打印".equals(s)) {
                mPrintSelected.set(0);
                model.savePrintCount(0);
            }else if("打印1份".equals(s)){
                mPrintSelected.set(1);
                model.savePrintCount(1);
            }else{
                mPrintSelected.set(2);
                model.savePrintCount(2);
            }
        }
    });

    public BindingCommand onToSettingPage = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            uc.eventToSettingPage.call();
        }
    });

    public BindingCommand onToRecordPage = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            uc.eventToRecordPage.call();
        }
    });

    //保存并打印
    public BindingCommand onSaveAndPrint = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            doSaveWeighRecord();
//            WeighRecord record = new WeighRecord();
//            doPrintLabel(record);
        }
    });

    //发送去皮指令
    public BindingCommand onSendTare = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            //判断当前重量productWeight.get()是否大于0
            if (!StringUtils.isTrimEmpty(productWeight.get()) && Double.parseDouble(productWeight.get()) > 0) {
                uc.eventTare.call();
                //更新皮重
                model.saveTare(productWeight.get());
                mTareWeight.set(model.getTare());
            } else {
                ToastUtils.showShort("当前皮重小于0，请重新清零或者放置物品");
            }
        }
    });

    //发送清零指令
    public BindingCommand onSendZero = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            uc.eventZero.call();
        }
    });

    //设置选中的垃圾ui
    public void setProductSelectState(ProductBean obj, boolean isSelect) {
        if (obj != null) {
            if (!model.getPluCode().equals(obj.getProductCode())) {
                mBatchNo = "YJ" + DateUtils.getTransactionIdByTime();
                model.saveBatchNumber(mBatchNo);
            }
            mSelectedProductBean.set(obj);
            funLoadNutrition();
            model.savePluCode(obj.getProductCode());
            if (!StringUtils.isTrimEmpty(productWeight.get())) {
                weightStableTime.set(sdf_show.format(new Date()));
            }
        } else {
            mSelectedProductBean.set(null);
            funLoadNutrition();
            model.savePluCode("");
        }

        MainViewProductItem tempViewModel;
        for (int i = 0; i < observableListProduct.size(); i++) {
            tempViewModel = observableListProduct.get(i);
            if (obj != null && obj.getProductCode().equals(tempViewModel.entity.get().getProductCode())) {
                tempViewModel.entity.get().setBgDrawable(Utils.getContext().getResources().getDrawable(R.drawable.shape_trash_type_corner_selected));
                tempViewModel.entity.get().setTextColorName(Utils.getContext().getResources().getColor(R.color.trash_type_selected));
                tempViewModel.entity.get().setTextColorPrice(Utils.getContext().getResources().getColor(R.color.trash_type_price_selected));
                tempViewModel.entity.notifyChange();
            } else {
                tempViewModel.entity.get().setBgDrawable(Utils.getContext().getResources().getDrawable(R.drawable.shape_trash_type_corner));
                tempViewModel.entity.get().setTextColorName(Utils.getContext().getResources().getColor(R.color.trash_type_normal));
                tempViewModel.entity.get().setTextColorPrice(Utils.getContext().getResources().getColor(R.color.trash_type_price_normal));
                tempViewModel.entity.notifyChange();
            }
        }
        if (mSelectedProductBean != null && obj.getProductCode().equals(mSelectedProductBean.get().getProductCode())) {
            return;
        }
        if (isSelect) {
            uc.eventSelectProductType.call();
        }
    }

    //加载产品
    public void funLoadProducts(String keywords) {
        observableListProduct.clear();

        mSelectedProductBean.set(null);
        funLoadNutrition();
        List<ProductBean> list = SqlManager.queryAllProductBean(keywords, 1, 1000);
        if (list == null || list.size() == 0) {
            return;
        }
        //如果只有一个产品，直接选中
        if (list.size() == 1) {
            model.savePluCode(list.get(0).getProductCode());
            mSelectedProductBean.set(list.get(0));
            funLoadNutrition();
        }
        for (ProductBean bean : list) {
            if (model.getPluCode().equals(bean.getProductCode())) {
                mSelectedProductBean.set(bean);
                funLoadNutrition();
            }
            observableListProduct.add(new MainViewProductItem(MainViewModel.this, bean));
        }
        if (mSelectedProductBean.get() != null) {
            setProductSelectState(mSelectedProductBean.get(), false);
        }
    }

    //加载营养成分
    private void funLoadNutrition() {
        observableListNutrition.clear();
        if (mSelectedProductBean.get() == null) {
            return;
        }
        mProductNutritionList = SqlManager.queryNutritionBeanByProductCode(mSelectedProductBean.get().getProductCode());
        if (mProductNutritionList == null) {
            mProductNutritionList = new ObservableArrayList<>();
        }
        for (ProductNutrition productNutrition : mProductNutritionList) {
            observableListNutrition.add(new MainViewNutritionItem(MainViewModel.this, productNutrition));
        }
    }

    //保存标签抬头
//    public void localSetTagTitle() {
//        model.saveLabelTitle(tagTitle.get());
//    }

    //保存皮重
    public void localSetTareWeight() {
        model.saveTare(mTareWeight.get());
    }

    //保存称重并打印
    public boolean doSaveWeighRecord() {
        weightStableTime.set(sdf_show.format(new Date()));

        if (mSelectedProductBean.get() == null) {
            ToastUtils.showShort("请选择产品");
            return false;
        }

        if (CommonUtils.isTest) {
            productWeight.set("2.35");
        }

        if (StringUtils.isTrimEmpty(productWeight.get()) || Double.parseDouble(productWeight.get()) <= 0) {
            ToastUtils.showShort("请称重");
            return false;
        }

        WeighRecord record = new WeighRecord();
        record.set_id(null);
        record.setProductCode(mSelectedProductBean.get().getProductCode());
        record.setProductName(mSelectedProductBean.get().getProductName());
        record.setTypeName(mSelectedProductBean.get().getTypeName());
        record.setIngredientContent(mSelectedProductBean.get().getIngredientContent());
        record.setShelfLife(mSelectedProductBean.get().getShelfLife());
        record.setStorageMethod(mSelectedProductBean.get().getStorageMethod());
        record.setEdibleMethod(mSelectedProductBean.get().getEdibleMethod());
        record.setPrecautions(mSelectedProductBean.get().getPrecautions());
        record.setStandard(mSelectedProductBean.get().getStandard());
        record.setProductionLicense(mSelectedProductBean.get().getProductionLicense());
        record.setManufacturer(mSelectedProductBean.get().getManufacturer());
        record.setAddress(mSelectedProductBean.get().getAddress());
        record.setEntrust(mSelectedProductBean.get().getEntrust());
        record.setEntrustAddress(mSelectedProductBean.get().getEntrustAddress());
        record.setOrigin(mSelectedProductBean.get().getOrigin());
        record.setPhone(mSelectedProductBean.get().getPhone());
        if (mProductNutritionList != null && mProductNutritionList.size() > 0) {
            String nutritionJson = JSON.toJSONString(mProductNutritionList);
            record.setNutritionContent(nutritionJson);
        }
        record.setProductionDate(mProductionDate.get());
        record.setNetWeight(productWeight.get());
        record.setCreateTime(weightStableTime.get());

        if (SqlManager.insertOrReplaceWeighRecord(record) > 0) {
            ToastUtils.showShort("保存成功");
            doPrintLabel(record);
            funLoadTotalWeight();
            return true;
        } else {
            ToastUtils.showShort("保存失败");
            return false;
        }
    }

    private void doPrintLabel(WeighRecord record) {
        uc.eventPrint.setValue(record);
    }

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  //设置日期格式

    //统计数据
    public void funLoadTotalWeight() {
        String beginDate = dateFormat.format(new java.util.Date()) + " 00:00:00";
        String endDate = dateFormat.format(new java.util.Date()) + " 23:59:59";

        long count = SqlManager.queryWeighRecordCount(beginDate, endDate, "");
        totalWeightCount.set(String.valueOf(count));
        List<Double> weights = SqlManager.queryWeighRecordTotalWeight(beginDate, endDate, "");
//        mTotalWeight.set(String.format("%.2f", weights.get(0)));
//        mTotalGrossWeight.set(String.format("%.2f", weights.get(1)));
//        mTotalProductNum.set(weights.get(2).intValue() + "");

        totalWeight.set(String.format("%.2f", weights.get(0)));   //净重
    }
}
