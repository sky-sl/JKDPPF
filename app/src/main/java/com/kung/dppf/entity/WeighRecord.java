package com.kung.dppf.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

@Entity
public class WeighRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id(autoincrement = true)
    private Long _id;

    private String ProductCode; //产品编码
    @Index
    private String ProductName; //产品名称
    //分类
    private String TypeCode; //分类编码
    @Index
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
    private String Manufacturer;
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
    //营养成分表内容
    private String NutritionContent; //营养成分表 json字符串
    //净含量
    private String NetWeight;
    //生产日期
    private String ProductionDate;
    //创建日期
    @Index
    private String createTime;  //创建日期 格式 yyyy-MM-dd HH:mm:ss

    @Transient
    private String index;
    @Transient
    private String indexName;
    @Transient
    private String qrCode;

    @Generated(hash = 933096808)
    public WeighRecord(Long _id, String ProductCode, String ProductName,
            String TypeCode, String TypeName, String IngredientContent,
            String ShelfLife, String StorageMethod, String EdibleMethod,
            String Precautions, String Standard, String ProductionLicense,
            String Manufacturer, String Address, String Entrust,
            String EntrustAddress, String Origin, String Phone,
            String NutritionContent, String NetWeight, String ProductionDate,
            String createTime) {
        this._id = _id;
        this.ProductCode = ProductCode;
        this.ProductName = ProductName;
        this.TypeCode = TypeCode;
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
        this.NutritionContent = NutritionContent;
        this.NetWeight = NetWeight;
        this.ProductionDate = ProductionDate;
        this.createTime = createTime;
    }
    @Generated(hash = 1796081477)
    public WeighRecord() {
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

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
    public String getTypeCode() {
        return this.TypeCode;
    }
    public void setTypeCode(String TypeCode) {
        this.TypeCode = TypeCode;
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
    public String getNutritionContent() {
        return this.NutritionContent;
    }
    public void setNutritionContent(String NutritionContent) {
        this.NutritionContent = NutritionContent;
    }
    public String getNetWeight() {
        return this.NetWeight;
    }
    public void setNetWeight(String NetWeight) {
        this.NetWeight = NetWeight;
    }
    public String getProductionDate() {
        return this.ProductionDate;
    }
    public void setProductionDate(String ProductionDate) {
        this.ProductionDate = ProductionDate;
    }
    public String getCreateTime() {
        return this.createTime;
    }
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
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
