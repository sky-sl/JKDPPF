package com.kung.dppf.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;

import com.kung.dppf.data.KungRepository;
import com.kung.dppf.entity.ProductBean;
import com.kung.dppf.entity.ProductNutrition;
import com.kung.dppf.entity.ProductType;
import com.kung.dppf.entity.database.SqlManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class HomeViewModel extends BaseViewModel<KungRepository> {
    public HomeViewModel(@NonNull Application application, KungRepository model) {
        super(application, model);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            //删除360天前的数据
            Calendar now = Calendar.getInstance();
            now.add(Calendar.DAY_OF_MONTH, -30 * 12);
            String endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now.getTime());
//            System.out.println("==========:" + endDate);
            //删除当天前的本地垃圾记录
            SqlManager.deleteWeighRecords(endDate);
            initData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //初始化数据
    public void initData() {
        List<ProductType> list = SqlManager.queryAllProductTypeBean("", 1, 100);
        if (list == null || list.size() == 0) {
            //初始化数据
            ProductType productType = new ProductType();
            productType.set_id(null);
            productType.setTypeName("油炸");
            productType.setUpdateTime(df.format(new Date()));
            SqlManager.insertOrReplaceProductTypeBean(productType);
        }

        List<ProductBean> list2 = SqlManager.queryAllProductBean("", 1, 1000);
        if (list2 == null || list2.size() == 0) {
            //初始化数据
            ProductBean productBean = new ProductBean();
            productBean.set_id(null);
            productBean.setProductCode("1234567890");
            productBean.setProductName("薯条");
            productBean.setTypeName("油炸");
            //配料表
            productBean.setIngredientContent("土豆");
            //保质期
            productBean.setShelfLife("30天");
            productBean.setStorageMethod("常温");
            productBean.setEdibleMethod("直接食用");
            productBean.setPrecautions("无");
            productBean.setStandard("GB/T 12345-2018");
            productBean.setProductionLicense("SC12345678");
            productBean.setManufacturer("健坤食品有限公司");
            productBean.setAddress("广东省深圳市南山区");
            productBean.setEntrust("健坤食品有限公司");
            productBean.setEntrustAddress("广东省深圳市南山区");
            productBean.setOrigin("广东省深圳市南山区");
            productBean.setPhone("0755-12345678");

            productBean.setUpdateTime(df.format(new Date()));

            productBean.setUpdateTime(df.format(new Date()));
            SqlManager.insertOrReplaceProductBean(productBean);

            //保存营养成分
            ProductNutrition nutrition = new ProductNutrition();
            nutrition.set_id(null);
            nutrition.setProductCode(productBean.getProductCode());
            nutrition.setName("薯条");
            nutrition.setContent("100g");
            nutrition.setNrv("100%");
            nutrition.setSort(1);
            SqlManager.insertOrReplaceNutritionBean(nutrition);
        }
    }
}
