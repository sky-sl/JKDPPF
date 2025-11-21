package com.kung.dppf.ui.main.fragment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.afollestad.materialdialogs.MaterialDialog;
import com.kung.dppf.BR;
import com.kung.dppf.R;
import com.kung.dppf.app.AppViewModelFactory;
import com.kung.dppf.databinding.FragProductsBinding;
import com.kung.dppf.entity.ProductBean;
import com.kung.dppf.entity.ProductType;
import com.kung.dppf.entity.database.SqlManager;
import com.kung.dppf.ui.main.KungViewModel;
import com.kung.dppf.widget.ConformDialog;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import jxl.Workbook;
import jxl.read.biff.BiffException;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.jahnen.libaums.core.UsbMassStorageDevice;
import pub.devrel.easypermissions.EasyPermissions;

public class ProductFrag extends BaseFragment<FragProductsBinding, ProductViewModel> {
    private static final int PICK_EXCEL_REQUEST = 103;

    String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    UsbMassStorageDevice[] devices;

    private KungViewModel kungViewModel;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.frag_products;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public ProductViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return ViewModelProviders.of(getActivity(), factory).get(ProductViewModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        resetData();
    }

    @Override
    public void initData() {
        super.initData();
        devices = UsbMassStorageDevice.getMassStorageDevices(getActivity());
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();

        viewModel.uc.eventBack.observe(this, o -> {
            Navigation.findNavController(getView()).navigateUp();
        });

        viewModel.uc.eventToProductAdd.observe(this, o -> {
            Bundle mBundle = new Bundle();
            mBundle.putSerializable("productBean", null);
            Navigation.findNavController(getView()).navigate(R.id.productAddFrag);
        });

        //监听下拉刷新完成
        viewModel.uc.finishRefreshing.observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                viewModel.funQueryProductList();
                //结束刷新
                binding.recyclingRL.finishRefreshing();
            }
        });
        //监听上拉加载完成
        viewModel.uc.finishLoadMore.observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                //结束刷新
                viewModel.funQueryProductList();
                binding.recyclingRL.finishLoadmore();
            }
        });

        viewModel.uc.eventDeleteItem.observe(this, new Observer<ProductBean>() {
            @Override
            public void onChanged(ProductBean productBean) {
                ConformDialog.Builder builder = new ConformDialog.Builder(getActivity());
                builder.setMessage("是否删除该产品？", "");
                builder.setTitle("删除产品");
                builder.setPositiveButton("确认",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog2,
                                                int which) {
                                dialog2.dismiss();
                                SqlManager.deleteProductBean(productBean);
                                resetData();
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

        viewModel.uc.eventModifyItem.observe(this, new Observer<ProductBean>() {
            @Override
            public void onChanged(ProductBean productBean) {
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("productBean", productBean);
                Navigation.findNavController(getView()).navigate(R.id.productAddFrag, mBundle);
            }
        });

        viewModel.uc.eventSearchKey.observe(this, new Observer() {
            @Override
            public void onChanged(Object o) {
                new MaterialDialog.Builder(getActivity()).title("输入查询关键字")
                        .content("关键字")
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input("关键字", viewModel.mKey.get(), new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                viewModel.mKey.set(input.toString());
                                resetData();
                            }
                        })
                        .positiveText("确定")
                        .show();
            }
        });

        viewModel.uc.eventImportExcel.observe(this, new Observer() {
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
                //打开文件选择器
                openFileSelector();
            }
        });
    }

    /**
     * 打开文件选择器
     */
    private void openFileSelector() {
        //打开文件选择器
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //如何修改为只显示Excel文件
        intent.setType("application/vnd.ms-excel");
//        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        //设置文件类型,xls,xlsx
//        intent.setType("application/vnd.ms-excel | application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//        String[] mimeTypes = {"application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"};
//        intent.setType("*/*");
//        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, PICK_EXCEL_REQUEST);
    }

    private void resetData() {
        viewModel.pageIndex.set(1);
        viewModel.funQueryProductList();
        viewModel.funQueryProductCount();
    }

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置日期格式
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_EXCEL_REQUEST && resultCode == getActivity().RESULT_OK) {
            //获取文件路径
            Uri uri = data.getData();
            //从指定的Uri获取文件
            ContentResolver cr = getActivity().getContentResolver();
            try {
                InputStream fis = cr.openInputStream(uri);
                Workbook workbook = Workbook.getWorkbook(fis);
                //获取第一个工作表
                jxl.Sheet sheet = workbook.getSheet(0);
                //如果列数不为3，提示导入失败
                if (sheet.getColumns() != 3) {
                    new MaterialDialog.Builder(getActivity()).title("提示")
                            .content("导入失败，Excel格式不对，请检查Excel格式是否正确")
                            .positiveText("确定")
                            .show();
                    return;
                }
                //从第二行获取数据
                for (int i = 1; i < sheet.getRows(); i++) {
                    //判断是否为数值sheet.getCell(2, i).getContents()，如果不是数值，跳过本次
                    try {
                        Double.parseDouble(sheet.getCell(2, i).getContents());
                    } catch (Exception e) {
                        continue;
                    }
                    ProductBean productBean = new ProductBean();
                    productBean.set_id(null);
                    productBean.setProductCode(sheet.getCell(0, i).getContents());
                    productBean.setProductName(sheet.getCell(1, i).getContents());
                    productBean.setUpdateTime(df.format(new Date()));

                    SqlManager.insertOrReplaceProductBean(productBean);
                }
                workbook.close();
                resetData();
                //提示导入成功
                new MaterialDialog.Builder(getActivity()).title("提示")
                        .content("导入成功")
                        .positiveText("确定")
                        .show();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (BiffException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //读取Excel文件
//            try {
//                FileInputStream fis = new FileInputStream(path);
//                Workbook workbook = Workbook.getWorkbook(fis);
//                //获取第一个工作表
//                jxl.Sheet sheet = workbook.getSheet(0);
//                //从第二行获取数据
//                for (int i = 1; i < sheet.getRows(); i++) {
//                    ProductBean productBean = new ProductBean();
//                    productBean.set_id(null);
//                    productBean.setProductName(sheet.getCell(0, i).getContents());
//                    productBean.setProductCode(sheet.getCell(1, i).getContents());
//                    productBean.setSingleWeight(sheet.getCell(2, i).getContents());
//                    productBean.setUpdateTime(df.format(new Date()));
//
//                    SqlManager.insertOrReplaceProductBean(productBean);
//                }
//                workbook.close();
//                resetData();
//                //提示导入成功
//                new MaterialDialog.Builder(getActivity()).title("提示")
//                        .content("导入成功")
//                        .positiveText("确定")
//                        .show();
//            } catch (FileNotFoundException e) {
//                throw new RuntimeException(e);
//            } catch (BiffException e) {
//                throw new RuntimeException(e);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
        }
    }
}
