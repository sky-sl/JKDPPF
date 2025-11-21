package com.kung.dppf.ui.main.fragment;


import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
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
import com.kung.dppf.databinding.FragProductTypeBinding;
import com.kung.dppf.entity.ProductBean;
import com.kung.dppf.entity.ProductType;
import com.kung.dppf.entity.database.SqlManager;
import com.kung.dppf.ui.main.KungViewModel;
import com.kung.dppf.widget.ConformDialog;

import me.goldze.mvvmhabit.base.BaseFragment;

public class ProductTypeFrag extends BaseFragment<FragProductTypeBinding, ProductTypeModel> {

    private String mOperationType = "";
    private KungViewModel kungViewModel;
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.frag_product_type;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public ProductTypeModel initViewModel() {
        AppViewModelFactory factory = AppViewModelFactory.getInstance(getActivity().getApplication());
        return ViewModelProviders.of(getActivity(), factory).get(ProductTypeModel.class);
    }

    @Override
    public void initParam() {
        super.initParam();
        Bundle mBundle = getArguments();
        if (mBundle != null) {
            //获取传递的实体类(产品信息
            mOperationType = mBundle.getString("operationType");
        }
    }

    @Override
    public void initData() {
        super.initData();
        kungViewModel = new ViewModelProvider(getActivity()).get(KungViewModel.class);
        if ("select".equals(mOperationType)) {
            binding.tvAdd.setVisibility(View.INVISIBLE);
            viewModel.mOperationType = "select";
        }
        viewModel.resetData();
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();

        viewModel.uc.eventBack.observe(this, o -> {
            Navigation.findNavController(getView()).navigateUp();
        });

        //监听下拉刷新完成
        viewModel.uc.finishRefreshing.observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                viewModel.funQueryProductTypeList();
                //结束刷新
                binding.recyclingRL.finishRefreshing();
            }
        });
        //监听上拉加载完成
        viewModel.uc.finishLoadMore.observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                //结束刷新
                viewModel.funQueryProductTypeList();
                binding.recyclingRL.finishLoadmore();
            }
        });

        viewModel.uc.eventDeleteItem.observe(this, new Observer<ProductType>() {
            @Override
            public void onChanged(ProductType productBean) {
                ConformDialog.Builder builder = new ConformDialog.Builder(getActivity());
                builder.setMessage("是否删除该品类？", "");
                builder.setTitle("删除品类");
                builder.setPositiveButton("确认",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog2,
                                                int which) {
                                dialog2.dismiss();
                                SqlManager.deleteProductTypeBean(productBean);
                                viewModel.resetData();
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

        viewModel.uc.eventModifyItem.observe(this, new Observer<ProductType>() {
            @Override
            public void onChanged(ProductType productType) {
                new MaterialDialog.Builder(getActivity())
                        .title("修改品类")
                        .input("请输入品类", productType.getTypeName(), (dialog1, input) -> {
                            //输入框内容改变时回调
                        })
                        .positiveText("确定")
                        .onPositive((dialog12, which) -> {
                            //确定
                            productType.setTypeName(dialog12.getInputEditText().getText().toString());
                            SqlManager.insertOrReplaceProductTypeBean(productType);
                            viewModel.resetData();
                        })
                        .negativeText("取消")
                        .onNegative((dialog13, which) -> {
                            //取消
                        })
                        .show();
            }
        });

        viewModel.uc.eventSelectItem.observe(this, new Observer<ProductType>() {
            @Override
            public void onChanged(ProductType productType) {
                kungViewModel.setSelectType(productType);
                Navigation.findNavController(getView()).navigateUp();
            }
        });

        viewModel.uc.eventToProductTypeAdd.observe(this, aVoid -> {
            //弹出带输入框的对话框
            new MaterialDialog.Builder(getActivity())
                    .title("请输入产品类型")
                    .input("请输入产品类型", "", (dialog1, input) -> {
                        //输入框内容改变时回调
                    })
                    .positiveText("确定")
                    .onPositive((dialog12, which) -> {
                        //确定
                        viewModel.funSaveProductType(dialog12.getInputEditText().getText().toString());
                    })
                    .negativeText("取消")
                    .onNegative((dialog13, which) -> {
                        //取消
                    })
                    .show();

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
                                viewModel.resetData();
                            }
                        })
                        .positiveText("确定")
                        .show();
            }
        });
    }
}
