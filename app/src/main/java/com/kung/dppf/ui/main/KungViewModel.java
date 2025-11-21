package com.kung.dppf.ui.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kung.dppf.entity.ProductType;

public class KungViewModel extends ViewModel {
    private MutableLiveData<String> mWeight = new MutableLiveData();  //重量
    //打印机连接状态
    private MutableLiveData<Boolean> mIsPrinterConnect = new MutableLiveData<>(false);
    //选择的品类
    private MutableLiveData<ProductType> mSelectType = new MutableLiveData();

    public void sendWeight(String weight){
        mWeight.postValue(weight);
    }

    public MutableLiveData<String> getMWeight(){
        return mWeight;
    }

    public MutableLiveData<Boolean> getmIsPrinterConnect() {
        return mIsPrinterConnect;
    }

    public void setmIsPrinterConnect(MutableLiveData<Boolean> mIsPrinterConnect) {
        this.mIsPrinterConnect = mIsPrinterConnect;
    }

    public MutableLiveData<ProductType> getSelectType() {
        return mSelectType;
    }

    public void setSelectType(ProductType mSelectType) {
        this.mSelectType.postValue(mSelectType);
    }
}
