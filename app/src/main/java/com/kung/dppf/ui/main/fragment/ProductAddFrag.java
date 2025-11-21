package com.kung.dppf.ui.main.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.afollestad.materialdialogs.MaterialDialog;
import com.kung.dppf.BR;
import com.kung.dppf.R;
import com.kung.dppf.app.AppViewModelFactory;
import com.kung.dppf.databinding.FragProductEditBinding;
import com.kung.dppf.entity.ProductBean;
import com.kung.dppf.entity.ProductNutrition;
import com.kung.dppf.entity.ProductType;
import com.kung.dppf.ui.main.KungViewModel;
import com.kung.dppf.widget.ConformDialog;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.utils.ToastUtils;

public class ProductAddFrag extends BaseFragment<FragProductEditBinding, ProductAddViewModel> {

    private ProductBean productBean;
    private KungViewModel kungViewModel;
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.frag_product_edit;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public ProductAddViewModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return ViewModelProviders.of(getActivity(), factory).get(ProductAddViewModel.class);
    }

    @Override
    public void initParam() {
        super.initParam();
        Bundle mBundle = getArguments();
        if (mBundle != null) {
            //获取传递的实体类(产品信息
            productBean = (ProductBean) mBundle.getSerializable("productBean");
        }
    }

    @Override
    public void initData() {
        super.initData();

        kungViewModel = new ViewModelProvider(getActivity()).get(KungViewModel.class);

        kungViewModel.getSelectType().observe(this, new Observer<ProductType>() {
            @Override
            public void onChanged(ProductType productType) {
                viewModel.productTypeName.set(productType.getTypeName());
            }
        });

        if (productBean != null) {
            viewModel.productBean = productBean;
            viewModel.productCode.set(viewModel.productBean.getProductCode());
            viewModel.productName.set(viewModel.productBean.getProductName());
            viewModel.productTypeName.set(viewModel.productBean.getTypeName());
            viewModel.productIngredients.set(viewModel.productBean.getIngredientContent());
            viewModel.productShelfLife.set(viewModel.productBean.getShelfLife());
            viewModel.productStorageMethod.set(viewModel.productBean.getStorageMethod());
            viewModel.productEdibleMethod.set(viewModel.productBean.getEdibleMethod());
            viewModel.productAttention.set(viewModel.productBean.getPrecautions());
            viewModel.productStandard.set(viewModel.productBean.getStandard());
            viewModel.productLicense.set(viewModel.productBean.getProductionLicense());
            viewModel.productManufacturer.set(viewModel.productBean.getManufacturer());
            viewModel.productAddress.set(viewModel.productBean.getAddress());
            viewModel.productOrigin.set(viewModel.productBean.getOrigin());
            viewModel.productContact.set(viewModel.productBean.getPhone());
            //获取营养成分
            viewModel.funGetNutrition();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.productBean = null;
        viewModel.productCode.set("");
        viewModel.productName.set("");
        viewModel.productTypeName.set("");
        viewModel.productIngredients.set("");
        viewModel.productShelfLife.set("");
        viewModel.productStorageMethod.set("");
        viewModel.productEdibleMethod.set("");
        viewModel.productAttention.set("");
        viewModel.productStandard.set("");
        viewModel.productLicense.set("");
        viewModel.productManufacturer.set("");
        viewModel.productAddress.set("");
        viewModel.productOrigin.set("");
        viewModel.productContact.set("");
        if (viewModel.nutritionList != null) {
            viewModel.nutritionList.clear();
        }
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();

        viewModel.uc.eventToBack.observe(this, o -> {
            Navigation.findNavController(getView()).navigateUp();
        });

        viewModel.uc.eventSave.observe(this, o -> {
            ConformDialog.Builder builder = new ConformDialog.Builder(getActivity());
            builder.setMessage("是否保存该产品？", "");
            builder.setTitle("保存产品");
            builder.setPositiveButton("确认",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog2,
                                            int which) {
                            dialog2.dismiss();
                            viewModel.funSaveProduct();
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
        });

        viewModel.uc.eventSelectType.observe(this, o -> {
            Bundle bundle = new Bundle();
            bundle.putString("operationType", "select");
            Navigation.findNavController(getView()).navigate(R.id.productTypeFrag, bundle);
        });

        viewModel.uc.eventAddNutrition.observe(this, o -> {
            int defaultSort = viewModel.getSort();
            //弹框可以输入：营养成分名称、营养成分、含量、排序4个字段
            MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                    .title("添加营养成分")
                    .customView(R.layout.dialog_input_fields, true)
                    .positiveText("确定")
                    .negativeText("取消")
                    .autoDismiss(false)
                    .onPositive((dialog1, which) -> {
                        //添加营养成分
                        EditText etNutritionName = (EditText) dialog1.findViewById(R.id.et_nutrition_name);
                        EditText etNutritionContent = (EditText)dialog1.findViewById(R.id.et_nutrition);
                        EditText etNutritionAmount = (EditText)dialog1.findViewById(R.id.et_content);
                        EditText etNutritionSort = (EditText)dialog1.findViewById(R.id.et_sort);
                        String nutritionName = etNutritionName.getText().toString();
                        String nutritionContent = etNutritionContent.getText().toString();
                        String nutritionAmount = etNutritionAmount.getText().toString();
                        String nutritionSort = etNutritionSort.getText().toString();
                        ProductNutrition productNutrition = new ProductNutrition();
                        productNutrition.setName(nutritionName);
                        productNutrition.setContent(nutritionContent);
                        productNutrition.setNrv(nutritionAmount);
                        productNutrition.setSort(Integer.parseInt(nutritionSort));
                        boolean result = viewModel.addNutritionToList(productNutrition);
                        //如果成功关闭弹框
                        if (result) {
                            dialog1.dismiss();
                        }
                    })
                    .onNegative((dialog12, which) -> {
                        dialog12.dismiss();
                    })
                    .build();
            EditText etNutritionSort = (EditText)dialog.findViewById(R.id.et_sort);
            etNutritionSort.setText(String.valueOf(defaultSort));
            dialog.show();
        });

        viewModel.uc.eventEditNutrition.observe(this, new Observer<ProductNutrition>() {
            @Override
            public void onChanged(ProductNutrition productNutrition) {
                //弹框可以输入：营养成分名称、营养成分、含量、排序4个字段
                MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                        .title("编辑营养成分")
                        .customView(R.layout.dialog_input_fields, true)
                        .positiveText("确定")
                        .negativeText("取消")
                        .onPositive((dialog1, which) -> {
                            //编辑营养成分
                            EditText etNutritionName = (EditText) dialog1.findViewById(R.id.et_nutrition_name);
                            EditText etNutritionContent = (EditText)dialog1.findViewById(R.id.et_nutrition);
                            EditText etNutritionAmount = (EditText)dialog1.findViewById(R.id.et_content);
                            EditText etNutritionSort = (EditText)dialog1.findViewById(R.id.et_sort);
                            String nutritionName = etNutritionName.getText().toString();
                            String nutritionContent = etNutritionContent.getText().toString();
                            String nutritionAmount = etNutritionAmount.getText().toString();
                            String nutritionSort = etNutritionSort.getText().toString();
                            productNutrition.setName(nutritionName);
                            productNutrition.setContent(nutritionContent);
                            productNutrition.setNrv(nutritionAmount);
                            productNutrition.setSort(Integer.parseInt(nutritionSort));
                            viewModel.editNutritionToList(productNutrition);
                        })
                        .build();
                //设置默认值
                EditText etNutritionName = (EditText)dialog.findViewById(R.id.et_nutrition_name);
                etNutritionName.setText(productNutrition.getName());
                EditText etNutritionContent = (EditText)dialog.findViewById(R.id.et_nutrition);
                etNutritionContent.setText(productNutrition.getContent());
                EditText etNutritionAmount = (EditText)dialog.findViewById(R.id.et_content);
                etNutritionAmount.setText(productNutrition.getNrv());
                EditText etNutritionSort = (EditText)dialog.findViewById(R.id.et_sort);
                etNutritionSort.setText(String.valueOf(productNutrition.getSort()));
                dialog.show();
            }
        });

    }
}
