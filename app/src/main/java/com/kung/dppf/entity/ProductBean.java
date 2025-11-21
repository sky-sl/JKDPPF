package com.kung.dppf.entity;

import android.graphics.drawable.Drawable;

import com.kung.dppf.R;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

import me.goldze.mvvmhabit.utils.Utils;

@Entity
public class ProductBean implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id(autoincrement = true)
    private Long _id;

    @Unique
    private String ProductCode; //产品编码
    private String ProductName; //产品名称
    private String TypeName; //分类名称
    //配料表
    private String IngredientContent; //配料表
    //保质期
    private String ShelfLife; //保质期
    //贮存方法
    private String StorageMethod; //贮存方法
    //食用方法
    private String EdibleMethod; //食用方法
    //注意事项
    private String Precautions; //注意事项
    //执行标准
    private String Standard; //执行标准
    //生产许可证编号
    private String ProductionLicense; //生产许可证编号
    //生产厂家
    private String Manufacturer; //生产厂家
    //地址
    private String Address; //地址
    //委托商
    private String Entrust; //委托商
    //委托商地址
    private String EntrustAddress; //委托商地址
    //产地
    private String Origin; //产地
    //电话
    private String Phone; //电话
    //修改日期
    private String updateTime;

    @Transient
    private String index;
    @Transient
    private String indexName;
    @Transient
    private boolean isSelected = false; //是否被选中
    @Transient
    private int textColorName = Utils.getContext().getResources().getColor(R.color.trash_type_normal);
    @Transient
    private int textColorPrice = Utils.getContext().getResources().getColor(R.color.trash_type_price_normal);
    @Transient
    private Drawable bgDrawable = Utils.getContext().getResources().getDrawable(R.drawable.shape_trash_type_corner, null);



    @Generated(hash = 1619075705)
    public ProductBean(Long _id, String ProductCode, String ProductName, String TypeName, String IngredientContent,
            String ShelfLife, String StorageMethod, String EdibleMethod, String Precautions, String Standard,
            String ProductionLicense, String Manufacturer, String Address, String Entrust, String EntrustAddress,
            String Origin, String Phone, String updateTime) {
        this._id = _id;
        this.ProductCode = ProductCode;
        this.ProductName = ProductName;
        this.TypeName = TypeName;
        this.IngredientContent = IngredientContent;
        this.ShelfLife = ShelfLife;
        this.StorageMethod = StorageMethod;
        this.EdibleMethod = EdibleMethod;
        this.Precautions = Precautions;
        this.Standard = Standard;
        this.ProductionLicense = ProductionLicense;
        this.Manufacturer = Manufacturer;
        this.Address = Address;
        this.Entrust = Entrust;
        this.EntrustAddress = EntrustAddress;
        this.Origin = Origin;
        this.Phone = Phone;
        this.updateTime = updateTime;
    }
    @Generated(hash = 669380001)
    public ProductBean() {
    }

    
    
    public Long get_id() {
        return this._id;
    }
    public void set_id(Long _id) {
        this._id = _id;
    }
    public String getProductCode() {
        return this.ProductCode;
    }
    public void setProductCode(String ProductCode) {
        this.ProductCode = ProductCode;
    }
    public String getProductName() {
        return this.ProductName;
    }
    public void setProductName(String ProductName) {
        this.ProductName = ProductName;
    }

//    public String getSingleWeightG() {
//
//        if (StringUtils.isTrimEmpty(SingleWeight)) {
//            return SingleWeight;
//        }
//        //将SingleWeight的kg转换为g,并保留2位小数
//        return String.format("%.2f", Float.parseFloat(SingleWeight) * 1000);
//    }
//
//    public void setSingleWeightG(String singleWeightG) {
//        SingleWeightG = singleWeightG;
//    }

    public String getUpdateTime() {
        return this.updateTime;
    }
    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getTextColorName() {
        return textColorName;
    }

    public void setTextColorName(int textColorName) {
        this.textColorName = textColorName;
    }

    public int getTextColorPrice() {
        return textColorPrice;
    }

    public void setTextColorPrice(int textColorPrice) {
        this.textColorPrice = textColorPrice;
    }

    public Drawable getBgDrawable() {
        return bgDrawable;
    }

    public void setBgDrawable(Drawable bgDrawable) {
        this.bgDrawable = bgDrawable;
    }
    public String getIngredientContent() {
        return this.IngredientContent;
    }
    public void setIngredientContent(String IngredientContent) {
        this.IngredientContent = IngredientContent;
    }
    public String getShelfLife() {
        return this.ShelfLife;
    }
    public void setShelfLife(String ShelfLife) {
        this.ShelfLife = ShelfLife;
    }
    public String getStorageMethod() {
        return this.StorageMethod;
    }
    public void setStorageMethod(String StorageMethod) {
        this.StorageMethod = StorageMethod;
    }
    public String getEdibleMethod() {
        return this.EdibleMethod;
    }
    public void setEdibleMethod(String EdibleMethod) {
        this.EdibleMethod = EdibleMethod;
    }
    public String getPrecautions() {
        return this.Precautions;
    }
    public void setPrecautions(String Precautions) {
        this.Precautions = Precautions;
    }
    public String getStandard() {
        return this.Standard;
    }
    public void setStandard(String Standard) {
        this.Standard = Standard;
    }
    public String getProductionLicense() {
        return this.ProductionLicense;
    }
    public void setProductionLicense(String ProductionLicense) {
        this.ProductionLicense = ProductionLicense;
    }
    public String getAddress() {
        return this.Address;
    }
    public void setAddress(String Address) {
        this.Address = Address;
    }
    public String getOrigin() {
        return this.Origin;
    }
    public void setOrigin(String Origin) {
        this.Origin = Origin;
    }
    public String getPhone() {
        return this.Phone;
    }
    public void setPhone(String Phone) {
        this.Phone = Phone;
    }
    public String getTypeName() {
        return this.TypeName;
    }
    public void setTypeName(String TypeName) {
        this.TypeName = TypeName;
    }
    public String getManufacturer() {
        return this.Manufacturer;
    }
    public void setManufacturer(String Manufacturer) {
        this.Manufacturer = Manufacturer;
    }
    public String getEntrust() {
        return this.Entrust;
    }
    public void setEntrust(String Entrust) {
        this.Entrust = Entrust;
    }
    public String getEntrustAddress() {
        return this.EntrustAddress;
    }
    public void setEntrustAddress(String EntrustAddress) {
        this.EntrustAddress = EntrustAddress;
    }
}
