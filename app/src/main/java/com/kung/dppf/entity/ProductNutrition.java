package com.kung.dppf.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ProductNutrition {
    @Id(autoincrement = true)
    private Long _id;

    private String productCode; //产品编码
    //名称
    private String name; //名称
    //每100g含量
    private String content; //每100g含量
    //NRV%
    private String nrv; //NRV%
    //排序
    private int sort; //排序

    @Generated(hash = 27720084)
    public ProductNutrition() {
    }
    @Generated(hash = 1255610030)
    public ProductNutrition(Long _id, String productCode, String name,
            String content, String nrv, int sort) {
        this._id = _id;
        this.productCode = productCode;
        this.name = name;
        this.content = content;
        this.nrv = nrv;
        this.sort = sort;
    }
    public Long get_id() {
        return this._id;
    }
    public void set_id(Long _id) {
        this._id = _id;
    }
    public String getProductCode() {
        return this.productCode;
    }
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getNrv() {
        return this.nrv;
    }
    public void setNrv(String nrv) {
        this.nrv = nrv;
    }
    public int getSort() {
        return this.sort;
    }
    public void setSort(int sort) {
        this.sort = sort;
    }

}
