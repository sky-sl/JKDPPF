package com.kung.dppf.ui.main.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.afollestad.materialdialogs.MaterialDialog;
import com.codbking.widget.DatePickDialog;
import com.codbking.widget.OnSureLisener;
import com.codbking.widget.bean.DateType;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.kung.dppf.BR;
import com.kung.dppf.R;
import com.kung.dppf.app.AppViewModelFactory;
import com.kung.dppf.databinding.FragMainBinding;
import com.kung.dppf.entity.WeighRecord;
import com.kung.dppf.ui.main.HomeActivity;
import com.kung.dppf.ui.main.KungViewModel;
import com.kung.dppf.utils.CommonUtils;
import com.kung.dppf.utils.DateUtils;
import com.kung.dppf.utils.QrCodeUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.utils.StringUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

public class MainFrag extends BaseFragment<FragMainBinding, MainViewModel> {
    private String TAG = MainFrag.class.getSimpleName();
    private KungViewModel kungViewModel;
    private HomeActivity homeActivity;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置日期格式
    private boolean isResetZero = true;    //是否清零,清零过，可以再次自动保存
    private int receiverNum = 0;    //接收次数
    private static int steadyNum = 20;   //稳定接收次数

    private TextWatcher textWatcher;

    private Bitmap qrBitmap;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.frag_main;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public MainViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return ViewModelProviders.of(getActivity(), factory).get(MainViewModel.class);
    }

    @Override
    public void initData() {
        super.initData();
        homeActivity = (HomeActivity) getActivity();
        //软盘弹出，页面不做变化
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        kungViewModel = new ViewModelProvider(getActivity()).get(KungViewModel.class);

        kungViewModel.getMWeight().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String tempWeight) {
                try {
                    double weight = Double.parseDouble(tempWeight);
                    viewModel.productWeight.set(String.format("%.2f", weight));
                    double grossWeight = weight + Double.parseDouble(viewModel.mTareWeight.get());
                    viewModel.productGrossWeight.set(String.format("%.2f", grossWeight));

//                    if (viewModel.mSelectedProductBean != null && !StringUtils.isEmpty(viewModel.mSelectedProductBean.getSingleWeight())) {
//                        //因为单位是g，所以要乘以1000
//                        double amount = 1000 * weight / Double.parseDouble(viewModel.mSelectedProductBean.getSingleWeight());
//                        //四舍五入
//                        viewModel.productCount.set(Math.round(amount) + "");
//                    } else {
//                        viewModel.productCount.set("0");
//                    }

                    if (Double.parseDouble(viewModel.productWeight.get()) <= 0) {
                        isResetZero = true;
                    }

                    //稳定后，自动保存
                    if (viewModel.lastWeight.get().equals(viewModel.productWeight.get())) {
                        //稳定，则增加次数
                        receiverNum++;
                    } else {
                        //不同，重新计算
                        viewModel.lastWeight.set(viewModel.productWeight.get());
                        receiverNum = 0;
                    }
//                    Log.d(TAG, "receiverNum11111: " + receiverNum + "isResetZero" + isResetZero);
                    if (receiverNum >= steadyNum && isResetZero) {
                        //稳定后，自动保存
                        if (!StringUtils.isTrimEmpty(viewModel.productWeight.get()) && Double.parseDouble(viewModel.productWeight.get()) > 0) {
                            viewModel.weightStableTime.set(df.format(new Date()));
//                            if (viewModel.doSaveWeighRecord()) {
//                                isResetZero = false;
//                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    viewModel.productWeight.set("0.00");
                    viewModel.productGrossWeight.set(viewModel.mTareWeight.get());
                }
            }
        });

        //打印机连接状态
        kungViewModel.getmIsPrinterConnect().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    binding.tvPrinterState.setText("已连接");
                    binding.tvPrinterState.setTextColor(getResources().getColor(R.color.colorPrimary));
                    binding.layoutPrinter.setBackground(getResources().getDrawable(R.drawable.shape_btn_rect_corner_green));
                    binding.ivPrinter.setImageResource(R.mipmap.ic_printer_on);
                } else {
                    binding.tvPrinterState.setText("未连接");
                    binding.tvPrinterState.setTextColor(getResources().getColor(R.color.btnColorRed));
                    binding.layoutPrinter.setBackground(getResources().getDrawable(R.drawable.shape_btn_rect_corner_red));
                    binding.ivPrinter.setImageResource(R.mipmap.ic_printer_off);
                }
            }
        });

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 文本变化之前的回调
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 文本变化时的回调，执行搜索操作
                String keyword = s.toString();
//                if (keyword.length() == 0) {
//                    return;
//                }
                viewModel.funLoadProducts(keyword);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 文本变化之后的回调
            }
        };

        binding.etSearchKey.addTextChangedListener(textWatcher);

        qrBitmap = QrCodeUtils.createQRCodeBitmap("welcome", 800, 800,"UTF-8","H", "1", Color.BLACK, Color.WHITE);
//        binding.ivLabel.setImageBitmap(qrBitmap);
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.funLoadProducts(viewModel.searchKey.get());
        viewModel.funLoadTotalWeight();

        if (homeActivity.isTest) {
            //随机重量
            double weight = Math.random() * 100;
            viewModel.productWeight.set(String.format("%.2f", weight));
        }
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();

        viewModel.uc.eventToSettingPage.observe(this, o -> {
            //跳转到设置页面
            Navigation.findNavController(getView()).navigate(R.id.settingFrag);
        });

        viewModel.uc.eventToInputTag.observe(this, o -> {
//            new MaterialDialog.Builder(getActivity())
//                    .title("标签抬头")
//                    //限制输入的长度
//                    .inputRangeRes(1, 20, R.color.textColorGreen)
//                    //限制输入类型
//                    .input("请输入标签", null, new MaterialDialog.InputCallback() {
//                        @Override
//                        public void onInput(MaterialDialog dialog, CharSequence input) {
//                            viewModel.tagTitle.set(String.valueOf(input));
//                            viewModel.localSetTagTitle();
//                            receiverNum = 0;
//                            isResetZero = true;
//                        }
//                    })
//                    .positiveText("确定")
//                    .negativeText("取消")
//                    .show();
            //弹出日期选择框，选择生产日期
            DatePickDialog dialog = new DatePickDialog(getContext());
            //设置上下年分限制
            dialog.setYearLimt(5);
            //设置标题
            dialog.setTitle("选择生成日期");
            //设置类型
            dialog.setType(DateType.TYPE_YMD);
            //设置消息体的显示格式，日期格式
            dialog.setMessageFormat("yyyy-MM-dd");
            //设置选择回调
            dialog.setOnChangeLisener(null);
            //设置点击确定按钮回调
            dialog.setOnSureLisener(new OnSureLisener() {
                @Override
                public void onSure(Date date) {
                    viewModel.mProductionDate.set(DateUtils.dataToStringDate(date));
                }
            });
            dialog.show();
        });

        viewModel.uc.eventToInputTare.observe(this, new Observer() {
            @Override
            public void onChanged(Object o) {
                new MaterialDialog.Builder(getActivity())
                        .title("皮重")
                        //限制输入的长度
                        .inputRangeRes(1, 6, R.color.textColorGreen)
                        //限制输入类型
                        .inputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL)
                        .input("请输入皮重", null, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                //判断是否为数字
                                if (!CommonUtils.isNumeric(String.valueOf(input))) {
                                    return;
                                }
                                if (Double.parseDouble(String.valueOf(input)) < 0) {
                                    ToastUtils.showShort("皮重必须大于0");
                                    return;
                                }
                                viewModel.mTareWeight.set(String.valueOf(input));
                                viewModel.localSetTareWeight();
                                receiverNum = 0;
                                isResetZero = true;
                            }
                        })
                        .positiveText("确定")
                        .negativeText("取消")
                        .show();
            }
        });

        viewModel.uc.eventToRecordPage.observe(this, new Observer<Void>() {
            @Override
            public void onChanged(Void unused) {
                //跳转到记录页面
                Navigation.findNavController(getView()).navigate(R.id.recordFrag);
            }
        });

        viewModel.uc.eventSelectProductType.observe(this, new Observer() {
            @Override
            public void onChanged(Object o) {
                isResetZero = true;
            }
        });

        viewModel.uc.eventPrint.observe(this, new Observer<WeighRecord>() {
            @Override
            public void onChanged(WeighRecord weighRecord) {
                isResetZero = false;
                int printCount = viewModel.mPrintSelected.get();
                for (int i = 0; i < printCount; i++) {
                    //延时200ms打印
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    homeActivity.doPrintContent(weighRecord);
                }
            }
        });

        viewModel.uc.eventTare.observe(this, new Observer() {
            @Override
            public void onChanged(Object o) {
                //去皮 03 54 03
                String order = "025403";
                homeActivity.sendOrderToScale(order);
            }
        });

        viewModel.uc.eventZero.observe(this, new Observer() {
            @Override
            public void onChanged(Object o) {
                //清零 02 5A 03
                String order = "025A03";
                homeActivity.sendOrderToScale(order);
            }
        });
    }

    private String formatDate(Long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date(timestamp));
    }
}
