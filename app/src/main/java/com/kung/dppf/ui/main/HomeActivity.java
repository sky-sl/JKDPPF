package com.kung.dppf.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.aill.androidserialport.SerialPort;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kung.dppf.BR;
import com.kung.dppf.R;
import com.kung.dppf.app.AppApplication;
import com.kung.dppf.app.AppViewModelFactory;
import com.kung.dppf.databinding.ActivityHomeBinding;
import com.kung.dppf.entity.ProductNutrition;
import com.kung.dppf.entity.WeighRecord;
import com.kung.dppf.utils.AnalysisWeightUtils;
import com.kung.dppf.utils.CommonUtils;
import com.kung.dppf.utils.HexUtil;
import com.kung.dppf.utils.SerialManager;
import com.kung.dppf.utils.usbPrinter.USBReceiver;
import com.kung.dppf.utils.usbPrinter.USBUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.utils.StringUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import pub.devrel.easypermissions.EasyPermissions;

public class HomeActivity extends BaseActivity<ActivityHomeBinding, HomeViewModel> {
    private static String TAG = HomeActivity.class.getSimpleName();
    private static HomeActivity mHomeActivity;

    private NavController navController;
    private KungViewModel kungViewModel;

    String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    private USBUtil usbUtil;
    private UsbDevice mUsbDevice;

    private ConnectedThread mConnectedThread;   //串口，称重

    Timer timerConnect = new Timer(true);

    public static final String ACTION_USB_STATE = "android.hardware.usb.action.USB_STATE";
    public static final int REC_DATA = 2;

    //是否是测试
    public boolean isTest = false;

    // 字符宽度表，根据需要可以进行扩展和调整
    private static final Map<Character, Integer> charWidths = new HashMap<>();

    static {
        // 设置基本的字符宽度
        for (char ch = '0'; ch <= '9'; ch++) charWidths.put(ch, 1);
        for (char ch = 'a'; ch <= 'z'; ch++) charWidths.put(ch, 1);
        for (char ch = 'A'; ch <= 'Z'; ch++) charWidths.put(ch, 1);
        charWidths.put(' ', 1);
        charWidths.put('.', 1);
        charWidths.put(':', 1);

        // 设置汉字的宽度（假设为2）
        for (char ch = 0x4e00; ch <= 0x9fff; ch++) charWidths.put(ch, 2);
    }

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_home;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public HomeViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getApplication());
        return ViewModelProviders.of(this, factory).get(HomeViewModel.class);
    }

    @Override
    public void initData() {
        super.initData();
        AppApplication.homeActivity = new WeakReference<HomeActivity>(this);
        mHomeActivity = this;
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        kungViewModel = new ViewModelProvider(this).get(KungViewModel.class);

        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "App正常运行需要存储权限、媒体权限", 1, perms);
        }

        // 注册广播监听usb设备
        IntentFilter filter = new IntentFilter(USBReceiver.ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(ACTION_USB_STATE);
        registerReceiver(mUsbDeviceReceiver, filter);

        // 初始化usb串口的连接
        USBUtil.getInstance().init(getApplication());

        //延时1.5秒执行
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //初始化串口
                autoConnectDevice();
            }
        }, 1500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mUsbDeviceReceiver != null) {
            unregisterReceiver(mUsbDeviceReceiver);
        }

        timerConnect.cancel();
    }

    boolean isConnectPrinter = false;
    private void autoConnectDevice() {
        timerConnect.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!SerialManager.getInstance().isOpened()) {
                    SerialManager.getInstance().openSerial("/dev/ttyS1", 9600);
                    // 开启已连接线程
                    if (mConnectedThread == null) {
                        mConnectedThread = new ConnectedThread(SerialManager.getInstance().getSerialPort());
                        mConnectedThread.start();
                    }
                }

                if (mUsbDevice != null) {
                    isConnectPrinter = usbUtil.isDeviceConnected(mUsbDevice);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mUsbDevice = usbUtil.getUsbDevice(19267, 13624);
                        }
                    });
//                    if (mUsbDevice != null) {
//                        boolean hasPermission = usbUtil.openPort(mUsbDevice);
//                        if (!hasPermission) {
//                            usbUtil.requestPermission(mUsbDevice);
//                        }
//                    }
                }

                //IsPrinterConnect变化才更新
                if (isConnectPrinter != kungViewModel.getmIsPrinterConnect().getValue()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            kungViewModel.getmIsPrinterConnect().setValue(isConnectPrinter);
                        }
                    });
                }
            }
        },100,5000);
    }

    private final BroadcastReceiver mUsbDeviceReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            mUsbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (USBReceiver.ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
//                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false) && mUsbDevice != null) {
//                        ToastUtils.showShort( "USB设备请求被允许");
//                        kungViewModel.getmIsPrinterConnect().setValue(true);
//                    } else {
//                        ToastUtils.showShort( "USB设备请求被拒绝");
//                        kungViewModel.getmIsPrinterConnect().setValue(false);
//                    }
//                    if (mUsbDevice != null) {
//                        usbUtil.requestPermission(mUsbDevice);
//                    }
                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                if (mUsbDevice != null) {
                    ToastUtils.showShort( "有设备拔出");
                    kungViewModel.getmIsPrinterConnect().setValue(false);
                }
            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                ToastUtils.showShort( "有设备插入");

                usbUtil = USBUtil.getInstance();
                if (mUsbDevice == null) {
                    mUsbDevice = usbUtil.getUsbDevice(19267, 13624);
                }

                if (mUsbDevice != null) {
                    boolean hasPermission = usbUtil.openPort(mUsbDevice);
                    if (!hasPermission) {
                        usbUtil.requestPermission(mUsbDevice);
                    }
                }
            } else if (ACTION_USB_STATE.equals(action)) {

                boolean connected = true;
                try {
                    /**  true - USB连接；false - USB未连接 或 电源充电  */
                    connected = intent.getBooleanExtra("connected", false);
//                    ToastUtils.showShort( "有设备" + connected);
//                    kungViewModel.getmIsBlePrinterConnect().setValue(connected);
                    usbUtil = USBUtil.getInstance();
                    if (mUsbDevice == null) {
                        mUsbDevice = usbUtil.getUsbDevice(19267, 13624);
                    }

                    if (mUsbDevice != null) {
                        boolean hasPermission = usbUtil.openPort(mUsbDevice);
                        if (!hasPermission) {
                            usbUtil.requestPermission(mUsbDevice);
                        }
                    }

                    /** true - USB传输文件模式  */
//                    boolean function_mtp = intent.getBooleanExtra(USB_FUNCTION_MTP, false);
//                    /** true - USB传输图片模式  */
//                    boolean function_ptp = intent.getBooleanExtra(USB_FUNCTION_PTP, false);
//                    /** true - adb模式（USB调试） */
//                    boolean function_adb = intent.getBooleanExtra(USB_FUNCTION_ADB , false);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * 已连接的相关处理线程
     */
    private class ConnectedThread extends Thread {
        private final SerialPort serialPort;
        private final InputStream mInputStream;
        private final OutputStream mOutputStream;

        public ConnectedThread(SerialPort socket) {
            serialPort = socket;
            InputStream is;
            OutputStream os;
            // 获取输入输出流
            is = socket.getInputStream();
            os = socket.getOutputStream();

            mInputStream = is;
            mOutputStream = os;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            DataInputStream in = new DataInputStream(mInputStream);
            // 监听输入流以备获取数据
            while (true) {
                try {
                    if (mInputStream.available() > 0) {
                        //当接收到数据时，sleep 500毫秒（sleep时间自己把握）
                        Thread.sleep(50);
                        int l;
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        while ((l = in.read(buffer)) != -1) {
                            out.write(buffer, 0, l);
                            break;
                        }
                        //转换为16进制字符串-健坤专用，别的称可能不支持
                        String temp_data = HexUtil.formatHexString(out.toByteArray(), false).toUpperCase();
                        mHandler.obtainMessage(REC_DATA, temp_data).sendToTarget();
                    }
                } catch (IOException | InterruptedException e) {
                    Log.e(TAG, "connection break", e);
                    break;
                }
                try {
                    //线程睡眠20ms以避免过于频繁工作  50ms->20ms 2017.12.2
                    //导致UI处理发回的数据不及时而阻塞
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

    // 用于从线程获取信息的Handler对象
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler(){
        String msgResult;
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REC_DATA:
                    try{
                        msgResult = (String) msg.obj;
                        //进行解密
//                        doDealDecryptionWeight(msgResult);
                        doDealWeight(msgResult);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    /**
     * 对接收到的重量进行解密
     * @param rec_data
     */
    private void doDealDecryptionWeight(String rec_data) throws Exception {
//        rec_data = "023165097EAFECDF1648B9B75485FA3B6A598EFFB6598EFF3C6FA698C9A203";
        if (StringUtils.isTrimEmpty(rec_data) || rec_data.length() != 31 * 2) {
            return;
        }
        byte[] bytes = HexUtil.hexStringToBytes(rec_data);  //长度31

        int data_A = bytes[1] & 0xff;
        int data_B = bytes[29] & 0xff;

        int startIndex = 0;
        int result = 0;
        //毛重索引 2-10
        //皮重    11-19
        //净重    20-28
        String weight_jz = "";  //净重
        startIndex = 20;
        for (int i = 1; i < 9; i++) {
            result = HexUtil.int2Bytes(bytes[startIndex + i] ^ data_B)[0];
            result = HexUtil.int2Bytes(result - i * data_A)[0];
            result = HexUtil.int2Bytes(result - 0xAA)[0];
            weight_jz += (char) result;
        }

        if (!StringUtils.isTrimEmpty(weight_jz)) {
            weight_jz = weight_jz.trim();
            kungViewModel.sendWeight(weight_jz);
        }
    }

    private void doDealWeight(String rec_data) {
        if (!StringUtils.isTrimEmpty(rec_data)) {
            rec_data = rec_data.replace(" ", "");
            byte[] bytes = HexUtil.hexStringToBytes(rec_data);
            String tempData = byteArrayToHexStr(bytes);
            String weight = AnalysisWeightUtils.JKanalysisWeight(tempData);
            kungViewModel.sendWeight(weight);
//            if("0.0".equals(weight)){
//                kungViewModel.setmTestContent(tempData);
//            } else {
//                kungViewModel.sendWeight(weight);
//            }
//            kungViewModel.sendWeight("" + Double.parseDouble(AnalysisWeightUtils.JKanalysisWeight(tempData, bytes.length)));
        }

//        String weight = AnalysisWeightUtils.JKanalysisWeight(rec_data);
//        kungViewModel.sendWeight(weight);
//        kungViewModel.sendWeight(rec_data);
    }


    ///打印标签
    public void doPrintContent(WeighRecord printDataBean) {
        //将营养成分json转换为对象列表
        List<ProductNutrition> nutritionList = null;
        if (!StringUtils.isTrimEmpty(printDataBean.getNutritionContent())) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<ProductNutrition>>() {}.getType();
            nutritionList = gson.fromJson(printDataBean.getNutritionContent(), type);
        }

        usbUtil = USBUtil.getInstance();
        if (mUsbDevice == null) {
            mUsbDevice = usbUtil.getUsbDevice(19267, 13624);
        }
        //其实高度坐标
        int startY2 = 47;
        //间隔距离
        int interval = 25;
        String printContent = "";
        //打印纸宽度540，高度380
        int printWidth = 780;
        if (CommonUtils.isTest) {
            //58mm打印纸
            printWidth = 540;
        }
        int printHeight = 800;
        String tempStr = "";
        int startX = 0;
        int startY = 0;
        int textTempWidth = 0;
        int contentMaxLen = 22;
        double fontSize = 1.2;
        double persent = 0.33;
        if (mUsbDevice != null && usbUtil.openPort(mUsbDevice)) {
            if (CommonUtils.isTest) {
                //实际54*38，高度设置相差2mm才正常
                printContent = "INTIALPRINTER\n" +
                        "SIZE 58 mm,38 mm\n" +
                        "GAP 2 mm,0 mm\n" +
                        "DIRECTION 1 \n" +
                        "CLS\n";
                usbUtil.sendMessage(printContent.getBytes(StandardCharsets.UTF_8));
                tempStr = "外用-擦拭";
//                printContent = "TEXT " + startX + ",2,\"4\",0,1.2,1.2,\"" + tempStr + "\"\n"; //公司名称
                //计算位置，让其居中
                fontSize = 3;
                startY = 90;
                textTempWidth = (int) (calculateTotalWidth(tempStr) * 15 * fontSize);
                startX = (int) ((printWidth - textTempWidth) / 2 / 1.5);
                printContent = "TEXT " + startX + "," + startY + ",\"4\",0," + fontSize + "," + fontSize + ",\"" + tempStr + "\"\n"; //公司名称
                usbUtil.sendMessage(printContent.getBytes(StandardCharsets.UTF_8));
                tempStr = "酒精、碘伏等、棉签";
                fontSize = 1.5;
                startY += 80;
                textTempWidth = (int) (calculateTotalWidth(tempStr) * 15 * fontSize);
                startX = (int) ((printWidth - textTempWidth) / 2 / 1.5);
                printContent = "TEXT " + startX + "," + startY + ",\"4\",0," + fontSize + "," + fontSize + ",\"" + tempStr + "\"\n"; //公司名称
                usbUtil.sendMessage(printContent.getBytes(StandardCharsets.UTF_8));
                usbUtil.sendMessage(printLabel(1,1));
                return;
            }
            //实际54*38，高度设置相差2mm才正常
            printContent = "INTIALPRINTER\n" +
                    "SIZE 78 mm,78 mm\n" +
                    "GAP 2 mm,0 mm\n" +
                    "DIRECTION 1 \n" +
                    "CLS\n";
            usbUtil.sendMessage(printContent.getBytes(StandardCharsets.UTF_8));

//            s = "BOX 0,60,380,280,3\n"; //x1,y1,x2,y2,宽度  ：矩形
//            usbUtil.sendMessage(s.getBytes(StandardCharsets.UTF_8));

            //净含量占打印纸40%宽度并居中
            tempStr = getString(R.string.product_net_weight_colon) + printDataBean.getNetWeight() + " KG";
            //计算文字所占长度
//            textTempWidth = calculateTotalWidth(tempStr) * 10;
//            startX = (int) ((printWidth * 0.4 - textTempWidth) / 2);
            startX = 10;
            printContent = "TEXT " + startX + ",2,\"4\",0,1.2,1.2,\"" + tempStr + "\"\n"; //公司名称
//            s = "TEXT 1,5,\"4\",0,1.5,1.5,\"" + printDataBean.getTagTitle()+ "\"\n"; //公司名称
            usbUtil.sendMessage(printContent.getBytes(StandardCharsets.UTF_8));

            //同行打印产品名称,在后面60%宽度中间
            tempStr = getString(R.string.product_name_colon) + printDataBean.getProductName();
            textTempWidth = calculateTotalWidth(tempStr) * 20;
            startX = (int) ((printWidth * 0.39 - textTempWidth) / 2) + (int)(printWidth * persent);
            printContent = "TEXT " + startX + ",2,\"4\",0,1.2,1.2,\"" + tempStr + "\"\n"; //公司名称
            usbUtil.sendMessage(printContent.getBytes(StandardCharsets.UTF_8));
            //营养成分
            tempStr = getString(R.string.product_nutrition);
            //计算文字所占长度
            textTempWidth = calculateTotalWidth(tempStr) * 15;
            startX = (int) ((printWidth * 0.39 - textTempWidth) / 2) + (int)(printWidth * persent);
            printContent = "TEXT " + startX + ",40,\"3\",0,1,1,\"" + tempStr + "\"\n"; //公司名称
            usbUtil.sendMessage(printContent.getBytes(StandardCharsets.UTF_8));

            if (isTest) {
//                printDataBean.setProductName("123456789012345678901234567890");
//                printDataBean.setProductName("gaegae人孔鹅考好么:2");
//                printDataBean.setProductName("层圆肩大圈层圆肩大圈层圆肩大圈层圆肩大圈");
            }

            //打印基本信息
            startX = 0;
            startY2 = 40;
            tempStr = getString(R.string.product_type_name_colon) + printDataBean.getTypeName();
            printContent = "TEXT " + startX + "," + startY2 + ",\"4\",0,1,1,\"" + tempStr + "\"\n"; //产品分类
            usbUtil.sendMessage(printContent.getBytes(StandardCharsets.UTF_8));
            startY2 += interval;

            tempStr = getString(R.string.product_formula_colon) + printDataBean.getIngredientContent();  //配料表
//            tempStr = getString(R.string.product_formula_colon) + "冻鸭、食用盐、冰糖、味精、鸡精、黄毅、香辛料";  //配料表
            String[] splitTexts = splitText(tempStr, contentMaxLen);
            for (int i = 0; i < splitTexts.length; i++) {
                printContent = "TEXT " + startX + "," + startY2 + ",\"3\",0,1,1,\"" + splitTexts[i] + "\"\n"; //Size#
                usbUtil.sendMessage(printContent.getBytes(StandardCharsets.UTF_8));
                startY2 += interval;
            }

            //生产日期
            tempStr = getString(R.string.product_produce_date_colon) + printDataBean.getProductionDate();
            printContent = "TEXT " + startX + "," + startY2 + ",\"3\",0,1,1,\"" + tempStr + "\"\n";
            usbUtil.sendMessage(printContent.getBytes(StandardCharsets.UTF_8));
            startY2 += interval;

            //保质期
            tempStr = getString(R.string.product_shelf_life_colon) + printDataBean.getShelfLife();
            printContent = "TEXT " + startX + "," + startY2 + ",\"3\",0,1,1,\"" + tempStr + "\"\n";
            usbUtil.sendMessage(printContent.getBytes(StandardCharsets.UTF_8));
            startY2 += interval;
            //贮存方法
            tempStr = getString(R.string.product_storage_colon) + printDataBean.getStorageMethod();
            printContent = "TEXT " + startX + "," + startY2 + ",\"3\",0,1,1,\"" + tempStr + "\"\n";
            usbUtil.sendMessage(printContent.getBytes(StandardCharsets.UTF_8));
            startY2 += interval;
            //食用方法
            tempStr = getString(R.string.product_eat_colon) + printDataBean.getEdibleMethod();
            printContent = "TEXT " + startX + "," + startY2 + ",\"3\",0,1,1,\"" + tempStr + "\"\n";
            usbUtil.sendMessage(printContent.getBytes(StandardCharsets.UTF_8));
            startY2 += interval;
            //注意事项，大于contentMaxLen换行
            tempStr = getString(R.string.product_note_colon) + printDataBean.getPrecautions();
            splitTexts = splitText(tempStr, contentMaxLen);
            for (int i = 0; i < splitTexts.length; i++) {
                printContent = "TEXT " + startX + "," + startY2 + ",\"3\",0,1,1,\"" + splitTexts[i] + "\"\n"; //Size#
                usbUtil.sendMessage(printContent.getBytes(StandardCharsets.UTF_8));
                startY2 += interval;
            }

            //执行标准
            startY2 += 50;
            tempStr = getString(R.string.product_standard_colon) + printDataBean.getStandard();
            printContent = "TEXT " + startX + "," + startY2 + ",\"3\",0,1,1,\"" + tempStr + "\"\n";
            usbUtil.sendMessage(printContent.getBytes(StandardCharsets.UTF_8));
            startY2 += interval;
            //生产许可证编号
            tempStr = getString(R.string.product_license_colon) + printDataBean.getProductionLicense();
            printContent = "TEXT " + startX + "," + startY2 + ",\"3\",0,1,1,\"" + tempStr + "\"\n";
            usbUtil.sendMessage(printContent.getBytes(StandardCharsets.UTF_8));
            startY2 += interval;
            //生产厂家
            tempStr = getString(R.string.product_factory_colon) + printDataBean.getManufacturer();
            printContent = "TEXT " + startX + "," + startY2 + ",\"3\",0,1,1,\"" + tempStr + "\"\n";
            usbUtil.sendMessage(printContent.getBytes(StandardCharsets.UTF_8));
            startY2 += interval;
            //地址
            tempStr = getString(R.string.product_address_colon) + printDataBean.getAddress();
            printContent = "TEXT " + startX + "," + startY2 + ",\"3\",0,1,1,\"" + tempStr + "\"\n";
            usbUtil.sendMessage(printContent.getBytes(StandardCharsets.UTF_8));
            startY2 += interval;
            //委托商
            tempStr = getString(R.string.product_entrust_colon) + printDataBean.getEntrust();
            printContent = "TEXT " + startX + "," + startY2 + ",\"3\",0,1,1,\"" + tempStr + "\"\n";
            usbUtil.sendMessage(printContent.getBytes(StandardCharsets.UTF_8));
            startY2 += interval;
            //委托商地址
            tempStr = getString(R.string.product_entrust_address_colon) + printDataBean.getEntrustAddress();
            printContent = "TEXT " + startX + "," + startY2 + ",\"3\",0,1,1,\"" + tempStr + "\"\n";
            usbUtil.sendMessage(printContent.getBytes(StandardCharsets.UTF_8));
            startY2 += interval;
            //产地
            tempStr = getString(R.string.product_origin_colon) + printDataBean.getOrigin();
            printContent = "TEXT " + startX + "," + startY2 + ",\"3\",0,1,1,\"" + tempStr + "\"\n";
            usbUtil.sendMessage(printContent.getBytes(StandardCharsets.UTF_8));
            startY2 += interval;
            //电话
            tempStr = getString(R.string.product_phone_colon) + printDataBean.getPhone();
            printContent = "TEXT " + startX + "," + startY2 + ",\"3\",0,1,1,\"" + tempStr + "\"\n";
            usbUtil.sendMessage(printContent.getBytes(StandardCharsets.UTF_8));

            //绘制矩形框
            interval = 38;
            startX = (int) (printWidth * persent);
            int boxStartY = 70;
            printContent = "BOX " + startX + "," + boxStartY + "," + printWidth*0.72 + "," + (boxStartY + interval) + ",3\n"; //x1,y1,x2,y2,宽度  ：矩形
            usbUtil.sendMessage(printContent.getBytes(StandardCharsets.UTF_8));
            //在矩形框内按2,2,1占比绘制 项目，每100克，nrv_percent
            startY2 = 80;
            //项目
            tempStr = getString(R.string.project);
            printContent = "TEXT " + (startX + 5) + "," + startY2 + ",\"3\",0,0.8,0.8,\"" + tempStr + "\"\n"; //Size#
            usbUtil.sendMessage(printContent.getBytes(StandardCharsets.UTF_8));
            //每100克
            tempStr = getString(R.string.per_100g);
            printContent = "TEXT " + (printWidth * 0.48) + "," + startY2 + ",\"3\",0,0.8,0.8,\"" + tempStr + "\"\n"; //Size#
            usbUtil.sendMessage(printContent.getBytes(StandardCharsets.UTF_8));
            //nrv_percent
            tempStr = getString(R.string.nrv_percent);
            printContent = "TEXT " + (printWidth * 0.64) + "," + startY2 + ",\"3\",0,0.8,0.8,\"" + tempStr + "\"\n"; //Size#
            usbUtil.sendMessage(printContent.getBytes(StandardCharsets.UTF_8));
            //根据数量打印营养成分
            if (nutritionList != null && nutritionList.size() > 0) {
                for (int i = 0; i < nutritionList.size(); i++) {
                    ProductNutrition productNutrition = nutritionList.get(i);
                    //项目
                    tempStr = productNutrition.getName();
                    printContent = "TEXT " + (startX + 5) + "," + (startY2 + (i + 1) * interval) + ",\"3\",0,0.8,0.8,\"" + tempStr + "\"\n"; //Size#
                    usbUtil.sendMessage(printContent.getBytes(StandardCharsets.UTF_8));
                    //每100克
                    tempStr = productNutrition.getContent();
                    printContent = "TEXT " + (printWidth * 0.48) + "," + (startY2 + (i + 1)  * interval) + ",\"3\",0,0.8,0.8,\"" + tempStr + "\"\n"; //Size#
                    usbUtil.sendMessage(printContent.getBytes(StandardCharsets.UTF_8));
                    //nrv_percent
                    tempStr = productNutrition.getNrv() + "%";
                    printContent = "TEXT " + (printWidth * 0.64) + "," + (startY2 + (i + 1)  * interval) + ",\"3\",0,0.8,0.8,\"" + tempStr + "\"\n"; //Size#
                    usbUtil.sendMessage(printContent.getBytes(StandardCharsets.UTF_8));
                }
                //绘制矩形框
                startY2 = nutritionList.size() * interval + boxStartY + interval;
                printContent = "BOX " + startX + "," + (boxStartY + interval) + "," + printWidth*0.72 + "," + startY2 + ",3\n"; //x1,y1,x2,y2,宽度  ：矩形
                usbUtil.sendMessage(printContent.getBytes(StandardCharsets.UTF_8));
            }


//            //打印二维码
//            s = "QRCODE 240," + startY2 + ",M,1,A,0,M1,S7,\"" + printDataBean.getQrCode() + "\"\n";
//            usbUtil.sendMessage(s.getBytes(StandardCharsets.UTF_8));
//            startY2 += interval + 5;
//            s = "TEXT 5," + startY2 + ",\"2\",0,1,1,\"时间：" + printDataBean.getWeighTime() + "\"\n"; //批号
//            usbUtil.sendMessage(s.getBytes(StandardCharsets.UTF_8));

            usbUtil.sendMessage(printLabel(1,1));
        } else {
            usbUtil.requestPermission(mUsbDevice);
        }
    }

    public byte[] printLabel(int quantity, int copy) {
        String message = "";
        message = "PRINT " + quantity + ", " + copy + "\r\n";
        return message.getBytes();
    }

    public void sendOrderToScale(String order){
        if (mConnectedThread == null) {
            return;
        }
        SerialManager.getInstance().send(order);
    }

    // 获取字符的宽度，默认为1
    private static int getCharWidth(char ch) {
        return charWidths.getOrDefault(ch, 1);
    }

    // 计算字符串的总宽度
    public static int calculateTotalWidth(String text) {
        int totalWidth = 0;
        for (char ch : text.toCharArray()) {
            totalWidth += getCharWidth(ch);
        }
        return totalWidth;
    }

    // 根据宽度限制分割字符串
    public static String[] splitText(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        int currentWidth = 0;

        for (char ch : text.toCharArray()) {
            int charWidth = getCharWidth(ch);

            // 如果当前行宽度超过限制，将当前行保存，并开始新的行
            if (currentWidth + charWidth > maxWidth) {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder();
                currentWidth = 0;
            }

            currentLine.append(ch);
            currentWidth += charWidth;
        }

        // 添加最后一行
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        return lines.toArray(new String[0]);
    }

    /**
     * String ss = "2B 30 30 30 2E 30 30 30 30 30 2E 30 30 6B 67 0D 0A";
     * @param byteArray
     * @return
     */
    public static String byteArrayToHexStr(byte[] byteArray) {
        if (byteArray == null){
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < byteArray.length; i++) {
            sb.append((char)byteArray[i]);
        }
        return sb.toString();
    }
}
