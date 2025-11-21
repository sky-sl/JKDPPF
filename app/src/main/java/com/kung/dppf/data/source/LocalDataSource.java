package com.kung.dppf.data.source;

/**
 * Created by goldze on 2019/3/26.
 */
public interface LocalDataSource {
    /**
     * 保存用户名
     */
    void saveUserName(String userName);

    /**
     * 保存用户密码
     */

    void savePassword(String password);

    /**
     * 获取用户名
     */
    String getUserName();

    /**
     * 获取用户密码
     */
    String getPassword();

    //产品Plu编码
    void savePluCode(String pluCode);
    String getPluCode();

    //标签抬头
    void saveLabelTitle(String labelTitle);
    String getLabelTitle();

    //皮重
    void saveTare(String tare);
    String getTare();
    //批次号
    void saveBatchNumber(String batchNumber);
    String getBatchNumber();
    //打印次数
    void savePrintCount(int printCount);
    int getPrintCount();
}
