package com.kung.dppf.utils;

import android.os.Build;

import java.util.UUID;

import me.goldze.mvvmhabit.utils.StringUtils;

/**
 * create by gkm
 * on 2020/5/6
 */
public class AnalysisWeightUtils {

    /**
     * +3132332e34353535352e35356B67odoa
     *
     * +021.33000.00kg
     * 通信协议：波特率9600
     * 共13位数据，2位单位；后面再加个 od oa
     * 6位数据包含小数点，显示实际的称重值
     * 1	2	3	4	5	6	7	8	9	10	11	12	13	14	15
     * 符号（’+’或者‘-’）	6位净重，包含小数点，ASCII码	6位皮重，包含小数点，ASCII码	‘k’	‘g’
     * 举例：净重123.45 皮重 555.55
     * 那么发送数据为：
     * +  31 32 33 2e 34 35 35 35 35 2e 35 35 6B 67 od oa
     *
     * 举例：净重0.45 皮重 0.55
     * 那么发送数据为：
     * +  30 30 30 2e 34 35 30 30 30 2e 35 35 6B 67 od oa
     * @param receiveData
     * @return
     */
    public static String JKanalysisWeight(String receiveData){
        String result = "0.0";
        if(StringUtils.isTrimEmpty(receiveData)){
            return result;
        }

        receiveData = receiveData.trim().toUpperCase();
        //重量数据正常格式为+/-开头，KG结尾
        //判断是否为合法数据
        if (!receiveData.startsWith("+") && !receiveData.startsWith("-") && !receiveData.endsWith("KG")){
            return result;
        }
        //+021.33000.00kg
        //获取符号
        String sign = receiveData.substring(0, 1);
        //KG 移除单位部分
        String dataPart = receiveData.substring(0, receiveData.length() - 2);
        // 移除开头的符号用于后续处理 去除+/-
        String numericPart = dataPart.substring(1);
        // 验证长度是否是偶数
        if (numericPart.length() % 2 != 0) {
            return result;
        }
        // 分割净重和毛重
        int midIndex = numericPart.length() / 2;
        String netWeight = numericPart.substring(0, midIndex);
        String grossWeight = numericPart.substring(midIndex);
        return sign + netWeight;
    }

    /**
     * 参考 https://blog.csdn.net/fepengwang/article/details/116664289
     * 以下规则勿改，否则影响判断
     * @return
     */
    public static String getUUID() {
        String serial = "szt-gkm";
        String m_szDevIDShort = "" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 +
                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 位
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                serial = android.os.Build.getSerial();
//            } else {
//                serial = Build.SERIAL;
//            }
//            //API>=9 使用serial号
//            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
//        } catch (Exception exception) {
//            //serial需要一个初始化
//            serial = "默认值"; // 随便一个初始化
//        }
        //使用硬件信息拼凑出来的15位号码
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }
}
