package com.kung.dppf.data.source.local;

import com.kung.dppf.data.source.LocalDataSource;

import me.goldze.mvvmhabit.utils.SPUtils;

/**
 * 本地数据源，可配合Room框架使用
 * Created by goldze on 2019/3/26.
 */
public class LocalDataSourceImpl implements LocalDataSource {
    private volatile static LocalDataSourceImpl INSTANCE = null;

    public static LocalDataSourceImpl getInstance() {
        if (INSTANCE == null) {
            synchronized (LocalDataSourceImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LocalDataSourceImpl();
                }
            }
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private LocalDataSourceImpl() {
        //数据库Helper构建
    }

    @Override
    public void saveUserName(String userName) {
        SPUtils.getInstance().put("UserName", userName);
    }

    @Override
    public void savePassword(String password) {
        SPUtils.getInstance().put("password", password);
    }

    @Override
    public String getUserName() {
        return SPUtils.getInstance().getString("UserName");
    }

    @Override
    public String getPassword() {
        return SPUtils.getInstance().getString("password");
    }

    @Override
    public void savePluCode(String pluCode) {
        SPUtils.getInstance().put("pluCode", pluCode);
    }

    @Override
    public String getPluCode() {
        return SPUtils.getInstance().getString("pluCode");
    }

    @Override
    public void saveLabelTitle(String labelTitle) {
        SPUtils.getInstance().put("labelTitle", labelTitle);
    }

    @Override
    public String getLabelTitle() {
        return SPUtils.getInstance().getString("labelTitle");
    }

    @Override
    public void saveTare(String tare) {
        SPUtils.getInstance().put("tare", tare);
    }

    @Override
    public String getTare() {
        return SPUtils.getInstance().getString("tare", "0.0");
    }

    @Override
    public void saveBatchNumber(String batchNumber) {
        SPUtils.getInstance().put("batchNumber", batchNumber);
    }

    @Override
    public String getBatchNumber() {
        return SPUtils.getInstance().getString("batchNumber");
    }

    @Override
    public void savePrintCount(int printCount) {
        SPUtils.getInstance().put("printCount", printCount);
    }

    @Override
    public int getPrintCount() {
        return SPUtils.getInstance().getInt("printCount", 1);
    }
}
