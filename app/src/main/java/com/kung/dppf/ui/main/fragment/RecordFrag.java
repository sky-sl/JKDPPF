package com.kung.dppf.ui.main.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.afollestad.materialdialogs.MaterialDialog;
import com.codbking.widget.DatePickDialog;
import com.codbking.widget.OnSureLisener;
import com.codbking.widget.bean.DateType;
import com.kung.dppf.BR;
import com.kung.dppf.R;
import com.kung.dppf.app.AppViewModelFactory;
import com.kung.dppf.databinding.FragRecordBinding;
import com.kung.dppf.entity.WeighRecord;
import com.kung.dppf.entity.database.SqlManager;
import com.kung.dppf.ui.main.HomeActivity;
import com.kung.dppf.utils.DateUtils;
import com.kung.dppf.utils.ExcelUtil;
import com.kung.dppf.widget.ConformDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.utils.StringUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.jahnen.libaums.core.UsbMassStorageDevice;
import pub.devrel.easypermissions.EasyPermissions;

public class RecordFrag extends BaseFragment<FragRecordBinding, RecordViewModel> {

    private HomeActivity homeActivity;

    String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    UsbMassStorageDevice[] devices;

    String fileName = "YJ" + DateUtils.getTransactionIdByTimeRandom();
    File file;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.frag_record;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public RecordViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return ViewModelProviders.of(getActivity(), factory).get(RecordViewModel.class);
    }

    @Override
    public void initData() {
        super.initData();
        homeActivity = (HomeActivity) getActivity();
        viewModel.refreshData();

        devices = UsbMassStorageDevice.getMassStorageDevices(getActivity());
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();

        viewModel.uc.eventDateStart.observe(this, new Observer() {
            @Override
            public void onChanged(Object o) {
                DatePickDialog dialog = new DatePickDialog(getContext());
                //设置上下年分限制
                dialog.setYearLimt(5);
                //设置标题
                dialog.setTitle("选择开始时间");
                //设置类型
                dialog.setType(DateType.TYPE_YMD);
                //设置消息体的显示格式，日期格式
                dialog.setMessageFormat("yyyy-MM-dd HH:mm");
                //设置选择回调
                dialog.setOnChangeLisener(null);
                //设置点击确定按钮回调
                dialog.setOnSureLisener(new OnSureLisener() {
                    @Override
                    public void onSure(Date date) {
                        viewModel.mStartDate.set(DateUtils.dataToStringDate(date));
                    }
                });
                dialog.show();
            }
        });

        viewModel.uc.eventDateEnd.observe(this, new Observer() {
            @Override
            public void onChanged(Object o) {
                DatePickDialog dialog = new DatePickDialog(getContext());
                //设置上下年分限制
                dialog.setYearLimt(5);
                //设置标题
                dialog.setTitle("选择结束时间");
                //设置类型
                dialog.setType(DateType.TYPE_YMD);
                //设置消息体的显示格式，日期格式
                dialog.setMessageFormat("yyyy-MM-dd HH:mm");
                //设置选择回调
                dialog.setOnChangeLisener(null);
                //设置点击确定按钮回调
                dialog.setOnSureLisener(new OnSureLisener() {
                    @Override
                    public void onSure(Date date) {
                        viewModel.mEndDate.set(DateUtils.dataToStringDate(date));
                    }
                });
                dialog.show();
            }
        });

        viewModel.uc.eventKeyValue.observe(this, new Observer() {
            @Override
            public void onChanged(Object o) {
                new MaterialDialog.Builder(getActivity()).title("产品关键字")
                        .content("搜索关键字")
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input("产品", viewModel.mKey.get(), new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                viewModel.mKey.set(input.toString());
                                viewModel.refreshData();
                            }
                        })
                        .positiveText("确定")
                        .show();
            }
        });

        //监听下拉刷新完成
        viewModel.uc.finishRefreshing.observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                viewModel.funLoadWeighRecords();
                //结束刷新
                binding.recyclingRL.finishRefreshing();
            }
        });
        //监听上拉加载完成
        viewModel.uc.finishLoadmore.observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                //结束刷新
                viewModel.funLoadWeighRecords();
                binding.recyclingRL.finishLoadmore();
            }
        });

        viewModel.uc.eventDeleteItem.observe(this, new Observer<WeighRecord>() {
            @Override
            public void onChanged(WeighRecord weighRecord) {
                ConformDialog.Builder builder = new ConformDialog.Builder(getActivity());
                builder.setMessage("是否删除该记录？", "");
                builder.setTitle("删除");
                builder.setPositiveButton("确认",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog2,
                                                int which) {
                                dialog2.dismiss();
                                SqlManager.deleteWeighRecord(weighRecord);
                                viewModel.refreshData();
                            }
                        });

                builder.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog2,
                                                int which) {
                                dialog2.dismiss();
                            }
                        });
                builder.create().show();
            }
        });

        viewModel.uc.eventExportData.observe(this, new Observer() {
            @Override
            public void onChanged(Object o) {
                //判断是否有存储权限
                if (!EasyPermissions.hasPermissions(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    EasyPermissions.requestPermissions(getActivity(), "App正常运行需要存储权限权限", 1, perms);
                    return;
                }

                //检测是否有外部存储U盘
                if (devices.length == 0) {
                    ToastUtils.showShort("未检测到U盘");
                    return;
                }

                ConformDialog.Builder builder = new ConformDialog.Builder(getActivity());
                builder.setMessage("是否导出记录？", "");
                builder.setTitle("导出Excel");
                builder.setPositiveButton("确认",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog2,
                                                int which) {
                                dialog2.dismiss();
                                showProgressDialog();
                                String beginDate;
                                String endDate;
                                if (StringUtils.isTrimEmpty(viewModel.mStartDate.get())) {
                                    beginDate = "";
                                } else {
                                    beginDate = viewModel.mStartDate.get() + " 00:00:00";
                                }

                                if (StringUtils.isTrimEmpty(viewModel.mEndDate.get())) {
                                    endDate = "";
                                } else {
                                    endDate = viewModel.mEndDate.get() + " 23:59:59";
                                }
                                List<WeighRecord> weighRecordList = SqlManager.queryWeighRecords(beginDate,endDate, viewModel.mKey.get(), 1, 100000);
                                if (weighRecordList != null && weighRecordList.size() > 0) {
                                    //导出等待提示
                                    //创建文件
                                    String[] title = {"PAKING LIST","PAKING LIST","PAKING LIST"};
                                    ExcelUtil.initExcel(fileName + ".xls", "称重记录", title, 1);

                                    if(ExcelUtil.writeObjListToExcel(weighRecordList, fileName + ".xls")) {
                                        Log.e("TAG", "onChanged: 导出成功" + fileName);
//                                        CommonUtils.shareFile(getActivity(),fileName);
                                        file = new File(ExcelUtil.savePath + "/" + fileName + ".xls");
                                        if (null != file && file.exists()) {
                                            openSaveFileSelector(fileName);
                                            // 隐藏软键盘
                                            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                                        } else {
                                            dismissProgressDialog();
                                        }
//                        https://blog.csdn.net/haiyoumeizhuce/article/details/99981936
                                    }
                                } else {
                                    dismissProgressDialog();
                                }
                            }
                        });

                builder.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog2,
                                                int which) {
                                dialog2.dismiss();
                            }
                        });
                builder.create().show();
            }
        });

        viewModel.uc.eventPrintItem.observe(this, new Observer<WeighRecord>() {
            @Override
            public void onChanged(WeighRecord record) {
//                String qrCode = record.getProductName() + "," + record.getProductColor() + "," +
//                        record.getQuantity()+ "," + record.getWeight() + "kg," +
//                        record.getGrossWeight() + "kg," + record.getWeighTime();
//                record.setQrCode(qrCode);
                homeActivity.doPrintContent(record);
            }
        });

        viewModel.uc.eventDetailItem.observe(this, new Observer<WeighRecord>() {
            @Override
            public void onChanged(WeighRecord record) {
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("weighRecord", record);
                Navigation.findNavController(getView()).navigate(R.id.recordDetailFrag, mBundle);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
    }

    private static final int REQUEST_CODE_SAVE_FILE = 123;

    private void openSaveFileSelector(String fileName) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");  //xlsx
        intent.setType("application/vnd.ms-excel"); //xls
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        startActivityForResult(intent, REQUEST_CODE_SAVE_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SAVE_FILE && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            saveExcelToUri(uri);
        }
    }

    private void saveExcelToUri(Uri uri) {
        //将文件写入到指定的uri中
        ContentResolver contentResolver = getActivity().getContentResolver();
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(contentResolver.openFileDescriptor(uri, "w").getFileDescriptor());
            //将excel文件写入到指定的uri中
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fileInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, len);
            }
            fileOutputStream.close();
            ToastUtils.showShort("导出成功");
            dismissProgressDialog();
        } catch (IOException e) {
            e.printStackTrace();
            ToastUtils.showShort("导出失败");
        }
    }

    // 在 Activity 或 Fragment 中定义 MaterialDialog 变量
    private MaterialDialog progressDialog;

    // 在需要打开等待提示对话框时调用该方法
    private void showProgressDialog() {
        // 创建 MaterialDialog 对话框并显示
        progressDialog = new MaterialDialog.Builder(getActivity())
                .content("请稍候...")
                .progress(true, 0)
                .cancelable(false)  // 禁止对话框被取消
                .show();
    }

    // 在需要关闭对话框的时候调用该方法
    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;  // 清空对话框引用
        }
    }
}
