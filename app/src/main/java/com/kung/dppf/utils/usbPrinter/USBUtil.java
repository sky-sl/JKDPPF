package com.kung.dppf.utils.usbPrinter;

import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * Created by xxj on 01/15.
 */

public class USBUtil {
    private static USBUtil instance;

    private PendingIntent mPermissionIntent;
    private UsbManager usbManager;
    private Context context;
    private USBReceiver usbReceiver;
    private UsbInterface usbInterface;
    private UsbEndpoint usbEndpointIn;
    private UsbEndpoint usbEndpointOut;
    private UsbDeviceConnection usbConnection;
    private boolean isConnect = false;

    private USBUtil() {
    }

    public static USBUtil getInstance() {
        if (instance == null) {
            synchronized (USBUtil.class) {
                if (instance == null) {
                    instance = new USBUtil();
                }
            }
        }
        return instance;
    }

    public UsbManager getUsbManager() {
        return usbManager;
    }

    public void init(Context context) {
        this.context = context;
        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        usbReceiver = new USBReceiver();
        mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(USBReceiver.ACTION_USB_PERMISSION),
                PendingIntent.FLAG_IMMUTABLE);
    }

    /**
     * 获取 USB 设备列表
     */
    public List<UsbDevice> getDeviceList() {
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        List<UsbDevice> usbDevices = new ArrayList<>();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            usbDevices.add(device);
            Log.e("USBUtil", "getDeviceList: " + device.getDeviceName());
        }
        return usbDevices;
    }

    /**
     * mVendorId=1137,mProductId=85  佳博 3150T 标签打印机
     *
     * @param vendorId  厂商ID
     * @param productId 产品ID
     * @return device
     */
    public UsbDevice getUsbDevice(int vendorId, int productId) {
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            if (device.getVendorId() == vendorId && device.getProductId() == productId) {
                Log.e("USBUtil", "getDeviceList: " + device.getDeviceName());
                requestPermission(device);
                return device;
            }
        }
//        Toast.makeText(context, "没有对应的设备", Toast.LENGTH_SHORT).show();
        return null;
    }

//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    private void init(Context context) {
//        mContext = context;
//        mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
//        mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
//        // 注册广播监听usb设备
//        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
//        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
//        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
//        mContext.registerReceiver(mUsbDeviceReceiver, filter);
//        // 列出所有的USB设备，并且都请求获取USB权限
//        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
//        for (UsbDevice device : deviceList.values()) {
//            // 得到此设备的一个接口
//            usbInterface = device.getInterface(0);
//            // 获取接口的类别 7代表连接的是打印机
//            if (usbInterface.getInterfaceClass() == 7) {
//                // 1137     85      1027
//                // 26728     1280      1045+2
//                if (!mUsbManager.hasPermission(device)) {
//                    mUsbManager.requestPermission(device, mPermissionIntent);
//                } else {
//                    connectUsbPrinter(device);
//                }
//            }
//        }
//
//    }

    /**
     * 判断对应 USB 设备是否有权限
     */
    public boolean hasPermission(UsbDevice device) {
        return usbManager.hasPermission(device);
    }

    /**
     * 请求获取指定 USB 设备的权限
     */
    public void requestPermission(UsbDevice device) {
        if (device != null) {
            if (usbManager.hasPermission(device)) {
                Toast.makeText(context, "已经获取到权限", Toast.LENGTH_SHORT).show();
                isConnect = true;
            } else {
                if (mPermissionIntent != null) {
                    usbManager.requestPermission(device, mPermissionIntent);
                    Toast.makeText(context, "请求USB权限", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "请注册USB广播", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * 打开通信端口
     */
    public boolean openPort(UsbDevice device) {
        //获取设备接口，一般只有一个，多个的自己研究去
        usbInterface = device.getInterface(0);

        // 判断是否有权限
        if (hasPermission(device)) {
            // 打开设备，获取 UsbDeviceConnection 对象，连接设备，用于后面的通讯
            usbConnection = usbManager.openDevice(device);

            if (usbConnection == null) {
                isConnect = false;
                return false;
            }
            if (usbConnection.claimInterface(usbInterface, true)) {
//                ToastUtils.showShort("找到 USB 设备接口", Toast.LENGTH_SHORT);
            } else {
                usbConnection.close();
                ToastUtils.showShort("没有找到USB设备接口", Toast.LENGTH_SHORT);
                isConnect = false;
                return false;
            }
        } else {
            ToastUtils.showShort("没有USB权限", Toast.LENGTH_SHORT);
            isConnect = false;
            return false;
        }

        //获取接口上的两个端点，分别对应 OUT 和 IN
        for (int i = 0; i < usbInterface.getEndpointCount(); ++i) {
            UsbEndpoint end = usbInterface.getEndpoint(i);
            if (end.getDirection() == UsbConstants.USB_DIR_IN) {
                usbEndpointIn = end;
            } else {
                usbEndpointOut = end;
            }
        }
        isConnect = true;
        return true;
    }

    public boolean isDeviceConnected(UsbDevice device) {
        // 临时连接，检测后立即关闭
        UsbDeviceConnection tempConnection = null;
        try {
            tempConnection = usbManager.openDevice(device);
            if (tempConnection == null) {
                return false;
            }

            // 可选：检查接口是否可用（针对特定打印机） //获取设备接口，一般只有一个，多个的自己研究去
            UsbInterface usbInterface = device.getInterface(0);
            if (usbInterface != null) {
                tempConnection.claimInterface(usbInterface, true);
            }

            return true;
        } catch (Exception e) {
            Log.e("USB", "Check connection failed", e);
            return false;
        } finally {
            // 关键！无论如何都关闭临时连接
            if (tempConnection != null) {
                tempConnection.close();
            }
        }
    }

    public boolean isConnected() {
        return isConnect;
    }

    public int sendMessage(byte[] bytes) {
        return usbConnection.bulkTransfer(usbEndpointOut, bytes, bytes.length, 500);
    }

    public void closeport(int timeout) {
        if (usbConnection == null) {
            return;
        }
        try {
            Thread.sleep((long) timeout);
        } catch (InterruptedException var4) {
            var4.printStackTrace();
        }
        try {
            usbConnection.close();
            usbConnection.releaseInterface(usbInterface);
            usbConnection = null;
            usbEndpointIn = null;
            usbEndpointOut = null;
            usbManager = null;
            usbInterface = null;
            Log.d("DemoKit", "Device closed. ");
        } catch (Exception var3) {
            Log.e("DemoKit", "Exception: " + var3.getMessage());
        }
    }

    /**
     * 注册广播
     */
    public void registerReceiver(Activity context) {
        mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(USBReceiver.ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(USBReceiver.ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        context.registerReceiver(usbReceiver, filter);
    }

    public void unRegisterReceiver(Activity context) {
        context.unregisterReceiver(usbReceiver);
        mPermissionIntent = null;
    }
}
