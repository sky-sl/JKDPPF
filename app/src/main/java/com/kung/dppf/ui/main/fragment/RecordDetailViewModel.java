package com.kung.dppf.ui.main.fragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kung.dppf.BR;
import com.kung.dppf.R;
import com.kung.dppf.data.KungRepository;
import com.kung.dppf.entity.ProductNutrition;
import com.kung.dppf.entity.WeighRecord;

import java.lang.reflect.Type;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.StringUtils;
import me.tatarka.bindingcollectionadapter2.ItemBinding;

public class RecordDetailViewModel extends BaseViewModel<KungRepository> {

    public ObservableField<WeighRecord> mWeighRecord = new ObservableField<>();

    public RecordDetailViewModel(@NonNull Application application, KungRepository model) {
        super(application, model);
    }

    //封装一个界面发生改变的观察者
    public UIChangeObservable uc = new UIChangeObservable();
    public class UIChangeObservable {
        //返回
        public SingleLiveEvent eventToBack = new SingleLiveEvent<>();
    }

    public BindingCommand onCommandBack = new BindingCommand(() -> {
        uc.eventToBack.call();
    });

    public ObservableList<RecordDetailViewItem> observableListNutrition = new ObservableArrayList<>();
    public ItemBinding<RecordDetailViewItem> itemBindingNutrition = ItemBinding.of(BR.viewModel, R.layout.item_nutrition_detail);

    public void setWeighRecord(WeighRecord weighRecord) {
        mWeighRecord.set(weighRecord);
        observableListNutrition.clear();

        List<ProductNutrition> nutritionList = null;
        if (!StringUtils.isTrimEmpty(weighRecord.getNutritionContent())) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<ProductNutrition>>() {}.getType();
            nutritionList = gson.fromJson(weighRecord.getNutritionContent(), type);
        }
        if (nutritionList != null) {
            for (ProductNutrition productNutrition : nutritionList) {
                observableListNutrition.add(new RecordDetailViewItem(this, productNutrition));
            }
        }
    }
}
